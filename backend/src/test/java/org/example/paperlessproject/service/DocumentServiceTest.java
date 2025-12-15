package org.example.paperlessproject.service;

import org.example.paperlessproject.messaging.DocumentUploadedEvent;
import org.example.paperlessproject.messaging.OcrPublisher;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.model.elastic.ElasticSearch;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock DocumentRepository documentRepository;
    @Mock ElasticSearchRepository elasticSearchRepository;
    @Mock OcrPublisher ocrPublisher;
    @Mock MinioService minioService;

    @InjectMocks DocumentService documentService;

    @Test
    void savePdf_uploadsToMinio_savesEntity_andPublishesEvent() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "PDF".getBytes()
        );

        when(minioService.uploadFile("Invoice", file)).thenReturn("minio-key-123");

        // return saved entity with id (so event has id)
        DocumentEntity saved = new DocumentEntity();
        saved.setId(42L);
        saved.setName("Invoice");
        saved.setMinioKey("minio-key-123");

        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);

        DocumentEntity out = documentService.savePdf("Invoice", file);

        assertNotNull(out);
        assertEquals(42L, out.getId());
        assertEquals("Invoice", out.getName());
        assertEquals("minio-key-123", out.getMinioKey());

        // verify order: upload -> save -> publish
        InOrder inOrder = inOrder(minioService, documentRepository, ocrPublisher);
        inOrder.verify(minioService).uploadFile("Invoice", file);
        inOrder.verify(documentRepository).save(any(DocumentEntity.class));
        inOrder.verify(ocrPublisher).publish(any(DocumentUploadedEvent.class));

        // verify event payload
        ArgumentCaptor<DocumentUploadedEvent> captor = ArgumentCaptor.forClass(DocumentUploadedEvent.class);
        verify(ocrPublisher).publish(captor.capture());

        DocumentUploadedEvent event = captor.getValue();
        assertEquals(42L, event.id());
        assertEquals("Invoice", event.filename());
        assertEquals("pdfs", event.bucket());
        assertEquals("minio-key-123", event.objectKey());
        assertNotNull(event.uploadedAt());;
    }

    @Test
    void savePdf_whenMinioUploadFails_shouldNotSaveOrPublish() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "PDF".getBytes()
        );

        when(minioService.uploadFile("Invoice", file))
                .thenThrow(new RuntimeException("MinIO down"));

        assertThrows(RuntimeException.class, () -> documentService.savePdf("Invoice", file));

        verify(documentRepository, never()).save(any());
        verify(ocrPublisher, never()).publish(any());
    }

    @Test
    void getPdfContent_downloadsBytesFromMinio() throws Exception {
        DocumentEntity e = new DocumentEntity();
        e.setId(1L);
        e.setName("Doc");
        e.setMinioKey("key-1");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(e));
        when(minioService.downloadFile("key-1")).thenReturn(new byte[]{1, 2, 3});

        byte[] out = documentService.getPdfContent(1L);

        assertArrayEquals(new byte[]{1, 2, 3}, out);
        verify(minioService).downloadFile("key-1");
    }

    @Test
    void deleteDocument_deletesFromMinio_thenDeletesFromRepository() throws Exception {
        DocumentEntity e = new DocumentEntity();
        e.setId(5L);
        e.setName("Doc");
        e.setMinioKey("key-5");

        when(documentRepository.findById(5L)).thenReturn(Optional.of(e));

        documentService.deleteDocument(5L);

        InOrder inOrder = inOrder(minioService, documentRepository);
        inOrder.verify(minioService).deleteFile("key-5");
        inOrder.verify(documentRepository).deleteById(5L);
    }

    @Test
    void deleteDocument_whenNotFound_shouldThrow_andNotDeleteAnything() throws Exception {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> documentService.deleteDocument(99L));

        verify(minioService, never()).deleteFile(anyString());
        verify(documentRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllDocuments_returnsRepositoryResult() {
        DocumentEntity a = new DocumentEntity();
        a.setId(1L);
        DocumentEntity b = new DocumentEntity();
        b.setId(2L);

        when(documentRepository.findAll()).thenReturn(List.of(a, b));

        List<DocumentEntity> out = documentService.getAllDocuments();

        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
        verify(documentRepository).findAll();
    }

    @Test
    void searchByOcrText_usesElastic_thenLoadsEntitiesByIds() {
        ElasticSearch s1 = new ElasticSearch();
        s1.setId(10L);
        ElasticSearch s2 = new ElasticSearch();
        s2.setId(11L);

        when(elasticSearchRepository.findByOcrTextContaining("hello"))
                .thenReturn(List.of(s1, s2));

        DocumentEntity d1 = new DocumentEntity();
        d1.setId(10L);
        DocumentEntity d2 = new DocumentEntity();
        d2.setId(11L);

        when(documentRepository.findAllById(List.of(10L, 11L)))
                .thenReturn(List.of(d1, d2));

        List<DocumentEntity> out = documentService.searchByOcrText("hello");

        assertEquals(2, out.size());
        verify(elasticSearchRepository).findByOcrTextContaining("hello");
        verify(documentRepository).findAllById(List.of(10L, 11L));
    }

    @Test
    void searchByOcrText_whenNoElasticResults_shouldQueryRepositoryWithEmptyList() {
        when(elasticSearchRepository.findByOcrTextContaining("nothing"))
                .thenReturn(List.of());

        when(documentRepository.findAllById(List.of())).thenReturn(List.of());

        List<DocumentEntity> out = documentService.searchByOcrText("nothing");

        assertNotNull(out);
        assertTrue(out.isEmpty());
        verify(documentRepository).findAllById(List.of());
    }
}
