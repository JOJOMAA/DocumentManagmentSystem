package org.example.paperlessproject.integration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class DocumentUploadIntegrationTest {

    @MockBean
    private ElasticSearchRepository elasticSearchRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("paperless")
            .withUsername("paperless")
            .withPassword("paperless");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withExposedPorts(5672, 15672);

    @Container
    static GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
            .withCommand("server /data")
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // WICHTIG: Das hier sorgt dafür, dass die Tabellen im Test-Container erstellt werden
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        // RabbitMQ
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        // MinIO
        registry.add("minio.url", () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
        registry.add("minio.access-key", () -> "minioadmin");
        registry.add("minio.secret-key", () -> "minioadmin");
        registry.add("minio.bucket", () -> "paperless-test-bucket");
    }

    @Test
    void testDocumentUpload_ShouldPersistInDbAndMinio() throws Exception {
        // 0. INIT: Bucket im MinIO Container erstellen (da er leer startet)
        MinioClient testClient = MinioClient.builder()
                .endpoint("http://" + minio.getHost() + ":" + minio.getMappedPort(9000))
                .credentials("minioadmin", "minioadmin")
                .build();

        if (!testClient.bucketExists(BucketExistsArgs.builder().bucket("paperless-test-bucket").build())) {
            testClient.makeBucket(MakeBucketArgs.builder().bucket("paperless-test-bucket").build());
        }

        // 1. Vorbereitung
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "integration-test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Hello Integration Test World".getBytes()
        );

        // 2. Ausführung
        mockMvc.perform(multipart("/documents/upload")
                        .file(file)
                        .param("name", "integration-test.pdf"))
                .andExpect(status().isOk());

        // 3. Prüfung DB
        List<DocumentEntity> docs = documentRepository.findAll();
        assertThat(docs).hasSize(1);
        DocumentEntity savedDoc = docs.get(0);

        assertThat(savedDoc.getName()).isEqualTo("integration-test.pdf");

        // 4. Prüfung MinIO (wir nutzen den Client von oben weiter)
        try (InputStream stream = testClient.getObject(
                GetObjectArgs.builder()
                        .bucket("paperless-test-bucket")
                        .object(savedDoc.getMinioKey())
                        .build())) {

            byte[] content = stream.readAllBytes();
            assertThat(new String(content)).isEqualTo("Hello Integration Test World");
        }
    }
}