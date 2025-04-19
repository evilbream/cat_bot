package com.baranova.cat_service.commands;

import java.util.LinkedHashMap;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.shared.entity.Sendable;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;
import com.baranova.shared.constants.UserMessage;

public class CommandViewCats extends AbsCommand {

    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;

    public CommandViewCats(Sendable sendable, PhotoService photoService, ReactionService reactionService) {
        super(sendable);
        this.commandText = sendable.getMessage();
        this.photoService = photoService;
        this.reactionService = reactionService;
    }

    private Sendable composeNextPhoto() {
        Long chatId = Long.parseLong(sendable.getChatId());
        CatDTO photo = photoService.getNextPhoto(chatId);
        if (photo == null) {
            photoService.resetStateWithPagination(chatId, sendable.getViewCatPage(), UserMessage.MAX_PHOTOS_PER_PAGE);
            photo = photoService.getNextPhoto(chatId);
            if (photo == null)
                return Sendable.builder()
                        .chatId(chatId.toString())
                        .message(UserMessage.MESSAGE_NO_MORE_CATS)
                        .viewCatPage(0)
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
        LinkedHashMap<String, String> reactions = new LinkedHashMap<>();
        reactions.put(likes, likeCallback);
        reactions.put(dislikes, dislikeCallback);

        return Sendable.builder()
                .chatId(sendable.getChatId())
                .photoName(caption)
                .command(this.commandText)
                .viewCatPage(sendable.getViewCatPage())
                .photo(photo.getPhoto())
                .myCatsMap(reactions)
                .build();
    }

    public Sendable execute() {
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
                        .build();
            }
        }

        return this.composeNextPhoto();
    }

}
