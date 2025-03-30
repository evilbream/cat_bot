package com.baranova.cat_service.handlers;

import org.springframework.stereotype.Component;

import com.baranova.cat_service.entity.Sendable;

@Component
public class ErrorHandler {
    public Sendable handleError(Long chatId, String errorMessage) {
        return new Sendable.Builder()
                .chatId(chatId)
                .message(errorMessage)
                .build();
    }
}
