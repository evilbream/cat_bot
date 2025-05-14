package com.baranova.cat_service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import com.baranova.cat_service.service.MessageController;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.shared.dto.converter.SendableConverter;

@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    MessageController messageController;


    @RabbitListener(queues = "${spring.rabbitmq.queue.name_consume}")
    public void receiveMessage(String message) {
        try {
            log.info("Received message: " + message);
            messageController.processMessage((SendableConverter.fromJson(message)));
        } catch (Exception e) {
            log.error("Error on  message: " + message + "Error: " + e.getMessage());
        }

    }
}