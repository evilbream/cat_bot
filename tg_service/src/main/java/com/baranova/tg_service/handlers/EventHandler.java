package com.baranova.tg_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import lombok.extern.slf4j.Slf4j;
import com.baranova.shared.entity.Sendable;
import com.baranova.tg_service.utils.ElementsFactory;

@Slf4j
@Component
public class EventHandler {
    @Autowired
    ElementsFactory elementsFactory;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private CallbackHandler callBackHandler;
    @Autowired
    private PhotoHandler photoHandler;

    public void handleUpdate(Update update, TelegramLongPollingBot bot) {
        log.info(update.toString());
        Sendable sendable = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            sendable = messageHandler.handleTextMessage(update);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            sendable = photoHandler.handlePhoto(update, bot);
        } else if (update.hasCallbackQuery()) {
            sendable = callBackHandler.handleCallback(update);
        }

        if (sendable == null) return;

        if (sendable.getPhoto() != null) {
            send(elementsFactory.composePhotoMessage(sendable), bot);
        } else {
            send(elementsFactory.composeMessage(sendable), bot);
        }
    }

    private void send(SendMessage msg, TelegramLongPollingBot bot) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo", e);
        }
    }

    private void send(SendPhoto msg, TelegramLongPollingBot bot) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo", e);
        }
    }


}
