package com.baranova.tg_service.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.tg_service.handlers.EventHandler;

@Component
public class Bot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String token;
    private final String name;
    private final EventHandler eventHandler;

    @Autowired
    public Bot(
            @Value("${telegram.token}") String token,
            @Value("${telegram.name}") String name,
            EventHandler eventHandler
    ) {

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
        eventHandler.handleUpdate(update, this);
    }
}

