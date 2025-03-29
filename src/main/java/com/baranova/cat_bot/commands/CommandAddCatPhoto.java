package com.baranova.cat_bot.commands;

import java.util.Map;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.UserService;
import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;

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
