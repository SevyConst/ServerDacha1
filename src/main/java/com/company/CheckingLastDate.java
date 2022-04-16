package com.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn({"forBot"})
public class CheckingLastDate implements Runnable {

    private static final int COEFFICIENT = 2;

    private boolean isWarningConnectionLostSent = false;

//    @Autowired
//    ProcessingProperties properties;

    @Autowired
    ForBot forBot;

    private volatile LocalDateTime timeLastConnection;

    public void setTimeLastConnection(LocalDateTime timeLastConnection) {
        this.timeLastConnection = timeLastConnection;
    }

    CheckingLastDate() {
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
            forBot.sendToAll("CheckDate: Exception while sleeping");
        }
    }

    void processLastConnection() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(timeLastConnection)) {
            forBot.sendToAll("currentDateTime is before dateLastConnect!");

            return;
        }

        LocalDateTime limit = timeLastConnection.plusSeconds(
                10 * COEFFICIENT);

        if (limit.isBefore(currentDateTime)) {
            if (!isWarningConnectionLostSent) {

                forBot.sendToAll("Соединение разорвано!");
                isWarningConnectionLostSent = true;
            }
        } else {
            if (isWarningConnectionLostSent) {

                forBot.sendToAll("Соединение восстановлено!");
                isWarningConnectionLostSent = false;
            }
        }

    }
}


