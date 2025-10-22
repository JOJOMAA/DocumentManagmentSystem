package org.example.paperlessproject.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String minioKey;

    // Use PostgreSQL text type (removing @Lob avoids CLOB mapping issues with UPPER)
    @Column(name = "ocr_text", columnDefinition = "text")
    private String ocrText;

    public DocumentEntity(Long id, String name, String minioKey) {
        this.id = id;
        this.name = name;
        this.minioKey = minioKey;
        this.ocrText = null;
    }
}
