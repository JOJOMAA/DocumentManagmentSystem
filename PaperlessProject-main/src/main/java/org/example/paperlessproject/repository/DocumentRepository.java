package org.example.paperlessproject.repository;

import org.example.paperlessproject.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}
