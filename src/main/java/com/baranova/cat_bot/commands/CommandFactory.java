package com.baranova.cat_bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.service.ReactionService;
import com.baranova.cat_bot.service.UserService;

@Component
public class CommandFactory {
    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ReactionService reactionService;


    public AbsCommand createCommand(UserDTO user, String commandText) {
        Commands cmd = Commands.fromCommandName(user.getState());

        return switch (cmd) {
            case START -> new CommandStart(userService, user);
            case ADD_CAT_PHOTO -> new CommandAddCatPhoto(userService, user, commandText);
            case ADD_CAT_NAME -> new CommandAddCatName(userService, user, commandText, photoService);
            case MY_CATS -> new CommandMyCats(userService, user, commandText, photoService, reactionService);
            case VIEW_CATS -> new CommandViewCats(userService, user, commandText, photoService, reactionService);
            default -> null;
        };
    }
}
