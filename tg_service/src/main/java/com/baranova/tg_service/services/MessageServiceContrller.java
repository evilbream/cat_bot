package com.baranova.tg_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baranova.tg_service.utils.MessageSender;

import lombok.extern.slf4j.Slf4j;

import com.baranova.tg_service.commands.CommandFactory;
import com.baranova.tg_service.commands.CommandInterface;
import com.baranova.tg_service.dto.UserDTO;
import com.baranova.shared.entity.Sendable;

@Slf4j
@Service
public class MessageServiceContrller {
    @Autowired
    private MessageSender utils;

    @Autowired
    private CommandFactory commandFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContextService userContextService;

    public void processMessage(Sendable sendable) {
        log.info("received new messaeg" + sendable.toString());
        UserDTO user = userContextService.getContext(Long.parseLong(sendable.getChatId()), sendable.getUsername());
        user.updateUser(sendable);

        if (sendable.getCommand() != null) {
            CommandInterface comamnd = commandFactory.createCommand(user, sendable.getCommand());
            sendable = comamnd.execute();
        }

        utils.execute(sendable);
        userService.saveUser(user);


    }

}
