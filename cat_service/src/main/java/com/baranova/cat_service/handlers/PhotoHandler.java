package com.baranova.cat_service.handlers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.baranova.cat_service.commands.CommandFactory;
import com.baranova.cat_service.commands.CommandInterface;
import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.dto.UserDTO;
import com.baranova.cat_service.dto.converters.UserConverter;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.entity.Sendable;
import com.baranova.cat_service.service.UserContextService;

@Component
public class PhotoHandler {

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CommandFactory commandFactory;


    public Sendable handlePhoto(Long chatId, byte[] photo) {

        UserDTO user = userContextService.getContext(chatId);

        Photo newPhoto = new Photo(UserConverter.toEntity(user), photo);
        CatDTO photoDTO = new CatDTO.Builder()
                .id(newPhoto.getId())
                .author(user.getId())
                .username(user.getUsername())
                .uploadedAt(LocalDateTime.now())
                .photo(photo)
                .build();
        user.setCurrentPhoto(photoDTO);

        CommandInterface comamnd = commandFactory.createCommand(user, null);
        return comamnd.execute();

    }
}
