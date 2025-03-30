package com.baranova.cat_service.commands;

import java.util.Map;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;
import com.baranova.cat_service.service.UserService;
import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.constants.UserMessage;

public class CommandViewCats extends AbsCommand {

    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;

    public CommandViewCats(UserService userService, UserDTO user, String commandText, PhotoService photoService, ReactionService reactionService) {
        super(userService, user);
        this.commandText = commandText;
        this.photoService = photoService;
        this.reactionService = reactionService;
    }

    public Sendable execute() {
        if (!user.getState().equals(Commands.VIEW_CATS.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MENU)) return back();
        if (commandText.equals(MessageCallback.VIEW_CATS)) return executePhoto();
        if (commandText.startsWith("dis_") || commandText.startsWith("like_")) return executePhoto();
        return null;
    }

    private Sendable composeNextPhoto() {
        CatDTO photo = photoService.getNextPhoto(user.getId());
        if (photo == null) return null;
        Integer lks = reactionService.getReactionCount(photo.getId(), Reactions.LIKE.getId());
        Integer dslks = reactionService.getReactionCount(photo.getId(), Reactions.DISLIKE.getId());
        String caption = "Имя: " + photo.getCatName() + "\n" + "Автор: " + photo.getUsername();
        String likes = UserMessage.BUTTON_LIKE + " (" + lks + ")";
        String dislikes = UserMessage.BUTTON_DISLIKE + " (" + dslks + ")";
        String likeCallback = "like_" + photo.getId();
        String dislikeCallback = "dis_" + photo.getId();

        return new Sendable.Builder()
                .chatId(user.getId())
                .message(caption)
                .photo(photo.getPhoto())
                .buttonsPerRow(2)
                .buttons(Map.of(likes, likeCallback,
                        dislikes, dislikeCallback,
                        UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU
                ))
                .build();
    }

    public Sendable executePhoto() {
        ReactionDTO.Builder reactionDTOBuilder = new ReactionDTO.Builder();
        reactionDTOBuilder.userId(user.getId());
        if (commandText.startsWith("like_")) {
            Long photoId = Long.parseLong(commandText.substring(5));
            ReactionDTO reactionDTO = reactionDTOBuilder
                    .photoId(photoId)
                    .reaction(Reactions.LIKE.getId())
                    .build();
            reactionService.updateReaction(reactionDTO);
        }

        if (commandText.startsWith("dis_")) {
            Long photoId = Long.parseLong(commandText.substring(4));
            ReactionDTO reactionDTO = reactionDTOBuilder
                    .photoId(photoId)
                    .reaction(Reactions.DISLIKE.getId())
                    .build();
            reactionService.updateReaction(reactionDTO);

        }
        return this.composeNextPhoto();
    }


    public Sendable back() {
        user.setState(Commands.START.getCommandName());
        photoService.resetState(user.getId());
        return new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build();
    }

}
