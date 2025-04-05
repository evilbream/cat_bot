package com.baranova.cat_service.commands;

import java.util.List;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.service.KeyboardService;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;
import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.constants.UserMessage;

public class CommandViewCats extends AbsCommand {

    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;

    public CommandViewCats(Sendable sendable, PhotoService photoService, ReactionService reactionService, KeyboardService keyboardService) {
        super(sendable, keyboardService);
        this.commandText = sendable.getCallbackData() == null ? sendable.getMessage() : sendable.getCallbackData();
        this.photoService = photoService;
        this.reactionService = reactionService;
    }

    public Sendable execute() {
        if (commandText.equals(MessageCallback.MENU)) return back();
        if (commandText.equals(MessageCallback.VIEW_CATS)) return executePhoto();
        if (commandText.startsWith("dis_") || commandText.startsWith("lik_")) return executePhoto();
        if (commandText.equals(MessageCallback.RESTART_VIEVING)) {
            sendable.setViewCatPage(0);
            return executePhoto();
        }
        return null;
    }

    private Sendable composeNextPhoto() {
        Long chatId = Long.parseLong(sendable.getChatId());
        CatDTO photo = photoService.getNextPhoto(chatId);
        if (photo == null) {
            photoService.resetStateWithPagination(chatId, sendable.getViewCatPage(), UserMessage.MAX_PHOTOS_PER_PAGE_VIEW_CATS);
            photo = photoService.getNextPhoto(chatId);
            if (photo == null)
                return Sendable.builder()
                        .chatId(chatId.toString())
                        .message(UserMessage.MESSAGE_NO_MORE_CATS)
                        .viewCatPage(0)
                        .buttonsPerRow(2)
                        .buttons(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_RESTART_VIEVING, MessageCallback.RESTART_VIEVING, UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU)))
                        .build();

            sendable.setViewCatPage(sendable.getViewCatPage() + 1);
        }
        Integer lks = reactionService.getReactionCount(photo.getId(), Reactions.LIKE.getId());
        Integer dslks = reactionService.getReactionCount(photo.getId(), Reactions.DISLIKE.getId());
        String caption = "Имя: " + photo.getCatName() + "\n" + "Автор: " + photo.getUsername();
        String likes = UserMessage.BUTTON_LIKE + " (" + lks + ")";
        String dislikes = UserMessage.BUTTON_DISLIKE + " (" + dslks + ")";
        String likeCallback = "lik_" + photo.getId();
        String dislikeCallback = "dis_" + photo.getId();


        return Sendable.builder()
                .chatId(sendable.getChatId())
                .message(caption)
                .viewCatPage(sendable.getViewCatPage())
                .photo(photo.getPhoto())
                .buttonsPerRow(2)
                .buttons(keyboardService.makeKeyBoardFromList(List.of(likes, likeCallback,
                        dislikes, dislikeCallback,
                        UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU
                )))
                .build();
    }

    public Sendable executePhoto() {
        if (commandText.startsWith("lik_") || commandText.startsWith("dis_")) {
            Long photoId = Long.parseLong(commandText.substring(4));
            Integer reactionId = Reactions.LIKE.getId();

            if (commandText.startsWith("dis_")) {
                reactionId = Reactions.DISLIKE.getId();
            }

            try {
                reactionService.updateReaction(ReactionDTO
                        .builder()
                        .userId(Long.parseLong(sendable.getChatId()))
                        .photoId(photoId)
                        .reaction(reactionId)
                        .build());
            } catch (IllegalArgumentException e) {
                return Sendable.builder()
                        .chatId(sendable.getChatId())
                        .message(UserMessage.MESSAGE_PHOTO_DELETED)
                        .buttonsPerRow(2)
                        .buttons(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU, UserMessage.BUTTON_VIEW_CATS, MessageCallback.VIEW_CATS)))
                        .build();
            }
        }

        return this.composeNextPhoto();
    }


    public Sendable back() {
        return Sendable.builder()
                .chatId(sendable.getChatId())
                .state(Commands.START.getCommandName())
                .message(UserMessage.MESSAGE_START)
                .buttonsPerRow(3)
                .buttons(MessageCallback.START_BUTTONS)
                .build();
    }

}
