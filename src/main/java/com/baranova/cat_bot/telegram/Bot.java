package com.baranova.cat_bot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.baranova.cat_bot.telegram.handlers.EventHandler;

@Component
public class Bot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final EventHandler eventHandler;
    private final String token;
    private final String name;

    @Autowired
    public Bot(
            @Value("${telegram.token}") String token,
            @Value("${telegram.name}") String name,
            EventHandler eventHandler) {

        super(token);
        this.token = token;
        this.name = name;
        this.eventHandler = eventHandler;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info(update.toString());
        SendMessage message = eventHandler.handleUpdate(update, this);
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message", e);
                ;
            }
        }
    }

}
