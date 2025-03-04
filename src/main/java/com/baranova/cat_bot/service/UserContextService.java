package com.baranova.cat_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.dto.converters.UserConverter;
import com.baranova.cat_bot.entity.User;

@Service
public class UserContextService {
    private final Map<Long, UserDTO> userContexts = new ConcurrentHashMap<>();

    @Autowired
    UserService userService;

    public UserDTO getContext(Long chatId) {
        if (!userContexts.containsKey(chatId)) {
            UserDTO user = userService.getUserById(chatId);
            if (user == null) {
                user = UserConverter.fromEntity(new User(chatId));
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