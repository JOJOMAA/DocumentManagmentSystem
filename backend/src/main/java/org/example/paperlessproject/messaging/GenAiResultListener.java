package org.example.paperlessproject.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class GenAiResultListener {

    private final DocumentRepository documentRepository;

    @RabbitListener(queues = "${GENAI_RESPONSE_QUEUE}")
    public void onGenAiResult(GenAiResultMessage msg) {
        Long documentId = msg.getDocumentId();
        String summary = msg.getSummary();

        if (documentId == null) {
            log.error("GenAI result without documentId – ignoring message");
            return;
        }

        documentRepository.findById(documentId).ifPresentOrElse(document -> {
            try {
                document.setSummary(summary);
                documentRepository.save(document);
                log.info("Saved GenAI summary for documentId={}", documentId);
            } catch (Exception e) {
                log.error("Failed to save GenAI summary for documentId={}", documentId, e);
            }
        }, () -> log.warn("Document with id={} not found for GenAI result", documentId));
    }
}
