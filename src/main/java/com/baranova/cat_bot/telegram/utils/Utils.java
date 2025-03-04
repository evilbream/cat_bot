package com.baranova.cat_bot.telegram.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.baranova.cat_bot.entity.Sendable;

@Service
public class Utils {
    private final Logger logger = LoggerFactory.getLogger(Utils.class);

    @Autowired
    private ElementsFactory elementsFactory;


    public void execute(Sendable sendable, TelegramLongPollingBot bot) {
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
            logger.error("Failed to send photo", e);
        }
    }

    private void send(SendPhoto msg, TelegramLongPollingBot bot) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Failed to send photo", e);
        }
    }
}
