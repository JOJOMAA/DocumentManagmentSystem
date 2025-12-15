package org.example.paperlessproject.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrPublisherTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    void publish_shouldSendPayloadToConfiguredQueue() {
        // given
        String queue = "paperless.ocr.queue";
        OcrPublisher publisher = new OcrPublisher(rabbitTemplate, queue);

        Object payload = new DocumentUploadedEvent(1L, "a.pdf", "pdfs", "minio-key", java.time.Instant.now());

        // when
        publisher.publish(payload);

        // then
        verify(rabbitTemplate).convertAndSend(queue, payload);
        verifyNoMoreInteractions(rabbitTemplate);
    }
}
