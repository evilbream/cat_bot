package com.baranova.tg_service.commands;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.shared.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;

import java.util.List;
import java.util.LinkedHashMap;

import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.shared.constants.UserMessage;

public class CommandAddCatName extends AbsCommand {
    private String commandText;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandAddCatName(UserService userService, UserDTO user, String commandText, RabbitMQProducer rabbitMQProducerService, KeyboardService keyboardService) {
        super(userService, user, keyboardService);
        this.commandText = commandText;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    public Sendable execute() {
        if (!user.getState().equals(Commands.ADD_CAT_NAME.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MENU)) return this.toMainMenu();
        if (commandText.startsWith(MessageCallback.REDO)) return this.redo();
        if (commandText.startsWith(MessageCallback.CONFIRM)) return this.save();
        return executePhoto();
    }

    public Sendable executePhoto() {
        user.setCurrentPhotoName(commandText);
        String caption = "Имя: " + commandText + "\n" + "Автор: " + user.getUsername();
        byte[] catPhotoBytes = user.getCurrentPhoto();

        return Sendable.builder()
                .chatId(user.getId().toString())
                .photo(catPhotoBytes)
                .message(caption)
                .photoName(commandText)
                .myCatsMap(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_CONFIRM, MessageCallback.CONFIRM,
                        UserMessage.BUTTON_REDO, MessageCallback.REDO,
                        UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU)))
                .build();

    }


    public Sendable back() {
        user.clearCurrentPhoto();
        return Sendable.builder()
                .chatId(user.getId().toString())
                .state(Commands.START.getCommandName())
                .message(UserMessage.MESSAGE_START)
                .myCatsMap(MessageCallback.START_BUTTONS)
                .build();
    }

    public Sendable redo() {
        user.clearCurrentPhoto();
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put(UserMessage.BUTTON_ADD_CAT_ASK_NAME, MessageCallback.ADD_CAT_ASK_NAME);
        return Sendable.builder()
                .chatId(user.getId().toString())
                .message(UserMessage.MESSAGE_ADD_CAT_PHOTO)
                .state(Commands.ADD_CAT_PHOTO.getCommandName())
                .myCatsMap(buttons)
                .build();
    }

    public Sendable save() {
        rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                .state(user.getState())
                .chatId(user.getId().toString())
                .username(user.getUsername())
                .command("save")
                .photo(user.getCurrentPhoto())
                .photoName(user.getCurrentPhotoName())
                .build()));
        user.clearCurrentPhoto();
        return null;
    }

}
