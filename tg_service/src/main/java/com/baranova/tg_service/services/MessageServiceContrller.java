package com.baranova.tg_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baranova.tg_service.utils.MessageSender;

import lombok.extern.slf4j.Slf4j;

import com.baranova.tg_service.dto.UserDTO;
import com.baranova.tg_service.entity.Sendable;

@Slf4j
@Service
public class MessageServiceContrller {
    @Autowired
    private MessageSender utils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContextService userContextService;

    public void processMessage(Sendable sendable) {
        log.info("received new messaeg" + sendable.toString());
        UserDTO user = userContextService.getContext(Long.parseLong(sendable.getChatId()), sendable.getUsername());
        if (sendable.getPhotoName() != null) user.setCurrentPhotoName(sendable.getPhotoName());
        if (sendable.getState() != null) user.setState(sendable.getState());
        if (sendable.getMyCatPage() != null) user.setMyCatPage(sendable.getMyCatPage());
        if (sendable.getViewCatPage() != null) user.setViewCatPage(sendable.getViewCatPage());

        utils.execute(sendable);
        userService.saveUser(user);

    }

}
