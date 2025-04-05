package com.baranova.tg_service.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.dto.converter.UserConverter;
import com.baranova.tg_service.entity.User;
import com.baranova.tg_service.repository.UserRepository;

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
        User user = new User(id, username, state, 0);
        if (!userRepository.findById(user.getId()).isPresent()) {
            userRepository.save(user);
        }
    }


}
