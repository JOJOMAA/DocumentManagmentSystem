package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public DocumentEntity savePdf(String name, MultipartFile file) throws Exception {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(name);
        documentEntity.setContent(file.getBytes());
        return documentRepository.save(documentEntity);
    }

    public DocumentEntity getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dokument nicht gefunden"));
    }

    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }
}
