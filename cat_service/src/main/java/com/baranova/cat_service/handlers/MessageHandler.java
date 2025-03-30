package com.baranova.cat_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.service.UserContextService;
import com.baranova.cat_service.constants.UserMessage;
import com.baranova.cat_service.commands.CommandFactory;
import com.baranova.cat_service.commands.CommandInterface;

@RequiredArgsConstructor
@Component
public class MessageHandler {

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CommandFactory commandFactory;

    public Sendable handleTextMessage(String messageText, Long chatId, String username) {

        UserDTO user = userContextService.getContext(chatId);

        if (messageText.equals(UserMessage.COMMAND_START)) {
            user.setUsername(username);
            user.setState(Commands.START.getCommandName());
        }


        CommandInterface comamnd = commandFactory.createCommand(user, messageText);
        return comamnd.execute();

    }

}
