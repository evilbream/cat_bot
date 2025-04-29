package com.baranova.tg_service.dto.converter;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.User;

public class UserConverter {
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .state(user.getState())
                .myCatPage(0)
                .viewCatPage(user.getViewCatPage())
                .notRegistered(false)
                .build();
    }

    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setState(userDTO.getState());
        user.setViewCatPage(userDTO.getViewCatPage());
        return user;
    }
}
