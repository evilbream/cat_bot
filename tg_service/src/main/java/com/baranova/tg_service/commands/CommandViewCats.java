package com.baranova.tg_service.commands;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.shared.entity.Sendable;
import com.baranova.shared.enums.CatActions;
import com.baranova.shared.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;

import java.util.LinkedHashMap;

import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.shared.constants.UserMessage;

public class CommandViewCats extends AbsCommand {

    private String commandText;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandViewCats(UserService userService, UserDTO user, String commandText, RabbitMQProducer rabbitMQProducerService, KeyboardService keyboardService) {
        super(userService, user, keyboardService);
        this.commandText = commandText;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }


    public Sendable execute() {
        if (!user.getState().equals(Commands.VIEW_CATS.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MENU)) return toMainMenu();
        if (commandText.equals(MessageCallback.MENU)) return back();
        if (commandText.equals(MessageCallback.VIEW_CATS)) return executePhoto();
        if (commandText.startsWith("dis_") || commandText.startsWith("lik_")) return executePhoto();
        if (commandText.equals(MessageCallback.RESTART_VIEVING)) {
            user.setViewCatPage(0);
            this.commandText = MessageCallback.VIEW_CATS;
            return executePhoto();
        }

        return null;
    }

    private Sendable executePhoto() {
        String catAction = CatActions.VIEW_CATS.getActionName();
        String photoId = null;
        if (commandText.startsWith("dis_")) {
            catAction = CatActions.DISLIKE.getActionName();
            photoId = commandText.substring(4);
        } else if (commandText.startsWith("lik_")) {
            catAction = CatActions.LIKE.getActionName();
            photoId = commandText.substring(4);
        }

        if (user.getCurrentPhoto() == null) {
            rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                    .state(user.getState())
                    .chatId(user.getId().toString())
                    .myCatPage(user.getMyCatPage())
                    .viewCatPage(user.getViewCatPage())
                    .catAction(catAction)
                    .command(commandText)
                    .photoId(photoId)
                    .build()));
            return null;
        }

        LinkedHashMap<String, String> buttons = new LinkedHashMap<>(user.getMyCatsMap());
        buttons.put(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU);

        Sendable sendable = Sendable.builder()
                .chatId(user.getId().toString())
                .message(user.getCurrentPhotoName())
                .photo(user.getCurrentPhoto())
                .myCatsMap(buttons)
                .build();
        user.clearCurrentPhoto();
        return sendable;
    }


    public Sendable back() {
        return Sendable.builder()
                .chatId(user.getId().toString())
                .state(Commands.START.getCommandName())
                .message(UserMessage.MESSAGE_START)
                .myCatsMap(MessageCallback.START_BUTTONS)
                .build();
    }


}
