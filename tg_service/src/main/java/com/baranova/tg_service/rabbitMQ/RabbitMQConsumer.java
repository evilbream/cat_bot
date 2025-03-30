package com.baranova.tg_service.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.tg_service.converter.SendableConverter;
import com.baranova.tg_service.utils.Utils;


@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private Utils utils;

    @RabbitListener(queues = "#{@consumerQueueName}")
    public void receiveMessage(String message) {
        try {
            log.info("onMessage received new message from queue", message);
            utils.execute(SendableConverter.fromJson(message));
        } catch (Exception e) {
            log.error("Error on  message: " + message + "Error: " + e.getMessage());
        }
    }


}