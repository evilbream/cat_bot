package com.baranova.tg_service.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.tg_service.converter.SendableConverter;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;


@Component
public class EventHandler {
    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private CallbackHandler callBackHandler;

    @Autowired
    private PhotoHandler photoHandler;

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;

    public void handleUpdate(Update update, TelegramLongPollingBot bot) {
        logger.info(update.toString());
        Sendable mqMesage = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            mqMesage = messageHandler.handleTextMessage(update, bot);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            mqMesage = photoHandler.handlePhoto(update, bot);
        } else if (update.hasCallbackQuery()) {
            mqMesage = callBackHandler.handleCallback(update);
        }
        if (mqMesage != null) rabbitMQProducerService.sendMessage(SendableConverter.toJson(mqMesage));
    }


}
