package org.example.paperlessproject;

import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PaperlessProjectApplicationTests {

    @Mock
    DocumentRepository documentRepository;

    @Autowired
    DocumentService documentService;

    @Test
    void contextLoads() {
    }

}
