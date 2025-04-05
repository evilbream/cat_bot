package com.baranova.tg_service.commands;

import java.util.Optional;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.dto.converter.SendableConverter;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;


public class CommandMyCats extends AbsCommand {

    private String commandText;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandMyCats(UserService userService, UserDTO user, String commandText, RabbitMQProducer rabbitMQProducerService) {
        super(userService, user);
        this.commandText = commandText;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }


    public Sendable execute() {
        if (!user.getState().equals(Commands.MY_CATS.getCommandName())) return null;
        if (commandText.equals(MessageCallback.BACK)) return back();
        if (commandText.equals(MessageCallback.NEXT_PAGE)) return changePage();
        if (commandText.equals(MessageCallback.PREVIOUS_PAGE)) return changePage();
        if (commandText.equals(MessageCallback.MENU)) return toMainMenu();

        rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                .state(user.getState())
                .chatId(user.getId().toString())
                .callbackData(commandText)
                .myCatPage(user.getMyCatPage())
                .message(commandText)
                .username(user.getUsername())
                .build()));


        return null;
    }

    public Sendable back() {
        user.setMyCatPage(0);
        this.commandText = MessageCallback.MY_CATS;
        return execute();
    }

    public Sendable changePage() {
        Integer page = Optional.ofNullable(user.getMyCatPage()).orElse(0);

        if (commandText.equals(MessageCallback.NEXT_PAGE)) {
            page++;
            user.setMyCatPage(page);
            this.commandText = MessageCallback.MY_CATS;
            return execute();
        }

        if (commandText.equals(MessageCallback.PREVIOUS_PAGE)) {
            if (page == 0) return null;
            page--;
            user.setMyCatPage(page);
            this.commandText = MessageCallback.MY_CATS;
            return execute();
        }

        return null;
    }

}