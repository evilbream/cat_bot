package com.baranova.tg_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.dto.converter.UserConverter;
import com.baranova.tg_service.entity.User;
import com.baranova.shared.enums.Commands;

@Service
public class UserContextService {
    private final Map<Long, UserDTO> userContexts = new ConcurrentHashMap<>();

    @Autowired
    UserService userService;

    public UserDTO getContext(Long chatId, String username) {
        if (!userContexts.containsKey(chatId)) {
            UserDTO user = userService.getUserById(chatId);
            if (user == null) {
                user = UserConverter.fromEntity(new User(chatId, username, Commands.START.getCommandName(), 0));
                user.setNotRegistered(true);
            }
            userContexts.put(chatId, user);
        }
        return userContexts.get(chatId);
    }

    public void removeContext(Long chatId) {
        userContexts.remove(chatId);
    }
}