package org.example.paperlessproject;

import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.messaging.OcrPublisher;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.example.paperlessproject.service.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration"
})
class PaperlessProjectApplicationTests {

    @MockBean DocumentRepository documentRepository;
    @MockBean ElasticSearchRepository elasticSearchRepository;

    // weil DocumentService auch diese braucht:
    @MockBean OcrPublisher ocrPublisher;
    @MockBean MinioService minioService;

    // weil DocumentController sonst nicht gebaut werden kann:
    @MockBean DocumentMapper documentMapper;

    @Test
    void contextLoads() { }
}
