package org.example.paperlessproject.controller;

import org.example.paperlessproject.dto.DocumentDto;
import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired MockMvc mvc;

    @MockBean DocumentService documentService;
    @MockBean DocumentMapper mapper;

    @Test
    void uploadPdf_acceptsMultipart_returnsDto_andCallsServiceAndMapper() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "PDF".getBytes()
        );

        DocumentEntity saved = new DocumentEntity();
        saved.setId(42L);
        saved.setName("Invoice");
        saved.setMinioKey("minio-key-123");

        DocumentDto dto = new DocumentDto();
        dto.setId(42L);
        dto.setName("Invoice");
        dto.setMinioKey("minio-key-123");

        when(documentService.savePdf(eq("Invoice"), any())).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        // when/then
        mvc.perform(multipart("/documents/upload")
                        .file(file)
                        .param("name", "Invoice"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Invoice"))
                .andExpect(jsonPath("$.minioKey").value("minio-key-123"));

        verify(documentService).savePdf(eq("Invoice"), any());
        verify(mapper).toDto(saved);
    }

    @Test
    void uploadPdf_missingName_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "PDF".getBytes()
        );

        mvc.perform(multipart("/documents/upload")
                        .file(file))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(documentService, mapper);
    }

    @Test
    void uploadPdf_missingFile_returns400() throws Exception {
        mvc.perform(multipart("/documents/upload")
                        .param("name", "Invoice"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(documentService, mapper);
    }

    @Test
    void getMeta_returns200_withDto() throws Exception {
        // given
        DocumentEntity doc = new DocumentEntity();
        doc.setId(7L);
        doc.setName("Doc");
        doc.setMinioKey("key-7");

        DocumentDto dto = new DocumentDto();
        dto.setId(7L);
        dto.setName("Doc");
        dto.setMinioKey("key-7");

        when(documentService.getById(7L)).thenReturn(doc);
        when(mapper.toDto(doc)).thenReturn(dto);

        // when/then
        mvc.perform(get("/documents/7"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Doc"))
                .andExpect(jsonPath("$.minioKey").value("key-7"));

        verify(documentService).getById(7L);
        verify(mapper).toDto(doc);
    }

    @Test
    void getDocuments_returnsListOfDtos() throws Exception {
        DocumentEntity a = new DocumentEntity(); a.setId(1L);
        DocumentEntity b = new DocumentEntity(); b.setId(2L);

        DocumentDto da = new DocumentDto(); da.setId(1L);
        DocumentDto db = new DocumentDto(); db.setId(2L);

        when(documentService.getAllDocuments()).thenReturn(List.of(a, b));
        when(mapper.toDto(a)).thenReturn(da);
        when(mapper.toDto(b)).thenReturn(db);

        mvc.perform(get("/documents/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(documentService).getAllDocuments();
        verify(mapper).toDto(a);
        verify(mapper).toDto(b);
    }

    @Test
    void downloadPdf_returnsPdfBytes_andHeaders() throws Exception {
        Long id = 10L;

        DocumentEntity doc = new DocumentEntity();
        doc.setId(id);
        doc.setName("invoice");
        when(documentService.getById(id)).thenReturn(doc);
        when(documentService.getPdfContent(id)).thenReturn(new byte[]{1,2,3});

        mvc.perform(get("/documents/download/10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"invoice.pdf\""))
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().bytes(new byte[]{1,2,3}));

        verify(documentService).getById(id);
        verify(documentService).getPdfContent(id);
    }

    @Test
    void getOcrText_returnsEmptyString_whenNull() throws Exception {
        DocumentEntity doc = new DocumentEntity();
        doc.setId(1L);
        doc.setOcrText(null);

        when(documentService.getById(1L)).thenReturn(doc);

        mvc.perform(get("/documents/1/text"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(documentService).getById(1L);
    }

    @Test
    void search_callsService_mapsDtos_returnsJsonArray() throws Exception {
        DocumentEntity a = new DocumentEntity(); a.setId(1L);
        DocumentDto da = new DocumentDto(); da.setId(1L);

        when(documentService.searchByOcrText("hello")).thenReturn(List.of(a));
        when(mapper.toDto(a)).thenReturn(da);

        mvc.perform(get("/documents/search").param("q", "hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(documentService).searchByOcrText("hello");
        verify(mapper).toDto(a);
    }

    @Test
    void delete_returns204_andCallsService() throws Exception {
        doNothing().when(documentService).deleteDocument(5L);

        mvc.perform(delete("/documents/5"))
                .andExpect(status().isNoContent());

        verify(documentService).deleteDocument(5L);
    }
}
