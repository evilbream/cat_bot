package com.baranova.cat_service.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;
import com.baranova.cat_service.service.UserService;

@Component
public class CommandFactory {
    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;


    public AbsCommand createCommand(UserDTO user, String commandText) {
        Commands cmd = Commands.fromCommandName(user.getState());

        return switch (cmd) {
            case START -> new CommandStart(userService, user);
            case ADD_CAT_PHOTO -> new CommandAddCatPhoto(userService, user, commandText);
            case ADD_CAT_NAME ->
                    new CommandAddCatName(userService, user, commandText, photoService, rabbitMQProducerService);
            case MY_CATS ->
                    new CommandMyCats(userService, user, commandText, photoService, reactionService, rabbitMQProducerService);
            case VIEW_CATS -> new CommandViewCats(userService, user, commandText, photoService, reactionService);
            default -> null;
        };
    }
}
