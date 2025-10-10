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
    private byte[] content;
    }
