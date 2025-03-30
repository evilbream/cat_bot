package com.baranova.tg_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
@EnableAsync
@Import({TelegramBotStarterConfiguration.class})
public class TgServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgServiceApplication.class, args);
    }

}