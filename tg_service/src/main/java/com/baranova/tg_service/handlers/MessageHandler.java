package com.baranova.tg_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.baranova.tg_service.entity.Sendable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.UserContextService;
import com.baranova.tg_service.commands.CommandInterface;
import com.baranova.tg_service.constants.UserMessage;
import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.commands.CommandFactory;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageHandler {

    @Autowired
    private CommandFactory commandFactory;

    @Autowired
    private UserContextService userContextService;

    public Sendable handleTextMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        UserDTO user = userContextService.getContext(chatId, update.getMessage().getFrom().getUserName());

        if (messageText.equals(UserMessage.COMMAND_START)) {
            user.setUsername(update.getMessage().getFrom().getUserName());
            user.setState(Commands.START.getCommandName());
        }

        CommandInterface comamnd = commandFactory.createCommand(user, messageText);
        Sendable sendable = comamnd.execute();
        return sendable;

    }

    ;


}
