package org.example.paperlessproject.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.paperlessproject.repository.DocumentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class OcrResultListener {

    private final DocumentRepository repository;

    @RabbitListener(queues = "${paperless.queues.result}")
    public void handle(DocumentOcrResult result) {
        repository.findById(result.id()).ifPresent(doc -> {
            doc.setOcrText(result.text());
            repository.save(doc);
            log.info("OCR text stored id={}", result.id());
        });
    }
}
