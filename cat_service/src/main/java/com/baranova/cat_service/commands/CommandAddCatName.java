package com.baranova.cat_service.commands;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.converters.SendableConverter;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.constants.UserMessage;
import com.baranova.cat_service.service.KeyboardService;

public class CommandAddCatName extends AbsCommand {
    private String commandText;
    private PhotoService photoService;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandAddCatName(Sendable sendable, PhotoService photoService, RabbitMQProducer rabbitMQProducerService, KeyboardService keyboardService) {
        super(sendable, keyboardService);
        this.photoService = photoService;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.commandText = sendable.getCallbackData() == null ? sendable.getMessage() : sendable.getCallbackData();
    }

    public Sendable execute() {
        if (!sendable.getState().equals(Commands.ADD_CAT_NAME.getCommandName())) return null;
        else if (commandText.startsWith(MessageCallback.REDO)) return this.redo();
        else if (commandText.startsWith(MessageCallback.CONFIRM)) return this.save();
        else return executePhoto();
    }

    public Sendable executePhoto() {
        Long chatId = Long.parseLong(sendable.getChatId());
        CatDTO photo = CatDTO.builder()
                .author(chatId)
                .username(sendable.getUsername())
                .catName(commandText)
                .uploadedAt(LocalDateTime.now())
                .photo(sendable.getPhoto())
                .build();

        String caption = "Имя: " + commandText + "\n" + "Автор: " + sendable.getUsername();
        byte[] catPhotoBytes = photo.getPhoto();

        return Sendable.builder()
                .chatId(chatId.toString())
                .photo(catPhotoBytes)
                .message(caption)
                .photoName(commandText)
                .buttonsPerRow(2)
                .buttons(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_CONFIRM, MessageCallback.CONFIRM,
                        UserMessage.BUTTON_REDO, MessageCallback.REDO,
                        UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU)))
                .build();

    }

    public Sendable redo() {
        return Sendable.builder()
                .chatId(sendable.getChatId())
                .message(UserMessage.MESSAGE_ADD_CAT_PHOTO)
                .buttonsPerRow(1)
                .state(Commands.ADD_CAT_PHOTO.getCommandName())
                .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                .build();
    }

    public Sendable save() {
        rabbitMQProducerService.sendAsync(SendableConverter.toJson(Sendable.builder()
                .state(Commands.START.getCommandName())
                .chatId(sendable.getChatId())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build()));

        CatDTO photo = CatDTO.builder()
                .author(Long.parseLong(sendable.getChatId()))
                .username(sendable.getUsername())
                .catName(sendable.getPhotoName())
                .uploadedAt(LocalDateTime.now())
                .photo(sendable.getPhoto())
                .build();

        Long photoID = photoService.savePhoto(Long.parseLong(sendable.getChatId()), photo);
        String message = "Котик " + sendable.getPhotoName() + " успешно сохранен!";
        if (photoID == null) message = "Не удалось сохранить котика " + sendable.getPhotoName();
        return Sendable.builder()
                .chatId(sendable.getChatId())
                .message(message)
                .build();

    }


}
