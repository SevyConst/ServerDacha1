package com.company;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class Config {

    @Bean
    public Db db(ApplicationProperties applicationProperties) {
        return new Db(applicationProperties.getUrlForDb());
    }

    @Bean
    public TelegramBot telegramBot(ApplicationProperties applicationProperties, Db db) {
        return new TelegramBot(applicationProperties.getTelegramBotToken(),
                applicationProperties.getCodeWord(),
                applicationProperties.getAdminCodeWord(),
                db);
    }

    @Bean
    public CheckingLastDate checkingLastDate(ApplicationProperties applicationProperties,
                                             TelegramBot telegramBot) {
        return new CheckingLastDate(applicationProperties.getPeriodPing(),
                applicationProperties.getCoefficientNotification(),
                telegramBot);
    }

}
