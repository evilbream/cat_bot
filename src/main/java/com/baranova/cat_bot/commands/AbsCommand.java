package com.baranova.cat_bot.commands;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.UserService;
import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbsCommand implements CommandInterface {
    protected UserService userService;
    protected UserDTO user;

    public Sendable toMainMenu() {
        user.setState(Commands.START.getCommandName());
        userService.saveUser(user);
        return new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build();
    }

}
