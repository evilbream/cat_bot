package com.baranova.tg_service.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import com.baranova.tg_service.entity.Sendable;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Service
public class ElementsFactory {

    public SendPhoto composePhotoMessage(Sendable sendable) {
        return SendPhoto
                .builder()
                .chatId(sendable.getChatId())
                .photo(new InputFile(new ByteArrayInputStream(sendable.getPhoto()), "cat.jpg"))
                .caption(sendable.getMessage())
                .replyMarkup(getKeyboard(sendable.getButtons(), sendable.getButtonsPerRow()))
                .build();

    }

    public SendMessage composeMessage(Sendable sendable) {
        if (sendable.getButtons() == null) {
            return SendMessage
                    .builder()
                    .chatId(sendable.getChatId())
                    .text(sendable.getMessage())
                    .build();
        }
        return SendMessage
                .builder()
                .chatId(sendable.getChatId())
                .text(sendable.getMessage())
                .replyMarkup(getKeyboard(sendable.getButtons(), sendable.getButtonsPerRow()))
                .build();

    }

    public InlineKeyboardMarkup getKeyboard(Map<String, String> buttons, int buttonsPerRow) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(entry.getKey())
                    .callbackData(entry.getValue())
                    .build();

            row.add(button);
            if (row.size() == buttonsPerRow) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }

}
