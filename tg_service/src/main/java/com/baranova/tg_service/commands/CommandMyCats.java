package com.baranova.tg_service.commands;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.dto.converter.SendableConverter;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.tg_service.constants.UserMessage;

import java.util.Map;
import java.util.List;

public class CommandMyCats extends AbsCommand {

    private String commandText;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandMyCats(UserService userService, UserDTO user, String commandText, RabbitMQProducer rabbitMQProducerService, KeyboardService keyboardService) {
        super(userService, user, keyboardService);
        this.commandText = commandText;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }


    public Sendable execute() {
        if (!user.getState().equals(Commands.MY_CATS.getCommandName())) return null;
        if (commandText.equals(MessageCallback.BACK)) return back();
        if (commandText.equals(MessageCallback.NEXT_PAGE)) return changePage();
        if (commandText.equals(MessageCallback.PREVIOUS_PAGE)) return changePage();
        if (commandText.equals(MessageCallback.MENU)) return toMainMenu();
        if (commandText.startsWith("rem_")) return delete();
        if (commandText.startsWith("view_")) return composeMyCatPhoto();

        if (user.getMyCatsMap() == null) {
            rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                    .state(user.getState())
                    .chatId(user.getId().toString())
                    .myCatPage(user.getMyCatPage())
                    .message(commandText)
                    .username(user.getUsername())
                    .build()));
            return null;
        } else return composePages();

    }


    private Sendable composePages() {
        Integer page = user.getMyCatPage();
        if (page < 0) page = 0;
        LinkedHashMap<String, String> photos = new LinkedHashMap<>(user.getMyCatsMap());
        if ((photos == null) || (photos.isEmpty() && page == 0))
            return Sendable.builder()
                    .chatId(user.getId().toString())
                    .message(UserMessage.MESSAGE_NO_MY_CATS)
                    .myCatsMap(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        if (page > 0) photos.put(UserMessage.BUTTON_BACK, MessageCallback.PREVIOUS_PAGE);
        photos.put(UserMessage.BUTTON_NEXT, MessageCallback.NEXT_PAGE);
        photos.put(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU);

        return Sendable.builder()
                .chatId(user.getId().toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatPage(page)
                .message(UserMessage.MESSAGE_MY_CATS)
                .myCatsMap(photos)
                .build();
    }

    private Sendable composeMyCatPhoto() {
        if (user.getCurrentPhoto() == null) {
            rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                    .state(user.getState())
                    .chatId(user.getId().toString())
                    .myCatPage(user.getMyCatPage())
                    .message(commandText)
                    .username(user.getUsername())
                    .build()));
            return null;
        }

        String caption = user.getCurrentPhotoName();
        String removeCallback = "rem_" + user.getCurrentPhotoId();
        return Sendable.builder()
                .chatId(user.getId().toString())
                .photo(user.getCurrentPhoto())
                .message(caption)
                .myCatsMap(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_BACK, MessageCallback.BACK,
                        UserMessage.BUTTON_REMOVE, removeCallback)))
                .build();
    }

    public Sendable delete() {
        if (commandText.startsWith("rem_")) {
            rabbitMQProducerService.sendMessage(SendableConverter.toJson(Sendable.builder()
                    .state(user.getState())
                    .chatId(user.getId().toString())
                    .myCatPage(user.getMyCatPage())
                    .message(commandText)
                    .username(user.getUsername())
                    .build()));
        }
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