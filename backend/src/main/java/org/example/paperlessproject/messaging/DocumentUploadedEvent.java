package org.example.paperlessproject.messaging;

import java.time.Instant;

public record DocumentUploadedEvent(
        Long id,
        String filename,
        String bucket,
        String objectKey,
        Instant uploadedAt
) {}