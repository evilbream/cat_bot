package com.baranova.cat_service.rabbitMQ;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import com.baranova.cat_service.handlers.EventHandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_service.dto.converters.SendableConverter;

@Slf4j
@Component
public class rabbitMQConsumer {

    @Autowired
    private EventHandler eventHandler;

    @PostConstruct
    public void onPostConstruct() {
        log.info("init consumer");
    }

    @RabbitListener(queues = "#{@consumerQueueName}")
    public void receiveMessage(String message) {
        try {
            log.info("Received message: " + message);
            eventHandler.handle(SendableConverter.fromJson(message));
        } catch (Exception e) {
            log.error("Error on  message: " + message + "Error: " + e.getMessage());
        }

    }
}