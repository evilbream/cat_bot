package com.baranova.tg_service.commands;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.dto.converter.SendableConverter;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;

public class CommandAddCatName extends AbsCommand {
    private String commandText;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandAddCatName(UserService userService, UserDTO user, String commandText, RabbitMQProducer rabbitMQProducerService) {
        super(userService, user);
        this.commandText = commandText;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    public Sendable execute() {
        if (!user.getState().equals(Commands.ADD_CAT_NAME.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MENU)) return this.toMainMenu();

        rabbitMQProducerService.sendMessage(SendableConverter.toJson(new Sendable.Builder()
                .state(user.getState())
                .chatId(user.getId())
                .callbackData(commandText)
                .message(commandText)
                .username(user.getUsername())
                .photo(user.getCurrentPhoto())
                .photoName(user.getCurrentPhotoName())
                .build()));
        return null;
    }

}
