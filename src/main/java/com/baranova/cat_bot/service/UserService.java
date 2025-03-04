package com.baranova.cat_bot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baranova.cat_bot.dto.UserDTO;
import com.baranova.cat_bot.dto.converters.UserConverter;
import com.baranova.cat_bot.entity.User;
import com.baranova.cat_bot.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return UserConverter.fromEntity(user);
    }

    @Async
    @Transactional
    public void saveUser(UserDTO userDto) {
        User user = UserConverter.toEntity(userDto);
        userRepository.save(user);
    }

    @Async
    @Transactional
    public void saveUser(Long id, String username, String state) {
        User user = new User(id, username, state);
        if (!userRepository.findById(user.getId()).isPresent()) {
            userRepository.save(user);
        }
    }


}
