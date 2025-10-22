// backend/src/main/java/org/example/paperlessproject/service/DocumentService.java
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
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private OcrPublisher ocrPublisher;
    @Autowired
    private MinioService minioService;

    public DocumentEntity savePdf(String name, MultipartFile file) throws Exception {
        log.info("Attempting to save document: {}", name);

        String minioKey = minioService.uploadFile(name, file);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setMinioKey(minioKey);

        DocumentEntity saved = documentRepository.save(documentEntity);
        log.info("Document saved successfully with ID: {}", saved.getId());

        DocumentUploadedEvent event = new DocumentUploadedEvent(
                saved.getId(),
                saved.getName(),
                "pdfs",
                minioKey,
                Instant.now()
        );
        ocrPublisher.publish(event);
        log.info("OCR event published for document ID: {}", saved.getId());
        return saved;
    }

    public DocumentEntity getById(Long id) {
        log.debug("Fetching document with ID: {}", id);

        return documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Document not found with ID: {}", id);
                    return new RuntimeException("Dokument nicht gefunden");
                });
    }

    public byte[] getPdfContent(Long id) throws Exception {
        DocumentEntity doc = getById(id);
        return minioService.downloadFile(doc.getMinioKey());
    }

    public List<DocumentEntity> getAllDocuments() {
        log.debug("Fetching all documents");
        return documentRepository.findAll();
    }

    public void deleteDocument(Long id) throws Exception {
        log.info("Attempting to delete document with ID: {}", id);

        DocumentEntity doc = getById(id);
        minioService.deleteFile(doc.getMinioKey());
        documentRepository.deleteById(id);
        log.info("Document deleted successfully: ID={}", id);
    }
}
