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
    private final String exchangeName;
    private final String routingKey;

    public RabbitMQProducer(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.exchange.name_send}") String exchangeName,
            @Value("${spring.rabbitmq.routing.key_send}")String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        log.info("Message sent to RabbitMQ: " + message);
    }

    @Async
    public void sendAsync(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        log.info("Message sent asynchrnously to RabbitMQ: " + message);
    }
}