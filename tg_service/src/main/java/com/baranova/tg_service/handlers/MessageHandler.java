package com.baranova.tg_service.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.tg_service.entity.Sendable;

import lombok.RequiredArgsConstructor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@RequiredArgsConstructor
@Component
public class MessageHandler {

    public Sendable handleTextMessage(Update update, TelegramLongPollingBot bot) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        return new Sendable
                .Builder()
                .chatId(chatId)
                .message(messageText)
                .username(update.getMessage().getFrom().getUserName())
                .build();
    }

}
