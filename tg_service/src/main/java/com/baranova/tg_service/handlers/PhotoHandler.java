package com.baranova.tg_service.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.services.PhotoService;


@Component
public class PhotoHandler {

    private final Logger logger = LoggerFactory.getLogger(PhotoHandler.class);


    @Autowired
    private PhotoService photoService;

    public Sendable handlePhoto(Update update, TelegramLongPollingBot bot) {

        PhotoSize photo = update
                .getMessage()
                .getPhoto()
                .stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        if (photo == null) return null;

        Long chatId = update.getMessage().getChatId();

        GetFile getFileMethod = new GetFile(photo.getFileId());

        try {
            File fileInfo = bot.execute(getFileMethod);
            InputStream fileStream = bot.downloadFileAsStream(fileInfo);
            byte[] data = photoService.toBytes(fileStream);
            return new Sendable
                    .Builder()
                    .chatId(chatId)
                    .photo(data)
                    .username(update.getMessage().getFrom().getUserName())
                    .build();
        } catch (TelegramApiException | IOException e) {
            logger.error("Error handlink photo in chat " + chatId.toString(), e);
        }
        return null;
    }
}


