package com.baranova.cat_bot.telegram.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

import com.baranova.cat_bot.commands.CommandFactory;
import com.baranova.cat_bot.commands.CommandInterface;
import com.baranova.cat_bot.dto.CatDTO;
import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.dto.converters.UserConverter;
import com.baranova.cat_bot.entity.Photo;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.service.UserContextService;
import com.baranova.cat_bot.telegram.utils.Utils;


@Component
public class PhotoHandler {

    private final Logger logger = LoggerFactory.getLogger(PhotoHandler.class);

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CommandFactory commandFactory;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private Utils utils;

    public void handlePhoto(Update update, TelegramLongPollingBot bot) {

        PhotoSize photo = update
                .getMessage()
                .getPhoto()
                .stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        if (photo == null) return;

        Long chatId = update.getMessage().getChatId();
        UserDTO user = userContextService.getContext(chatId);

        if (user.getState().equals(Commands.ADD_CAT_PHOTO.getCommandName())) {
            GetFile getFileMethod = new GetFile(photo.getFileId());

            try {
                File fileInfo = bot.execute(getFileMethod);
                InputStream fileStream = bot.downloadFileAsStream(fileInfo);
                byte[] data = photoService.toBytes(fileStream);
                Photo newPhoto = new Photo(UserConverter.toEntity(user), data);
                CatDTO photoDTO = new CatDTO.Builder()
                        .id(newPhoto.getId())
                        .author(user.getId())
                        .username(user.getUsername())
                        .uploadedAt(LocalDateTime.now())
                        .photo(data)
                        .build();
                user.setCurrentPhoto(photoDTO);

                CommandInterface comamnd = commandFactory.createCommand(user, null);
                Sendable sendable = comamnd.execute();
                utils.execute(sendable, bot);
            } catch (TelegramApiException | IOException e) {
                logger.error("Error handlink photo in chat " + chatId.toString(), e);
            }
        }
    }

}
