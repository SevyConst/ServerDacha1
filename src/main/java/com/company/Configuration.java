package com.company;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(ProcessingProperties.class)
public class Configuration {

    @Bean
    public Db db(ProcessingProperties processingProperties) {
        return new Db(processingProperties.getUrlForDb());
    }

    @Bean
    public TelegramBot telegramBot(ProcessingProperties processingProperties, Db db) {
        return new TelegramBot(processingProperties.getTelegramBotToken(),
                processingProperties.getCodeWord(), db);
    }

    @Bean
    public CheckingLastDate checkingLastDate(ProcessingProperties processingProperties,
                                             TelegramBot telegramBot) {
        return new CheckingLastDate(processingProperties.getPeriodPing(), telegramBot);
    }

}
