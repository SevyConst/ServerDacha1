package com.company;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties
public class ProcessingProperties {

    private String urlForDb;
    private Integer periodPing;
    private String telegramBotToken;
    private String codeWord;

    public String getUrlForDb() {
        return urlForDb;
    }

    public void setUrlForDb(String urlForDb) {
        this.urlForDb = urlForDb;
    }

    public Integer getPeriodPing() {
        return periodPing;
    }

    public void setPeriodPing(Integer periodPing) {
        this.periodPing = periodPing;
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getCodeWord() {
        return codeWord;
    }

    public void setCodeWord(String codeWord) {
        this.codeWord = codeWord;
    }
}
