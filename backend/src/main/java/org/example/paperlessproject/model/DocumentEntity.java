package org.example.paperlessproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String minioKey;

    // Full OCR text (PostgreSQL text type)
    @Column(name = "ocr_text", columnDefinition = "text")
    private String ocrText;

    // GenAI summary (stored after Gemini processing)
    @Column(length = 4000)
    private String summary;

    // Your custom constructor (keep it)
    public DocumentEntity(Long id, String name, String minioKey) {
        this.id = id;
        this.name = name;
        this.minioKey = minioKey;
        this.ocrText = null;
        this.summary = null;
    }
}
