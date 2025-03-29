package com.baranova.cat_bot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.UserContextService;
import com.baranova.cat_bot.telegram.constants.UserMessage;
import com.baranova.cat_bot.telegram.utils.Utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import com.baranova.cat_bot.commands.CommandFactory;
import com.baranova.cat_bot.commands.CommandInterface;

@RequiredArgsConstructor
@Component
public class MessageHandler {


    @Autowired
    private Utils utils;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CommandFactory commandFactory;

    public void handleTextMessage(Update update, TelegramLongPollingBot bot) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        UserDTO user = userContextService.getContext(chatId);

        if (messageText.equals(UserMessage.COMMAND_START)) {
            user.setUsername(update.getMessage().getFrom().getUserName());
            user.setState(Commands.START.getCommandName());
        }


        CommandInterface comamnd = commandFactory.createCommand(user, messageText);
        Sendable sendable = comamnd.execute();
        utils.execute(sendable, bot);

    }

}
