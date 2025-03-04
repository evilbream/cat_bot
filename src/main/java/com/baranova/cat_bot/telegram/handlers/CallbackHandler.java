package com.baranova.cat_bot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.utils.Utils;
import com.baranova.cat_bot.commands.CommandFactory;
import com.baranova.cat_bot.commands.CommandInterface;
import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.service.UserContextService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CallbackHandler {

    @Autowired
    private Utils utils;


    @Autowired
    private UserContextService userContextService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private CommandFactory commandFactory;


    public void handleCallback(Update update, TelegramLongPollingBot bot) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        UserDTO user = userContextService.getContext(chatId);

        String userState = user.getState();

        if (userState.equals(Commands.START.getCommandName())) {
            if (callbackData.equals(MessageCallback.ADD_CAT_ASK_NAME))
                user.setState(Commands.ADD_CAT_PHOTO.getCommandName());

            if (callbackData.equals(MessageCallback.MY_CATS)) {
                photoService.resetState(chatId);
                user.setState(Commands.MY_CATS.getCommandName());
            }

            if (callbackData.equals(MessageCallback.VIEW_CATS)) {
                photoService.resetState(chatId);
                user.setState(Commands.VIEW_CATS.getCommandName());
            }

        }

        CommandInterface comamnd = commandFactory.createCommand(user, callbackData);
        Sendable sendable = comamnd.execute();
        utils.execute(sendable, bot);
    }


}
