package com.baranova.tg_service.commands;

import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.tg_service.constants.UserMessage;
import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.UserService;

public class CommandStart extends AbsCommand {

    public CommandStart(UserService userService, UserDTO user) {
        super(userService, user);
    }

    public Sendable execute() {
        String helloMessage = UserMessage.MESSAGE_START;
        user.setState(Commands.START.getCommandName());
        if (user.getNotRegistered() == true) {
            helloMessage = "Привет " + user.getUsername() + "!\n" + UserMessage.MESSAGE_START;
            userService.saveUser(user);
            user.setNotRegistered(false);
        }

        return new Sendable.Builder()
                .chatId(user.getId())
                .message(helloMessage)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build();
    }

}