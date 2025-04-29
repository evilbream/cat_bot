package com.baranova.shared.entity;

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
    private String command;
    private Map<String, String> myCatsMap;
    private byte[] photo;
    private String photoName;
    private String catAction;
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
                ", photo=" + (photo != null ? photo.length : 0) +
                ", photoName='" + photoName + '\'' +
                ", photoId='" + photoId + '\'' +
                ", myCatPage='" + myCatPage + '\'' +
                '}';
    }
}

