package com.baranova.tg_service.commands;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.shared.constants.UserMessage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbsCommand implements CommandInterface {

    protected UserService userService;
    protected UserDTO user;
    protected KeyboardService keyboardService;

    public Sendable toMainMenu() {
        user.setState(Commands.START.getCommandName());
        userService.saveUser(user);
        return Sendable.builder()
                .chatId(user.getId().toString())
                .message(UserMessage.MESSAGE_START)
                .myCatsMap(MessageCallback.START_BUTTONS)
                .build();
    }

}
