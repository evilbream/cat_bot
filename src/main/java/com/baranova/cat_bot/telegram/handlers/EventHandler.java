package com.baranova.cat_bot.telegram.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class EventHandler {
    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private CallbackHandler callBackHandler;

    @Autowired
    private PhotoHandler photoHandler;

    public SendMessage handleUpdate(Update update, TelegramLongPollingBot bot) {
        logger.info(update.toString());
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageHandler.handleTextMessage(update, bot);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            photoHandler.handlePhoto(update, bot);
        } else if (update.hasCallbackQuery()) {
            callBackHandler.handleCallback(update, bot);
        }

        return null;
    }


}
