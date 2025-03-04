package com.baranova.cat_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication
@EnableAsync
@Import({TelegramBotStarterConfiguration.class})
public class CatBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatBotApplication.class, args);
    }

}
