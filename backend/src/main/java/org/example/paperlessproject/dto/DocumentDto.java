package org.example.paperlessproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DocumentDto {

    private Long id;
    private String name;
    private String minioKey;
    private String ocrText;
    private String summary;

    // Constructor for old tests without OCR text / summary
    public DocumentDto(Long id, String name, String minioKey) {
        this.id = id;
        this.name = name;
        this.minioKey = minioKey;
        this.ocrText = null;
        this.summary = null;
    }
}
