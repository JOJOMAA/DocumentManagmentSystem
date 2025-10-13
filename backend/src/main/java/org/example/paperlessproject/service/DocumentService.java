package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.paperlessproject.messaging.OcrPublisher;
import org.example.paperlessproject.messaging.DocumentUploadedEvent;

import java.io.IOException;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private OcrPublisher ocrPublisher;

    public DocumentEntity savePdf(String name, MultipartFile file) throws Exception {
        log.info("Attempting to save document: {}", name);

        try {
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setName(name);

            documentEntity.setContent(file.getBytes());
            log.debug("File content read successfully, size: {} bytes", file.getSize());

            DocumentEntity saved = documentRepository.save(documentEntity);
            log.info("Document saved successfully with ID: {}", saved.getId());

            DocumentUploadedEvent event = new DocumentUploadedEvent(
                    saved.getId(),
                    saved.getName(),
                    "local",
                    "none",
                    Instant.now()
            );
            ocrPublisher.publish(event);
            log.info("OCR event published for document ID: {}", saved.getId());
            return saved;
        } catch (IOException e) {
            log.error("Failed to read file content for document: {}", name, e);
            throw new Exception("Fehler beim Lesen der Datei", e);
        } catch (Exception e) {
            log.error("Failed to save document: {}", name, e);
            throw e;
        }
    }

    public DocumentEntity getById(Long id) {
        log.debug("Fetching document with ID: {}", id);

        return documentRepository.findById(id)
                .map(doc -> {
                    log.debug("Document found: ID={}, Name={}", doc.getId(), doc.getName());
                    return doc;
                })
                .orElseThrow(() -> {
                    log.warn("Document not found with ID: {}", id);
                    return new RuntimeException("Dokument nicht gefunden");
                });
    }

    public List<DocumentEntity> getAllDocuments() {
        log.debug("Fetching all documents");

        try {
            List<DocumentEntity> documents = documentRepository.findAll();
            log.info("Retrieved {} documents", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Error fetching all documents", e);
            throw e;
        }
    }

    public void deleteDocument(Long id) {
        log.info("Attempting to delete document with ID: {}", id);

        try {
            if (!documentRepository.existsById(id)) {
                log.warn("Cannot delete - document not found with ID: {}", id);
                throw new RuntimeException("Dokument nicht gefunden");
            }

            documentRepository.deleteById(id);
            log.info("Document deleted successfully: ID={}", id);

        } catch (Exception e) {
            log.error("Failed to delete document with ID: {}", id, e);
            throw e;
        }
    }
}
