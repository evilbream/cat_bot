package com.baranova.tg_service.commands;

import java.util.Map;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.tg_service.constants.UserMessage;

public class CommandAddCatPhoto extends AbsCommand {
    private String commandText;

    public CommandAddCatPhoto(UserService userService, UserDTO user, String commandText) {
        super(userService, user);
        this.commandText = commandText;
    }

    public Sendable execute() {
        if (commandText == null) { // photo passed no text command
            user.setState(Commands.ADD_CAT_NAME.getCommandName());
            userService.saveUser(user);
            return new Sendable.Builder()
                    .chatId(user.getId())
                    .message(UserMessage.MESSAGE_ADD_CAT_NAME)
                    .buttonsPerRow(1)
                    .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        }
        if (commandText.equals(MessageCallback.ADD_CAT_ASK_NAME)) {
            return new Sendable.Builder()
                    .chatId(user.getId())
                    .message(UserMessage.MESSAGE_ADD_CAT_PHOTO)
                    .buttonsPerRow(1)
                    .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();
        }


        return null;
    }
}
