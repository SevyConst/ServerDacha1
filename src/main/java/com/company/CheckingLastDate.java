package com.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class CheckingLastDate implements Runnable {

    static Logger logger = LogManager.getLogger(CheckingLastDate.class.getName());

    private Integer periodPing;

    // The coefficient determines strictness of monitoring
    public static final int COEFFICIENT = 2;

    TelegramBot telegramBot;

    private boolean isMessageOfflineSent = false;

    private volatile LocalDateTime timeLastConnection;

    public volatile boolean isMessageOnlineSent;

    public void setTimeLastConnection(LocalDateTime timeLastConnection) {
        this.timeLastConnection = timeLastConnection;
    }

    CheckingLastDate(Integer periodPing, TelegramBot telegramBot) {
        this.periodPing = periodPing;
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
                periodPing * COEFFICIENT);

        if (limit.isBefore(currentDateTime)) {
            if (!isMessageOfflineSent) {

                String message = "pi is offline";
                logger.info(message);
                telegramBot.sendToAll(message);

                isMessageOfflineSent = true;
                isMessageOnlineSent = false;
            }
        } else {
            if (isMessageOfflineSent) {

                if (!isMessageOnlineSent) {

                    String message = "pi is online";
                    logger.info(message);
                    telegramBot.sendToAll(message);

                    isMessageOnlineSent = true;
                }

                isMessageOfflineSent = false;
            }
        }

    }
}


