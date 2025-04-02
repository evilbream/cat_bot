package com.baranova.tg_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.tg_service.commands.CommandFactory;
import com.baranova.tg_service.commands.CommandInterface;
import com.baranova.tg_service.constants.MessageCallback;
import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.Sendable;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.services.UserContextService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CallbackHandler {

    @Autowired
    private CommandFactory commandFactory;


    @Autowired
    private UserContextService userContextService;

    public Sendable handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        UserDTO user = userContextService.getContext(chatId);

        String userState = user.getState();

        if (userState.equals(Commands.START.getCommandName())) {
            if (callbackData.equals(MessageCallback.ADD_CAT_ASK_NAME))
                user.setState(Commands.ADD_CAT_PHOTO.getCommandName());

            if (callbackData.equals(MessageCallback.MY_CATS)) {
                user.setState(Commands.MY_CATS.getCommandName());
            }

            if (callbackData.equals(MessageCallback.VIEW_CATS)) {
                user.setState(Commands.VIEW_CATS.getCommandName());
            }
        }

        CommandInterface comamnd = commandFactory.createCommand(user, callbackData);
        Sendable sendable = comamnd.execute();
        return sendable;

    }
}