package com.company;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(applicationProperties.class)
public class Config {

    @Bean
    public Db db(applicationProperties applicationProperties) {
        return new Db(applicationProperties.getUrlForDb());
    }

    @Bean
    public TelegramBot telegramBot(applicationProperties applicationProperties, Db db) {
        return new TelegramBot(applicationProperties.getTelegramBotToken(),
                applicationProperties.getCodeWord(), db);
    }

    @Bean
    public CheckingLastDate checkingLastDate(applicationProperties applicationProperties,
                                             TelegramBot telegramBot) {
        return new CheckingLastDate(applicationProperties.getPeriodPing(),
                applicationProperties.getCoefficientNotification(), telegramBot);
    }

}
