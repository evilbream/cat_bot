package com.baranova.cat_bot.dto.converters;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.entity.User;

public class UserConverter {
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO
                .Builder()
                .id(user.getId())
                .username(user.getUsername())
                .state(user.getState())
                .myCatPage(0)
                .notRegistered(false)
                .build();
    }

    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setState(userDTO.getState());
        return user;
    }
}
