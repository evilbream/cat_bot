package com.baranova.cat_service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key_produce}")
    private String produceRoutingKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchangeName, produceRoutingKey, message);
        log.info("Message sent to RabbitMQ: " + message);
    }

    @Async
    public void sendAsync(String message) {
        rabbitTemplate.convertAndSend(exchangeName, produceRoutingKey, message);
        log.info("Message sent to RabbitMQ: " + message);
    }
}