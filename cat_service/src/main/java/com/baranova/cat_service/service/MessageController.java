package com.baranova.cat_service.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.shared.entity.Sendable;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageController {
    @Autowired
    private PhotoService photoService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;


    public void processMessage(Sendable sendable) {
        Sendable toUser = null;
        try {
            toUser = new CatController(sendable, photoService, reactionService, rabbitMQProducerService).execute();
            if (toUser == null) return;
        } catch (Exception e) {
            log.error("Sending user message with error. Error while handling event: " + e.getMessage());
            toUser = Sendable.builder()
                    .chatId(sendable.getChatId())
                    .message(e.getMessage())
                    .build();
        }

        rabbitMQProducerService.sendMessage(SendableConverter.toJson(toUser));
    }
}
