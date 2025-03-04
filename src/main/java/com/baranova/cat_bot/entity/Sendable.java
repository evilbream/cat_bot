package com.baranova.cat_bot.entity;

import java.util.Map;

import com.baranova.cat_bot.telegram.constants.MessageCallback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sendable {
    private String chatId;
    private String message;
    private Map<String, String> buttons;
    private int buttonsPerRow = MessageCallback.DEFAULT_BUTTONS_PER_ROW;
    private byte[] photo;


    public static class Builder {
        private String chatId;
        private String message;
        private Map<String, String> buttons;
        private int buttonsPerRow;
        private byte[] photo;

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId.toString();
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder buttons(Map<String, String> buttons) {
            this.buttons = buttons;
            return this;
        }

        public Builder buttonsPerRow(int buttonsPerRow) {
            this.buttonsPerRow = buttonsPerRow;
            return this;
        }

        public Builder photo(byte[] photo) {
            this.photo = photo;
            return this;
        }

        public Sendable build() {
            return new Sendable(chatId, message, buttons, buttonsPerRow, photo);
        }
    }
}
