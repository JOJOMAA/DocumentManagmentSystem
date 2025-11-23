package org.example.paperlessproject.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OcrResultListener {

    private final AmqpTemplate amqpTemplate;

    @Value("${GENAI_REQUEST_QUEUE}")
    private String genAiRequestQueue;

    @RabbitListener(queues = "${RESULT_QUEUE}")
    public void onOcrResult(DocumentOcrResult msg) {
        Long documentId = msg.id();
        String text = msg.text();

        // Basic validation
        if (documentId == null) {
            log.error("Received OCR result without documentId — cannot process.");
            return;
        }

        if (text == null || text.isBlank()) {
            log.warn("Received empty OCR text for documentId={}", documentId);
            return;
        }

        log.info("OCR result received for documentId={} (text length={})",
                documentId, text.length());

        // Build payload for GenAI worker
        Map<String, Object> genAiRequest = new HashMap<>();
        genAiRequest.put("documentId", documentId);
        genAiRequest.put("text", text);

        try {
            amqpTemplate.convertAndSend(genAiRequestQueue, genAiRequest);
            log.info("Sent GenAI request for documentId={} to queue={}",
                    documentId, genAiRequestQueue);
        } catch (Exception ex) {
            log.error("Failed to send GenAI request for documentId={} to queue={}: {}",
                    documentId, genAiRequestQueue, ex.getMessage(), ex);
        }
    }
}
