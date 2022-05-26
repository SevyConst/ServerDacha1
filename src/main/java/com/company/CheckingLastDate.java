package com.company;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class CheckingLastDate implements Runnable {

    private Integer periodPing;
    private static final int COEFFICIENT = 2;

    TelegramBot telegramBot;

    private boolean isWarningConnectionLostSent = false;

    private volatile LocalDateTime timeLastConnection;

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
            telegramBot.sendToAll("CheckDate: Exception while sleeping");
        }
    }

    void processLastConnection() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(timeLastConnection)) {
            telegramBot.sendToAll("currentDateTime is before dateLastConnect!");

            return;
        }

        LocalDateTime limit = timeLastConnection.plusSeconds(
                periodPing * COEFFICIENT);

        if (limit.isBefore(currentDateTime)) {
            if (!isWarningConnectionLostSent) {

                telegramBot.sendToAll("Соединение разорвано!");
                isWarningConnectionLostSent = true;
            }
        } else {
            if (isWarningConnectionLostSent) {

                telegramBot.sendToAll("Соединение восстановлено!");
                isWarningConnectionLostSent = false;
            }
        }

    }
}


