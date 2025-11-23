package org.example.paperlessproject.messaging;

import lombok.Data;

@Data
public class GenAiResultMessage {
    private Long documentId;
    private String summary;
}
