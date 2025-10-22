package org.example.paperlessproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.paperlessproject.dto.DocumentDto;
import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.log4j.Log4j2;
import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper mapper;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadPdf(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws Exception {
        DocumentEntity saved = documentService.savePdf(name, file);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getMeta(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(documentService.getById(id)));
    }

    @GetMapping("/list")
    public ResponseEntity<List<DocumentDto>> getDocuments() {
        List<DocumentDto> dtoList = documentService.getAllDocuments().stream()
                .map(mapper::toDto).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws Exception {
        DocumentEntity doc = documentService.getById(id);
        byte[] content = documentService.getPdfContent(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getName() + ".pdf\"")
                .header("Content-Type", "application/pdf")
                .body(content);
    }

    @GetMapping("/{id}/text")
    public ResponseEntity<String> getOcrText(@PathVariable Long id) {
        DocumentEntity doc = documentService.getById(id);
        return ResponseEntity.ok(doc.getOcrText() == null ? "" : doc.getOcrText());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDto>> search(@RequestParam("q") String q) {
        List<DocumentDto> dto = documentService.searchByOcrText(q).stream()
                .map(mapper::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws Exception {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
