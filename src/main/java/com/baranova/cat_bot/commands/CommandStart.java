package com.baranova.cat_bot.commands;

import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;
import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.UserService;


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