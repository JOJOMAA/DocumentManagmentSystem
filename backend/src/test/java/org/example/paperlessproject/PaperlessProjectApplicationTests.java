package org.example.paperlessproject;

import org.example.paperlessproject.mapper.DocumentMapper;
import org.example.paperlessproject.messaging.OcrPublisher;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.example.paperlessproject.service.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration," +
                "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
})
class PaperlessProjectApplicationTests {

    @MockBean DocumentRepository documentRepository;
    @MockBean ElasticSearchRepository elasticSearchRepository;

    @MockBean OcrPublisher ocrPublisher;
    @MockBean MinioService minioService;

    @MockBean DocumentMapper documentMapper;

    @MockBean ConnectionFactory connectionFactory;
    @MockBean RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() { }
}
