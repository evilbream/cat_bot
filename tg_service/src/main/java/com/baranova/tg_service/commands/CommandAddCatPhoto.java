package com.baranova.tg_service.commands;

import java.util.Map;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.shared.constants.UserMessage;

public class CommandAddCatPhoto extends AbsCommand {
    private String commandText;

    public CommandAddCatPhoto(UserService userService, UserDTO user, String commandText, KeyboardService keyboardService) {
        super(userService, user, keyboardService);
        this.commandText = commandText;
    }

    public Sendable execute() {
        if (commandText == null) { // photo passed no text command
            user.setState(Commands.ADD_CAT_NAME.getCommandName());
            userService.saveUser(user);
            return Sendable.builder()
                    .chatId(user.getId().toString())
                    .message(UserMessage.MESSAGE_ADD_CAT_NAME)
                    .myCatsMap(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        }
        if (commandText.equals(MessageCallback.MENU)) return this.toMainMenu();
        if (commandText.equals(MessageCallback.ADD_CAT_ASK_NAME)) {
            return Sendable.builder()
                    .chatId(user.getId().toString())
                    .message(UserMessage.MESSAGE_ADD_CAT_PHOTO)
                    .myCatsMap(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();
        }


        return null;
    }
}
