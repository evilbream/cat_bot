package com.baranova.tg_service.rabbitMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.tg_service.services.MessageServiceContrller;


@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private MessageServiceContrller catServiceContrller;

    @RabbitListener(queues = "#{@consumerQueueName}")
    public void receiveMessage(String message) {
        try{
        log.info("onMessage received new message from queue", message);
        catServiceContrller.processMessage(SendableConverter.fromJson(message));
    } catch (Exception e) {
        log.error("Error on  message: " + message + "Error: " + e.getMessage());
    }
    }


}