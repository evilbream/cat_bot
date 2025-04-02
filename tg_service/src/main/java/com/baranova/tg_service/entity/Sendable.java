package com.baranova.tg_service.entity;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sendable implements Serializable {
    private static final long serialVersionUID = 1L;
    private String chatId;
    private String username;
    private String state;
    private String message;
    private String callbackData;
    private Map<String, String> buttons;
    private int buttonsPerRow = 3;
    private byte[] photo;
    private String photoName;
    private String photoId;
    private Integer myCatPage = 0;

    @Override
    public String toString() {
        return "Sendable{" +
                "chatId='" + chatId + '\'' +
                ", username='" + username + '\'' +
                ", state='" + state + '\'' +
                ", message='" + message + '\'' +
                ", callbackData='" + callbackData + '\'' +
                ", buttons=" + buttons +
                ", buttonsPerRow=" + buttonsPerRow +
                ", photo=" + (photo != null ? photo.length : 0) +
                ", photoName='" + photoName + '\'' +
                ", photoId='" + photoId + '\'' +
                ", myCatPage='" + myCatPage + '\'' +
                '}';
    }

    public static class Builder {
        private String chatId;
        private String username;
        private String state;
        private String message;
        private String callbackData;
        private Map<String, String> buttons;
        private int buttonsPerRow;
        private byte[] photo;
        private String photoName;
        private String photoId;
        private Integer myCatPage = 0;

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId.toString();
            return this;
        }

        public Builder callbackData(String callbackData) {
            this.callbackData = callbackData;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder photoName(String photoName) {
            this.photoName = photoName;
            return this;
        }

        public Builder photoId(String photoId) {
            this.photoId = photoId;
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

        public Builder myCatPage(Integer myCatPage) {
            this.myCatPage = myCatPage;
            return this;
        }

        public Sendable build() {
            return new Sendable(chatId, username, state, message, callbackData, buttons, buttonsPerRow, photo, photoName, photoId, myCatPage);
        }
    }
}
