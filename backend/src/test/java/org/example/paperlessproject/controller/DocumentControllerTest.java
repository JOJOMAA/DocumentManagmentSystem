package org.example.paperlessproject.controller;

import org.springframework.test.web.servlet.MockMvc;
import org.example.paperlessproject.dto.DocumentDto;
import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired MockMvc mvc;

    @MockBean DocumentService documentService;
    @MockBean DocumentMapper mapper;

    @Test
    void getMeta_returnsDto() throws Exception {
        DocumentEntity e = new DocumentEntity(7L, "Contract", "minio-key-abc");
        given(documentService.getById(7L)).willReturn(e);

        DocumentDto dto = new DocumentDto(7L, "Contract", "minio-key-abc");
        given(mapper.toDto(e)).willReturn(dto);

        mvc.perform(get("/documents/7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Contract"))
                .andExpect(jsonPath("$.minioKey").value("minio-key-abc"));
    }

    @Test
    void download_returnsAttachment() throws Exception {
        DocumentEntity e = new DocumentEntity(5L, "TestPDF", "minio-key-xyz");
        byte[] bytes = "Hello".getBytes();
        given(documentService.getById(5L)).willReturn(e);
        given(documentService.getPdfContent(5L)).willReturn(bytes);

        mvc.perform(get("/documents/download/5"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"TestPDF.pdf\""))
                .andExpect(content().bytes(bytes));
    }
}
