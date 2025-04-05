package com.baranova.cat_service.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.KeyboardService;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;

@Component
public class CommandFactory {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private RabbitMQProducer rabbitMQProducerService;


    public AbsCommand createCommand(Sendable sendable) {
        Commands cmd = Commands.fromCommandName(sendable.getState());

        return switch (cmd) {
            case ADD_CAT_NAME ->
                    new CommandAddCatName(sendable, photoService, rabbitMQProducerService, keyboardService);
            case MY_CATS ->
                    new CommandMyCats(sendable, photoService, reactionService, rabbitMQProducerService, keyboardService);
            case VIEW_CATS -> new CommandViewCats(sendable, photoService, reactionService, keyboardService);
            default -> null;
        };
    }
}
