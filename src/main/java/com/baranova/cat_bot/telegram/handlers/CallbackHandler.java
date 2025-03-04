package com.baranova.cat_bot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.baranova.cat_bot.service.PhotoService;
import com.baranova.cat_bot.telegram.constants.UserMessage;
import com.baranova.cat_bot.telegram.constants.MessageCallback;


@Component
public class CallbackHandler {
    @Autowired
    private PhotoService photoService;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private PhotoHandler photohandler;

    public SendMessage handleCallback(Update update, TelegramLongPollingBot bot) {
        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        if (callbackData.equals(MessageCallback.SHOW_CATS)) {
            String filename = photoService.getNextPhoto(chatId);
            if (filename != null) {
                photohandler.sendNextPhoto(chatId, filename, bot);
                return null;
            } else {
                return messageHandler.composePlainMessage(chatId, UserMessage.NO_MORE_PHOTOS);
            }
        }
        if (callbackData.equals(MessageCallback.BACK)) {
            photoService.resetState(chatId);
            return messageHandler.composeStartMessage(chatId);

        }
        return null;
    }


}
