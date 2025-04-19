package com.baranova.cat_service.commands;

import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.stream.Collectors;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.shared.dto.converter.SendableConverter;
import com.baranova.shared.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baranova.shared.constants.UserMessage;

public class CommandMyCats extends AbsCommand {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;
    private RabbitMQProducer rabbitMQProducerService;


    public CommandMyCats(Sendable sendable, PhotoService photoService, ReactionService reactionService, RabbitMQProducer rabbitMQProducerService) {
        super(sendable);
        this.photoService = photoService;
        this.reactionService = reactionService;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.commandText = sendable.getMessage() == null ? sendable.getCommand() : sendable.getMessage();
    }


    public Sendable execute() {
        if (commandText.startsWith("rem_")) return delete();
        if (commandText.startsWith("view_")) return composeMyCatPhoto();
        return composePages();
    }

    private Sendable composePages() {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .command(commandText)
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
                .command(commandText)
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatsMap(photoLinkedMap)
                .build();
    }

    private Sendable composePages(Long removedPhoto) {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .command(commandText)
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
                .command(commandText)
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatsMap(photoLinkedMap)
                .build();
    }


    private Sendable composeMyCatPhoto() {
        Long photoId = Long.parseLong(commandText.substring(5));
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
                .command(commandText)
                .build();
    }

    public Sendable delete() {
        if (commandText.startsWith("rem_")) {
            Long photoId = Long.parseLong(commandText.substring(4));
            this.commandText = Commands.MY_CATS.getCommandName();
            executorService.submit(() -> rabbitMQProducerService.sendAsync(SendableConverter.toJson((composePages(photoId)))));
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
        return null;
    }

}