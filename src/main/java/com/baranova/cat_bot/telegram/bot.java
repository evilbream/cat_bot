package com.baranova.cat_bot.telegram;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@NoArgsConstructor
public class bot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(bot.class);

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.name}")
    private String name;

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
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            logger.info("Received message from chatId: " + chatId + " with text: " + messageText);
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }
    }

}
