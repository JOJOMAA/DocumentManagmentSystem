package org.example.paperlessproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.paperlessproject.dto.DocumentDto;
import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper mapper;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadPdf (
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws Exception {
        DocumentEntity saved = documentService.savePdf(name, file);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getMeta(@PathVariable Long id) {
        DocumentEntity entity = documentService.getById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @GetMapping("/list")
    public ResponseEntity<List<DocumentDto>> getDocuments() {
        List<DocumentEntity> documentList = documentService.getAllDocuments();

        List<DocumentDto> dtoList = documentList.stream()
                .map(mapper::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        DocumentEntity doc = documentService.getById(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getName() + ".pdf\"")
                .body(doc.getContent());
    }
}
