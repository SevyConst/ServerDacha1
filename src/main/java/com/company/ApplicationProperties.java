package com.company;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties
public class ApplicationProperties {

    private String urlForDb;
    private Integer periodPing;
    private String telegramBotToken;
    private String codeWord;
    private String adminCodeWord;
    private int coefficientNotification;  // The coefficient determines strictness of monitoring

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

    public String getAdminCodeWord() {
        return adminCodeWord;
    }

    public void setAdminCodeWord(String adminCodeWord) {
        this.adminCodeWord = adminCodeWord;
    }

    public int getCoefficientNotification() {
        return coefficientNotification;
    }

    public void setCoefficientNotification(int coefficientNotification) {
        this.coefficientNotification = coefficientNotification;
    }
}
