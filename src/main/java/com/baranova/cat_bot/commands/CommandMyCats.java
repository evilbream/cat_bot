package com.baranova.cat_bot.commands;

import java.util.Map;
import java.util.Optional;
import java.util.List;

import java.util.stream.Collectors;

import com.baranova.cat_bot.dto.CatDTO;
import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.Sendable;
import com.baranova.cat_bot.enums.Commands;
import com.baranova.cat_bot.enums.Reactions;
import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.service.ReactionService;
import com.baranova.cat_bot.service.UserService;
import com.baranova.cat_bot.telegram.constants.MessageCallback;
import com.baranova.cat_bot.telegram.constants.UserMessage;


public class CommandMyCats extends AbsCommand {

    private String commandText;
    private PhotoService photoService;
    private ReactionService reactionService;

    public CommandMyCats(UserService userService, UserDTO user, String commandText, PhotoService photoService, ReactionService reactionService) {
        super(userService, user);
        this.commandText = commandText;
        this.photoService = photoService;
        this.reactionService = reactionService;
    }


    public Sendable execute() {
        if (!user.getState().equals(Commands.MY_CATS.getCommandName())) return null;
        if (commandText.equals(MessageCallback.MY_CATS)) return composePages();
        if (commandText.equals(MessageCallback.BACK)) return back();
        if (commandText.equals(MessageCallback.NEXT_PAGE)) return changePage();
        if (commandText.equals(MessageCallback.PREVIOUS_PAGE)) return changePage();
        if (commandText.startsWith("rem_")) return delete();
        if (commandText.equals(MessageCallback.MENU)) return toMainMenu();
        if (commandText.startsWith("view_")) return composeMyCatPhoto();
        return null;
    }

    private Sendable composePages() {
        Integer page = Optional.ofNullable(user.getMyCatPage()).orElse(0);
        List<CatDTO> photo = photoService.getUserPhotosWithPagination(user.getId(), page, UserMessage.MAX_PHOTOS_PER_PAGE);
        if (photo.isEmpty() && page == 0)
            return new Sendable.Builder()
                    .chatId(user.getId())
                    .message(UserMessage.MESSAGE_NO_MY_CATS)
                    .buttonsPerRow(2)
                    .buttons(Map.of(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU))
                    .build();

        Boolean lastPage = photo.isEmpty() || photo.size() < UserMessage.MAX_PHOTOS_PER_PAGE;
        if (lastPage) {
            Integer pg = page - 1;
            photo = photoService.getUserPhotosWithPagination(user.getId(), pg, UserMessage.MAX_PHOTOS_PER_PAGE);
        }
        Map<String, String> photoMap = photo.stream().collect(Collectors.toMap(CatDTO::getCatName, p -> "view_" + p.getId().toString()));

        if (page > 0) photoMap.put(UserMessage.BUTTON_BACK, MessageCallback.PREVIOUS_PAGE);
        if (!lastPage) {
            photoMap.put(UserMessage.BUTTON_NEXT, MessageCallback.NEXT_PAGE);
        }
        photoMap.put(UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU);
        user.setState(Commands.MY_CATS.getCommandName());
        user.setMyCatPage(page);
        userService.saveUser(user);
        return new Sendable.Builder()
                .chatId(user.getId())
                .message(UserMessage.MESSAGE_MY_CATS)
                .buttonsPerRow(3)
                .buttons(photoMap)
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
        return new Sendable.Builder()
                .chatId(photo.getAuthor())
                .photo(photo.getPhoto())
                .message(caption)
                .buttonsPerRow(1)
                .buttons(Map.of(UserMessage.BUTTON_BACK, MessageCallback.BACK,
                        UserMessage.BUTTON_REMOVE, removeCallback))
                .build();
    }

    public Sendable back() {
        user.setMyCatPage(0);
        this.commandText = MessageCallback.MY_CATS;
        return execute();
    }

    public Sendable changePage() {
        Integer page = Optional.ofNullable(user.getMyCatPage()).orElse(0);

        if (commandText.equals(MessageCallback.NEXT_PAGE)) {
            page++;
            user.setMyCatPage(page);
            this.commandText = MessageCallback.MY_CATS;
            return execute();
        }

        if (commandText.equals(MessageCallback.PREVIOUS_PAGE)) {
            if (page == 0) return null;
            page--;
            user.setMyCatPage(page);
            this.commandText = MessageCallback.MY_CATS;
            return execute();
        }

        return null;
    }

    public Sendable delete() {
        if (commandText.startsWith("rem_")) {
            user.setMyCatPage(0);
            Long photoId = Long.parseLong(commandText.substring(4));
            String successMessage = UserMessage.MESSAGE_CAT_DELETED;
            if (photoService.existsById(photoId)) photoService.deletePhoto(photoId);
            else successMessage = UserMessage.MESSAGE_CAT_NOT_DELETED;

            return new Sendable.Builder()
                    .chatId(user.getId())
                    .message(successMessage)
                    .buttonsPerRow(2)
                    .buttons(Map.of(UserMessage.BUTTON_BACK, MessageCallback.BACK,
                            UserMessage.BUTTON_TO_MAIN_MENU, MessageCallback.MENU
                    ))
                    .build();
        }
        return null;
    }

}