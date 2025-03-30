package com.baranova.cat_service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.commands.CommandFactory;
import com.baranova.cat_service.commands.CommandInterface;
import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.UserContextService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CallbackHandler {


    @Autowired
    private UserContextService userContextService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private CommandFactory commandFactory;


    public Sendable handleCallback(String callbackData, Long chatId) {
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
        return comamnd.execute();
    }


}
