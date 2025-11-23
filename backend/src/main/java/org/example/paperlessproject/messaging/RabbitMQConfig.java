package org.example.paperlessproject.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue ocrQueue(@Value("${paperless.queues.ocr}") String name) {
        return new Queue(name, true);
    }

    @Bean
    Queue resultQueue(@Value("${paperless.queues.result}") String name) {
        return new Queue(name, true);
    }

    @Bean
    Jackson2JsonMessageConverter jackson() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(jackson());
        return t;
    }

    @Value("${GENAI_REQUEST_QUEUE}")
    private String genAiRequestQueue;

    @Value("${GENAI_RESPONSE_QUEUE}")
    private String genAiResponseQueue;

    @Bean
    public Queue genAiRequestQueue() {
        return new Queue(genAiRequestQueue, true);
    }

    @Bean
    public Queue genAiResponseQueue() {
        return new Queue(genAiResponseQueue, true);
    }

}
