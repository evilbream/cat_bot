package com.baranova.cat_service.commands;

import java.time.LocalDateTime;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.converters.SendableConverter;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.enums.Commands;
import com.baranova.cat_service.rabbitMQ.RabbitMQProducer;
import com.baranova.cat_service.service.PhotoService;

public class CommandAddCatName extends AbsCommand {
    private PhotoService photoService;
    private RabbitMQProducer rabbitMQProducerService;

    public CommandAddCatName(Sendable sendable, PhotoService photoService, RabbitMQProducer rabbitMQProducerService) {
        super(sendable);
        this.photoService = photoService;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    public Sendable execute() {
        if (sendable.getCommand().equals("save")) return this.save();
        else return null;
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


}
