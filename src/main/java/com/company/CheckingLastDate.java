package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CheckingLastDate implements Runnable {

    static Logger logger = LogManager.getLogger(CheckingLastDate.class.getName());

    public static final String MESSAGE_OFFLINE = "Соединение разорвано!";

    private static final Integer LIMIT_SHOWING_SECONDS = 10;

    private Integer periodPing;
    private Integer coefficientNotification;
    private TelegramBot telegramBot;

    private boolean isMessageOfflineSent = false;

    private volatile LocalDateTime timeLastConnection;

    private volatile LocalDateTime previousConnectionTime;

    public volatile boolean isMessageOnlineSent;

    public static final DateTimeFormatter dateTimeFormatterMilliSeconds =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter dateTimeFormatterSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateTimeFormatterWithoutSeconds =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setTimeLastConnection(LocalDateTime timeLastConnection) {
        this.timeLastConnection = timeLastConnection;
    }

    public LocalDateTime getTimeLastConnection() {
        return timeLastConnection;
    }

    public LocalDateTime getPreviousConnectionTime() {
        return previousConnectionTime;
    }

    public void setPreviousConnectionTime(LocalDateTime previousConnectionTime) {
        this.previousConnectionTime = previousConnectionTime;
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void setPeriodPing(Integer periodPing) {
        this.periodPing = periodPing;
    }

    public void setCoefficientNotification(Integer coefficientNotification) {
        this.coefficientNotification = coefficientNotification;
    }

    CheckingLastDate(Integer periodPing,
                     Integer coefficientNotification,
                     TelegramBot telegramBot) {
        this.periodPing = periodPing;
        this.coefficientNotification = coefficientNotification;
        this.telegramBot = telegramBot;

        Thread thread = new Thread(this,  CheckingLastDate.class.getName());
        thread.start();
    }


    @Override
    public void run() {
        while(true) {
            processOnePeriod();
        }
    }

    private void processOnePeriod() {

        if (null != timeLastConnection) {
            processLastConnection();
        }

        try {
            TimeUnit.SECONDS.sleep(periodPing);
        } catch (InterruptedException e){
            logger.error(e);
            telegramBot.sendToAll("CheckDate: Exception while sleeping", true);
        }
    }

    void processLastConnection() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(timeLastConnection)) {
            telegramBot.sendToAll("Error: timeLastConnection is after currentDateTime !\n" +
                    "timeLastConnection = " + timeLastConnection.format(dateTimeFormatterMilliSeconds) + "\n" +
                    "currentDateTime = " + currentDateTime.format(dateTimeFormatterMilliSeconds),
                    true);

            return;
        }

        LocalDateTime limit = timeLastConnection.plusSeconds(
                periodPing * coefficientNotification);

        if (limit.isBefore(currentDateTime)) {
            if (!isMessageOfflineSent) {
                telegramBot.sendToAll(MESSAGE_OFFLINE, false);

                isMessageOfflineSent = true;
                isMessageOnlineSent = false;
            }
        } else {
            if (isMessageOfflineSent) {
                if (!isMessageOnlineSent) {
                    sendMessageOnline();
                    isMessageOnlineSent = true;
                }

                isMessageOfflineSent = false;
            }
        }
    }

    private void sendMessageOnline() {
        telegramBot.sendToAll("Соединение восстановлено! Интернет не работал c " +
                format(previousConnectionTime) +
                " до " + format(timeLastConnection),
                false);
    }

    public String format(LocalDateTime time) {

        // greater or equal than minute -> don't show seconds:
        if (periodPing >= LIMIT_SHOWING_SECONDS) {
            return time.format(dateTimeFormatterWithoutSeconds);
        } else {
            return time.format(dateTimeFormatterSeconds);
        }
    }
}
