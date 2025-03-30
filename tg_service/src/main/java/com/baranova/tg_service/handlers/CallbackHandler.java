package com.baranova.tg_service.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.tg_service.entity.Sendable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CallbackHandler {

    public Sendable handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        return new Sendable
                .Builder()
                .callbackData(callbackData)
                .chatId(chatId)
                .username(update.getCallbackQuery().getFrom().getUserName())
                .build();
    }


}
