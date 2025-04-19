package com.baranova.tg_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.baranova.shared.entity.Sendable;

@Service
public class MessageSender {
    private final Logger logger = LoggerFactory.getLogger(MessageSender.class);
    private final TelegramLongPollingBot bot;
    @Autowired
    private ElementsFactory elementsFactory;

    @Autowired
    public MessageSender(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void execute(Sendable sendable) {
        if (sendable.getPhoto() != null) {
            send(elementsFactory.composePhotoMessage(sendable));
        } else {
            send(elementsFactory.composeMessage(sendable));
        }

    }

    private void send(SendMessage msg) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Failed to send photo", e);
        }
    }

    private void send(SendPhoto msg) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Failed to send photo", e);
        }
    }
}
