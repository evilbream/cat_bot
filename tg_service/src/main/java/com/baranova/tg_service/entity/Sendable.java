package com.baranova.tg_service.entity;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Sendable implements Serializable {
    private static final long serialVersionUID = 1L;
    private String chatId;
    private String username;
    private String state;
    private String message;
    private String callbackData;
    private Map<String, String> buttons;

    @Builder.Default
    private int buttonsPerRow = 3;
    private byte[] photo;
    private String photoName;
    private String photoId;
    @Builder.Default
    private Integer myCatPage = 0;
    @Builder.Default
    private Integer viewCatPage = 0;


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
}
