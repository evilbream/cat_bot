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

import com.baranova.tg_service.commands.CommandFactory;
import com.baranova.tg_service.commands.CommandInterface;
import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.entity.Sendable;
import com.baranova.shared.enums.Commands;
import com.baranova.tg_service.services.PhotoService;
import com.baranova.tg_service.services.UserContextService;


@Component
public class PhotoHandler {

    private final Logger logger = LoggerFactory.getLogger(PhotoHandler.class);

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CommandFactory commandFactory;


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
        UserDTO user = userContextService.getContext(chatId, update.getMessage().getFrom().getUserName());
        if (user.getState().equals(Commands.ADD_CAT_PHOTO.getCommandName())) {
            GetFile getFileMethod = new GetFile(photo.getFileId());

            try {
                File fileInfo = bot.execute(getFileMethod);
                InputStream fileStream = bot.downloadFileAsStream(fileInfo);
                byte[] data = photoService.toBytes(fileStream);
                user.setCurrentPhoto(data);
                CommandInterface comamnd = commandFactory.createCommand(user, null);
                Sendable sendable = comamnd.execute();
                return sendable;


            } catch (TelegramApiException | IOException e) {
                logger.error("Error handlink photo in chat " + chatId.toString(), e);
            }
        }
        return null;
    }
}


