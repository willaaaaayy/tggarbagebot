package com.tgbotmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TgBotMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgBotMapApplication.class, args);
    }
}
