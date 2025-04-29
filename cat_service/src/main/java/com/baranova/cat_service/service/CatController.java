package com.baranova.cat_service.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.baranova.shared.constants.UserMessage;
import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.shared.entity.Sendable;
import com.baranova.shared.enums.CatActions;
import com.baranova.shared.enums.Commands;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;

public class CatController implements CatControllerInterface {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private PhotoService photoService;
    private RabbitMQProducer rabbitMQProducerService;
    private String catAction;
    private Sendable sendable;
    private ReactionService reactionService;

    public CatController(Sendable sendable, PhotoService photoService, ReactionService reactionService, RabbitMQProducer rabbitMQProducerService) {
        this.sendable = sendable;
        this.photoService = photoService;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.reactionService = reactionService;
        this.catAction = sendable.getCatAction();
    }

    @Override
    public Sendable execute() {
        if (catAction.equals(CatActions.SAVE.getActionName())) return save();
        if (catAction.equals(CatActions.DELETE.getActionName())) return delete();
        if (catAction.equals(CatActions.GET_MY_CATS.getActionName())) return getMyCats();
        if (catAction.equals(CatActions.VIEW_MY_CATS.getActionName())) return getMyCatPhoto();
        if (catAction.equals(CatActions.LIKE.getActionName())) return setLike();
        if (catAction.equals(CatActions.DISLIKE.getActionName())) return setDislike();
        if (catAction.equals(CatActions.VIEW_CATS.getActionName())) return getNextCatPhoto();
        return null;
    }


    public Sendable save() {
        rabbitMQProducerService.sendAsync(SendableConverter.toJson(Sendable.builder()
                .state(Commands.START.getCommandName())
                .chatId(sendable.getChatId())
                .command(Commands.START.getCommandName())
                .build()));

        CatDTO photo = CatDTO.builder()
                .author(Long.parseLong(sendable.getChatId()))
                .username(sendable.getUsername())
                .catName(sendable.getPhotoName())
                .uploadedAt(LocalDateTime.now())
                .photo(sendable.getPhoto())
                .build();

        Long photoID = photoService.savePhoto(Long.parseLong(sendable.getChatId()), photo);
        String message = "Котик " + sendable.getPhotoName() + " успешно сохранен!";
        if (photoID == null) message = "Не удалось сохранить котика " + sendable.getPhotoName();
        return Sendable.builder()
                .chatId(sendable.getChatId())
                .message(message)
                .build();

    }

    private Sendable getMyCats() {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .command(sendable.getCommand())
                    .chatId(chatId.toString())
                    .build();

        Boolean lastPage = photo.isEmpty() || photo.size() < UserMessage.MAX_PHOTOS_PER_PAGE;
        if (lastPage && page > 0) {
            Integer pg = page - 1;
            photo = photoService.getUserPhotosWithPagination(chatId, pg, UserMessage.MAX_PHOTOS_PER_PAGE);
        }
        Map<String, String> photoMap = photo.stream().collect(Collectors.toMap(CatDTO::getCatName, p -> "view_" + p.getId().toString()));
        LinkedHashMap<String, String> photoLinkedMap = new LinkedHashMap<>(photoMap);

        return Sendable.builder()
                .command(sendable.getCommand())
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatsMap(photoLinkedMap)
                .build();
    }

    private Sendable getMyCats(Long removedPhoto) {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .command(sendable.getCommand())
                    .chatId(chatId.toString())
                    .build();

        Boolean lastPage = photo.isEmpty() || photo.size() < UserMessage.MAX_PHOTOS_PER_PAGE;
        if (lastPage && page > 0) {
            Integer pg = page - 1;
            photo = photoService.getUserPhotosWithPagination(chatId, pg, UserMessage.MAX_PHOTOS_PER_PAGE);
        }
        photo.removeIf(p -> p.getId().equals(removedPhoto));
        Map<String, String> photoMap = photo.stream().collect(Collectors.toMap(CatDTO::getCatName, p -> "view_" + p.getId().toString()));
        LinkedHashMap<String, String> photoLinkedMap = new LinkedHashMap<>(photoMap);

        return Sendable.builder()
                .command(sendable.getCommand())
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatsMap(photoLinkedMap)
                .build();
    }


    private Sendable getMyCatPhoto() {
        Long photoId = Long.parseLong(sendable.getPhotoId());
        CatDTO photo = photoService.getPhotoById(photoId);
        if (photo == null) return null;

        Integer lks = reactionService.getReactionCount(photo.getId(), Reactions.LIKE.getId());
        Integer dslks = reactionService.getReactionCount(photo.getId(), Reactions.DISLIKE.getId());

        String caption = "Имя: " + photo.getCatName() + "\n" + "Автор: " + photo.getUsername() + "\n" + "👍" + " (" + lks + ")" + "\n" + "👎" + " (" + dslks + ")";
        return Sendable.builder()
                .chatId(photo.getAuthor().toString())
                .photo(photo.getPhoto())
                .photoId(photo.getId().toString())
                .photoName(caption)
                .command(sendable.getCommand())
                .build();
    }

    public Sendable delete() {
        Long photoId = Long.parseLong(sendable.getPhotoId());
        this.catAction = CatActions.GET_MY_CATS.getActionName();
        executorService.submit(() -> rabbitMQProducerService.sendAsync(SendableConverter.toJson((getMyCats(photoId)))));
        String successMessage = UserMessage.MESSAGE_CAT_DELETED;
        if (photoService.existsById(photoId)) {
            photoService.deletePhoto(photoId);
            photoService.clearCache();
        } else successMessage = UserMessage.MESSAGE_CAT_NOT_DELETED;

        return Sendable.builder()
                .myCatPage(0)
                .state(successMessage)
                .chatId(sendable.getChatId())
                .message(successMessage)
                .build();

    }

    private Sendable getNextCatPhoto() {
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
                .command(sendable.getCommand())
                .viewCatPage(sendable.getViewCatPage())
                .photo(photo.getPhoto())
                .myCatsMap(reactions)
                .build();
    }

    public Sendable setLike() {
        Integer reactionId = Reactions.LIKE.getId();
        return updateReaction(reactionId);
    }

    public Sendable setDislike() {
        Integer reactionId = Reactions.DISLIKE.getId();
        return updateReaction(reactionId);
    }


    public Sendable updateReaction(int reactionId) {
        try {
            reactionService.updateReaction(ReactionDTO
                    .builder()
                    .userId(Long.parseLong(sendable.getChatId()))
                    .photoId(Long.parseLong(sendable.getPhotoId()))
                    .reaction(reactionId)
                    .build());
        } catch (IllegalArgumentException e) {
            return Sendable.builder()
                    .chatId(sendable.getChatId())
                    .message(UserMessage.MESSAGE_PHOTO_DELETED)
                    .build();
        }

        return this.getNextCatPhoto();

    }


}
