package com.baranova.cat_bot.telegram.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;

@Component
public class PhotoHandler {

    private final Logger logger = LoggerFactory.getLogger(PhotoHandler.class);


    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private PhotoService photoService;

    public SendMessage handleSavePhoto(Update update, TelegramLongPollingBot bot) {
        PhotoSize photo = update
                .getMessage()
                .getPhoto()
                .stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        if (photo == null) {
            return null;
        }
        String chatId = update.getMessage().getChatId().toString();
        GetFile getFileMethod = new GetFile(photo.getFileId());

        try {
            File fileInfo = bot.execute(getFileMethod);
            String fileExtension = fileInfo.getFilePath().substring(fileInfo.getFilePath().lastIndexOf("."));
            InputStream fileStream = bot.downloadFileAsStream(fileInfo);
            String filename = photoService.savePhoto(chatId, fileStream, fileExtension);

            if (filename != null) {
                return messageHandler.composePlainMessage(chatId, UserMessage.PHOTO_SAVED);
            }
        } catch (TelegramApiException | IOException e) {
            logger.error("Error saving photo in chat " + chatId, e);
            ;
        }
        return messageHandler.composePlainMessage(chatId, UserMessage.PHOTO_NOT_SAVED);

    }

    public void sendNextPhoto(String chatId, String filename, TelegramLongPollingBot bot) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(new java.io.File(filename)));

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>(List.of(
                InlineKeyboardButton.builder().text(UserMessage.BACK_BUTTON).callbackData(MessageCallback.BACK).build(),
                InlineKeyboardButton.builder().text(UserMessage.NEXT_BUTTON).callbackData(MessageCallback.SHOW_CATS).build()
        ));
        InlineKeyboardMarkup markupKeyboard = InlineKeyboardMarkup.builder().keyboard(List.of(keyboardButtonsRow1)).build();
        sendPhotoRequest.setReplyMarkup(markupKeyboard);
        try {
            bot.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            logger.error("Failed to send photo", e);
        }
    }

}
