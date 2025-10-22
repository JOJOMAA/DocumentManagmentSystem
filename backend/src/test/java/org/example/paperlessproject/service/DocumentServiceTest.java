package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.paperlessproject.messaging.OcrPublisher;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock DocumentRepository documentRepository;
    @Mock MinioService minioService;
    @Mock OcrPublisher ocrPublisher;
    @InjectMocks DocumentService documentService;

    @Test
    void getById_returnsEntityIfExists() {
        DocumentEntity e = new DocumentEntity(42L, "Invoice", "minio-key-123");
        when(documentRepository.findById(42L)).thenReturn(Optional.of(e));

        DocumentEntity out = documentService.getById(42L);

        assertSame(e, out);
        verify(documentRepository).findById(42L);
    }

    @Test
    void getById_throwsIfNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> documentService.getById(99L));
        verify(documentRepository).findById(99L);
    }
}
