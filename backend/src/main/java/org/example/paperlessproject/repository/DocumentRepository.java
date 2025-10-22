package org.example.paperlessproject.repository;

import org.example.paperlessproject.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByOcrTextContainingIgnoreCase(String q);
}
