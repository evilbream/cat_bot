package com.baranova.cat_service.commands;

import java.time.LocalDateTime;
import java.util.Map;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.dto.converters.SendableConverter;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.UserService;
import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.constants.UserMessage;

public class CommandAddCatName extends AbsCommand {
    private String commandText;
    private PhotoService photoService;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandAddCatName(UserService userService, UserDTO user, String commandText, PhotoService photoService, RabbitMQProducer rabbitMQProducerService) {
        super(userService, user);
        this.commandText = commandText;
        this.photoService = photoService;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    public Sendable execute() {
        if (!user.getState().equals(Commands.ADD_CAT_NAME.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MENU)) return this.toMainMenu();
        else if (commandText.equals(MessageCallback.REDO)) return this.redo();
        else if (commandText.equals(MessageCallback.CONFIRM)) return this.save();
        else return executePhoto();
    }


    public Sendable executePhoto() {
        CatDTO photo = new CatDTO.Builder()
                .id(user.getCurrentPhoto().getId())
                .author(user.getId())
                .username(user.getUsername())
                .catName(commandText)
                .uploadedAt(LocalDateTime.now())
                .photo(user.getCurrentPhoto().getPhoto())
                .build();

        String caption = "Имя: " + commandText + "\n" + "Автор: " + user.getUsername();
        user.setCurrentPhoto(photo);
        byte[] catPhotoBytes = photo.getPhoto();

        return new Sendable.Builder()
                .chatId(user.getId())
                .photo(catPhotoBytes)
                .message(caption)
                .buttonsPerRow(2)
                .buttons(Map.of(UserMessage.BUTTON_CONFIRM, MessageCallback.CONFIRM,
                        UserMessage.BUTTON_REDO, MessageCallback.REDO,
                        UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                .build();

    }

    public Sendable redo() {
        user.setState(Commands.ADD_CAT_PHOTO.getCommandName());
        userService.saveUser(user);
        return new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_ADD_CAT_PHOTO)
                .buttonsPerRow(1)
                .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                .build();

    }

    public Sendable save() {
        user.setState(Commands.START.getCommandName());
        rabbitMQProducerService.sendAsync(SendableConverter.toJson(new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build()));
        CatDTO photo = user.getCurrentPhoto();
        photoService.savePhoto(user.getId(), photo);
        userService.saveUser(user);
        return new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_PHOTO_SAVED)
                .build();

    }


}
