package org.example.paperlessproject.service;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock DocumentRepository documentRepository;
    @InjectMocks DocumentService documentService;

    @Test
    void savePdf_persistsEntity() throws Exception {
        MultipartFile file = new MockMultipartFile("file","test.pdf","application/pdf","Hello".getBytes());
        DocumentEntity saved = new DocumentEntity(1L,"TestPDF","Hello".getBytes());
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);

        DocumentEntity result = documentService.savePdf("TestPDF", file);

        assertEquals(1L, result.getId());
        assertEquals("TestPDF", result.getName());
        assertArrayEquals("Hello".getBytes(), result.getContent());
        verify(documentRepository).save(argThat(matches("TestPDF", "Hello".getBytes())));
    }

    @Test
    void getById_returnsEntityIfExists() {
        DocumentEntity e = new DocumentEntity(42L,"Invoice",new byte[]{1,2,3});
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

    private static ArgumentMatcher<DocumentEntity> matches(String name, byte[] content) {
        return e -> e.getId() == null && name.equals(e.getName()) && Arrays.equals(content, e.getContent());
    }
}
