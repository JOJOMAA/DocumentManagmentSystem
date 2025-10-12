package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.paperlessproject.messaging.OcrPublisher;
import org.example.paperlessproject.messaging.DocumentUploadedEvent;
import java.time.Instant;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private OcrPublisher ocrPublisher;

    public DocumentEntity savePdf(String name, MultipartFile file) throws Exception {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setContent(file.getBytes());
        DocumentEntity saved = documentRepository.save(documentEntity);

        DocumentUploadedEvent event = new DocumentUploadedEvent(
                saved.getId(),
                saved.getName(),
                "local",
                "none",
                Instant.now()
        );
        ocrPublisher.publish(event);

        return saved;
    }

    public DocumentEntity getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dokument nicht gefunden"));
    }

    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
