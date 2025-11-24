package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.example.paperlessproject.model.elastic.ElasticSearch;
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
    private ElasticSearchRepository elasticSearchRepository;
    @Autowired
    private OcrPublisher ocrPublisher;
    @Autowired
    private MinioService minioService;

    public DocumentEntity savePdf(String name, MultipartFile file) throws Exception {
        String minioKey = minioService.uploadFile(name, file);
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setMinioKey(minioKey);
        DocumentEntity saved = documentRepository.save(documentEntity);
        ocrPublisher.publish(new DocumentUploadedEvent(
                saved.getId(), saved.getName(), "pdfs", minioKey, Instant.now()
        ));
        return saved;
    }

    public DocumentEntity getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public byte[] getPdfContent(Long id) throws Exception {
        DocumentEntity doc = getById(id);
        return minioService.downloadFile(doc.getMinioKey());
    }

    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    public void deleteDocument(Long id) throws Exception {
        DocumentEntity doc = getById(id);
        minioService.deleteFile(doc.getMinioKey());
        documentRepository.deleteById(id);
    }

    public List<DocumentEntity> searchByOcrText(String query) {
        List<ElasticSearch> searchResults = elasticSearchRepository.findByOcrTextContaining(query);

        List<Long> ids = searchResults.stream().map(ElasticSearch::getId).toList();
        return documentRepository.findAllById(ids);
    }
}
