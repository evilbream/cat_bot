package com.baranova.tg_service.commands;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.tg_service.constants.UserMessage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbsCommand implements CommandInterface {
    protected UserService userService;
    protected UserDTO user;

    public Sendable toMainMenu() {
        user.setState(Commands.START.getCommandName());
        userService.saveUser(user);
        return Sendable.builder()
                .chatId(user.getId().toString())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build();
    }

}
