package com.baranova.cat_bot.telegram.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;

@Component
public class MessageHandler {

    public SendMessage handleTextMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (messageText.equals("/start")) {
            return composeStartMessage(chatId.toString());
        }
        return composePlainMessage(chatId.toString(), UserMessage.START_PROMPT);
    }

    public SendMessage composePlainMessage(String ChatId, String text) {
        return SendMessage.builder()
                .chatId(ChatId)
                .text(text)
                .build();
    }

    public SendMessage composeStartMessage(String chatID) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>(List.of(
                InlineKeyboardButton.builder().text(UserMessage.VIEW__MY_CATS_BUTTON).callbackData(MessageCallback.SHOW_CATS).build()
        ));
        InlineKeyboardMarkup markupKeyboard = InlineKeyboardMarkup.builder().keyboard(List.of(keyboardButtonsRow1)).build();
        return SendMessage.builder()
                .chatId(chatID)
                .text(UserMessage.START_MESSAGE)
                .replyMarkup(markupKeyboard)
                .build();
    }

}
