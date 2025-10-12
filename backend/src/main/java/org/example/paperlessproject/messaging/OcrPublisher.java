package org.example.paperlessproject.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OcrPublisher {
    private final RabbitTemplate tpl;
    private final String ocrQueue;

    public OcrPublisher(RabbitTemplate tpl, @Value("${paperless.queues.ocr}") String ocrQueue) {
        this.tpl = tpl; this.ocrQueue = ocrQueue;
    }
    public void publish(Object payload) { tpl.convertAndSend(ocrQueue, payload); }
}
