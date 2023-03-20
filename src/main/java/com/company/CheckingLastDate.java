package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CheckingLastDate implements Runnable {

    static Logger logger = LogManager.getLogger(CheckingLastDate.class.getName());

    public static final String MESSAGE_OFFLINE = "Соединение разорвано!";

    public final Integer coefficientNotification;  // The coefficient determines strictness of monitoring
    private final Integer periodPing;
    private final TelegramBot telegramBot;

    private boolean isMessageOfflineSent = false;

    private volatile LocalDateTime timeLastConnection;

    public volatile boolean isMessageOnlineSent;

    public static final DateTimeFormatter dateTimeFormatterSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateTimeFormatterWithoutSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public void setTimeLastConnection(LocalDateTime timeLastConnection) {
        this.timeLastConnection = timeLastConnection;
    }

    public LocalDateTime getTimeLastConnection() {
        return timeLastConnection;
    }

    CheckingLastDate(Integer periodPing, Integer coefficientNotification, TelegramBot telegramBot) {
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
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e){
            String message = "CheckDate: Exception while sleeping";
            logger.error(message, e);
            telegramBot.sendToAll(message);
        }
    }

    void processLastConnection() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(timeLastConnection)) {
            String message = "Error: currentDateTime is before dateLastConnect!";
            logger.error(message);
            telegramBot.sendToAll(message);

            return;
        }

        LocalDateTime limit = timeLastConnection.plusSeconds(
                periodPing * coefficientNotification);

        if (limit.isBefore(currentDateTime)) {
            if (!isMessageOfflineSent) {
                logger.info(MESSAGE_OFFLINE);
                telegramBot.sendToAll(MESSAGE_OFFLINE);

                isMessageOfflineSent = true;
                isMessageOnlineSent = false;
            }
        } else {
            if (isMessageOfflineSent) {
                if (!isMessageOnlineSent) {
                    sendMessageOnline(currentDateTime);
                    isMessageOnlineSent = true;
                }

                isMessageOfflineSent = false;
            }
        }
    }

    private void sendMessageOnline(LocalDateTime currentDateTime) {
        String message;
        if (periodPing*coefficientNotification >= 60) {
            message = "Соединение восстановлено! Интернет не работал c " +
                    timeLastConnection.format(dateTimeFormatterWithoutSeconds) +
                    " до " + currentDateTime.format(dateTimeFormatterWithoutSeconds);
        } else {
            message = "Соединение восстановлено! Интернет не работал c " +
                    timeLastConnection.format(dateTimeFormatterSeconds) +
                    " до " + currentDateTime.format(dateTimeFormatterSeconds);
        }
        logger.info(message);
        telegramBot.sendToAll(message);
    }
}
