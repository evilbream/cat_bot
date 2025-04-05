package com.baranova.cat_service.commands;

import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.stream.Collectors;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.converters.SendableConverter;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.enums.Reactions;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.KeyboardService;
import com.baranova.cat_service.service.PhotoService;
import com.baranova.cat_service.service.ReactionService;
import com.baranova.cat_service.constants.MessageCallback;
import com.baranova.cat_service.constants.UserMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandMyCats extends AbsCommand {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;
    private RabbitMQProducer rabbitMQProducerService;


    public CommandMyCats(Sendable sendable, PhotoService photoService, ReactionService reactionService, RabbitMQProducer rabbitMQProducerService, KeyboardService keyboardService) {
        super(sendable, keyboardService);
        this.photoService = photoService;
        this.reactionService = reactionService;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.commandText = sendable.getCallbackData() == null ? sendable.getMessage() : sendable.getCallbackData();
    }


    public Sendable execute() {
        if (commandText.equals(MessageCallback.MY_CATS)) return composePages();
        if (commandText.startsWith("rem_")) return delete();
        if (commandText.startsWith("view_")) return composeMyCatPhoto();
        return null;
    }

    private Sendable composePages() {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .chatId(chatId.toString())
                    .message(UserMessage.MESSAGE_NO_MY_CATS)
                    .buttonsPerRow(2)
                    .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        Boolean lastPage = photo.isEmpty() || photo.size() < UserMessage.MAX_PHOTOS_PER_PAGE;
        if (lastPage && page > 0) {
            Integer pg = page - 1;
            photo = photoService.getUserPhotosWithPagination(chatId, pg, UserMessage.MAX_PHOTOS_PER_PAGE);
        }
        Map<String, String> photoMap = photo.stream().collect(Collectors.toMap(CatDTO::getCatName, p -> "view_" + p.getId().toString()));
        LinkedHashMap<String, String> photoLinkedMap = new LinkedHashMap<>(photoMap);

        if (page > 0) photoLinkedMap.put(UserMessage.BUTTON_BACK, MessageCallback.PREVIOUS_PAGE);
        if (!lastPage) {
            photoLinkedMap.put(UserMessage.BUTTON_NEXT, MessageCallback.NEXT_PAGE);
        }
        photoLinkedMap.put(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU);
        return Sendable.builder()
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatPage(page)
                .message(UserMessage.MESSAGE_MY_CATS)
                .buttonsPerRow(3)
                .buttons(photoLinkedMap)
                .build();
    }

    private Sendable composePages(Long removedPhoto) {
        Long chatId = Long.parseLong(sendable.getChatId());
        Integer page = Optional.ofNullable(sendable.getMyCatPage()).orElse(0);
        if (page < 0) page = 0;
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(chatId, page, UserMessage.MAX_PHOTOS_PER_PAGE + 1);
        if (photo.isEmpty() && page == 0)
            return Sendable.builder()
                    .chatId(chatId.toString())
                    .message(UserMessage.MESSAGE_NO_MY_CATS)
                    .buttonsPerRow(2)
                    .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        Boolean lastPage = photo.isEmpty() || photo.size() < UserMessage.MAX_PHOTOS_PER_PAGE - 1;
        if (lastPage && page > 0) {
            Integer pg = page - 1;
            photo = photoService.getUserPhotosWithPagination(chatId, pg, UserMessage.MAX_PHOTOS_PER_PAGE);
        }
        photo.removeIf(p -> p.getId().equals(removedPhoto));
        Map<String, String> photoMap = photo.stream().collect(Collectors.toMap(CatDTO::getCatName, p -> "view_" + p.getId().toString()));
        LinkedHashMap<String, String> photoLinkedMap = new LinkedHashMap<>(photoMap);

        if (page > 0) photoLinkedMap.put(UserMessage.BUTTON_BACK, MessageCallback.PREVIOUS_PAGE);
        if (!lastPage) {
            photoLinkedMap.put(UserMessage.BUTTON_NEXT, MessageCallback.NEXT_PAGE);
        }
        photoLinkedMap.put(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU);
        return Sendable.builder()
                .chatId(chatId.toString())
                .state(Commands.MY_CATS.getCommandName())
                .myCatPage(page)
                .message(UserMessage.MESSAGE_MY_CATS)
                .buttonsPerRow(2)
                .buttons(photoLinkedMap)
                .build();
    }


    private Sendable composeMyCatPhoto() {
        Long photoId = Long.parseLong(commandText.substring(5));
        CatDTO photo = photoService.getPhotoById(photoId);
        if (photo == null) return null;

        Integer lks = reactionService.getReactionCount(photo.getId(), Reactions.LIKE.getId());
        Integer dslks = reactionService.getReactionCount(photo.getId(), Reactions.DISLIKE.getId());

        String caption = "Имя: " + photo.getCatName() + "\n" + "Автор: " + photo.getUsername() + "\n" + "👍" + " (" + lks + ")" + "\n" + "👎" + " (" + dslks + ")";
        String removeCallback = "rem_" + photo.getId();
        return Sendable.builder()
                .chatId(photo.getAuthor().toString())
                .photo(photo.getPhoto())
                .message(caption)
                .buttonsPerRow(1)
                .buttons(keyboardService.makeKeyBoardFromList(List.of(UserMessage.BUTTON_BACK, MessageCallback.BACK,
                        UserMessage.BUTTON_REMOVE, removeCallback)))
                .build();
    }

    public Sendable delete() {
        if (commandText.startsWith("rem_")) {
            Long photoId = Long.parseLong(commandText.substring(4));
            this.commandText = MessageCallback.MY_CATS;
            executorService.submit(() -> rabbitMQProducerService.sendAsync(SendableConverter.toJson((composePages(photoId)))));
            String successMessage = UserMessage.MESSAGE_CAT_DELETED;
            if (photoService.existsById(photoId)) {
                photoService.deletePhoto(photoId);
                photoService.clearCache();
            } else successMessage = UserMessage.MESSAGE_CAT_NOT_DELETED;

            return Sendable.builder()
                    .myCatPage(0)
                    .chatId(sendable.getChatId())
                    .message(successMessage)
                    .build();
        }
        return null;
    }

}