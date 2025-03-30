package com.baranova.cat_service.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_service.dto.converters.SendableConverter;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;


@Component
public class EventHandler {
    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private CallbackHandler callBackHandler;

    @Autowired
    private ErrorHandler errorHandler;

    @Autowired
    private PhotoHandler photoHandler;

    public void handle(Sendable sendable) {
        logger.info(sendable.toString());
        Sendable messageToUser = null;
        try {
            if (sendable.getMessage() != null) {
                messageToUser = messageHandler.handleTextMessage(sendable.getMessage(), Long.parseLong(sendable.getChatId()), sendable.getUsername());
            } else if (sendable.getCallbackData() != null) {
                messageToUser = callBackHandler.handleCallback(sendable.getCallbackData(), Long.parseLong(sendable.getChatId()));
            } else if (sendable.getPhoto() != null) {
                messageToUser = photoHandler.handlePhoto(Long.parseLong(sendable.getChatId()), sendable.getPhoto());
            }
        } catch (Exception e) {
            logger.error("Sending user message with error. Error while handling event: " + e.getMessage());
            messageToUser = errorHandler.handleError(Long.parseLong(sendable.getChatId()), e.getMessage());
        }

        if (messageToUser != null) rabbitMQProducerService.sendMessage(SendableConverter.toJson(messageToUser));

    }


}
