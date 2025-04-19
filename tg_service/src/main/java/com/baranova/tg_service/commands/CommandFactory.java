package com.baranova.tg_service.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.enums.Commands;
import com.baranova.tg_service.rabbitMQ.RabbitMQProducer;
import com.baranova.tg_service.services.KeyboardService;
import com.baranova.tg_service.services.UserService;

@Component
public class CommandFactory {
    @Autowired
    private UserService userService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;

    public AbsCommand createCommand(UserDTO user, String commandText) {
        Commands cmd = Commands.fromCommandName(user.getState());

        return switch (cmd) {
            case START -> new CommandStart(userService, user, keyboardService);
            case ADD_CAT_PHOTO -> new CommandAddCatPhoto(userService, user, commandText, keyboardService);
            case ADD_CAT_NAME ->
                    new CommandAddCatName(userService, user, commandText, rabbitMQProducerService, keyboardService);
            case MY_CATS -> new CommandMyCats(userService, user, commandText, rabbitMQProducerService, keyboardService);
            case VIEW_CATS ->
                    new CommandViewCats(userService, user, commandText, rabbitMQProducerService, keyboardService);
            default -> null;
        };
    }
}
