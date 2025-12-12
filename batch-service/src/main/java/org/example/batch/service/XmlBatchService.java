package org.example.batch.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.batch.dto.AccessLogsXml;
import org.example.batch.model.DailyAccessStat;
import org.example.batch.repository.DailyAccessStatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XmlBatchService {

    @Value("${batch.input.path}")
    private String inputPath;

    @Value("${batch.archive.path}")
    private String archivePath;

    private final DailyAccessStatRepository repository;
    private final XmlMapper xmlMapper = new XmlMapper();

    public XmlBatchService(DailyAccessStatRepository repository) {
        this.repository = repository;
        System.out.println(">>> XmlBatchService initialisiert. <<<");
        // Debugging beim Start
        debugPath("/data");
        debugPath("/data/input");
    }

    private void debugPath(String pathStr) {
        File f = new File(pathStr);
        System.out.println("DEBUG: Prüfe Pfad: " + f.getAbsolutePath());
        if (!f.exists()) {
            System.out.println("DEBUG: Pfad existiert NICHT.");
        } else {
            System.out.println("DEBUG: Pfad existiert. Ist Verzeichnis? " + f.isDirectory());
            String[] list = f.list();
            if (list != null) {
                System.out.println("DEBUG: Inhalt von " + pathStr + ": " + Arrays.toString(list));
            } else {
                System.out.println("DEBUG: Verzeichnis ist leer oder kein Zugriff.");
            }
        }
    }

    @Scheduled(cron = "${batch.schedule.cron}")
    public void processAccessLogs() {
        System.out.println("--- Scheduler Start ---");
        File folder = new File(inputPath);

        if (!folder.exists()) {
            System.err.println("CRITICAL: Input-Ordner " + inputPath + " fehlt!");
            debugPath("/data"); // Zeige Inhalt von /data
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (files == null || files.length == 0) {
            System.out.println("Keine XML-Dateien in " + inputPath + " gefunden.");
            return;
        }

        System.out.println("Gefunden: " + files.length + " Dateien.");

        for (File file : files) {
            try {
                processFile(file);
                archiveFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processFile(File file) throws IOException {
        System.out.println("Lese Datei: " + file.getAbsolutePath());
        AccessLogsXml logs = xmlMapper.readValue(file, AccessLogsXml.class);

        if (logs.getEntries() == null || logs.getEntries().isEmpty()) {
            System.out.println("Datei ist leer oder hat falsches Format.");
            return;
        }

        Map<Long, Long> countsPerDoc = logs.getEntries().stream()
                .collect(Collectors.groupingBy(
                        AccessLogsXml.Entry::getDocumentId,
                        Collectors.counting()
                ));

        countsPerDoc.forEach((docId, count) -> {
            DailyAccessStat stat = new DailyAccessStat(
                    docId,
                    LocalDateTime.now().toLocalDate(),
                    count
            );
            repository.save(stat);
            System.out.println("DB SAVED: DocID=" + docId + ", Count=" + count);
        });
    }

    private void archiveFile(File file) throws IOException {
        Path targetDir = Path.of(archivePath);
        if (!Files.exists(targetDir)) Files.createDirectories(targetDir);

        String newName = file.getName() + "." + System.currentTimeMillis() + ".processed";
        Files.move(file.toPath(), targetDir.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Archiviert: " + newName);
    }
}