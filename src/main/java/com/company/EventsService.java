package com.company;

import com.company.models.Event;
import com.company.models.Events;
import com.company.models.EventsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventsService {

    private static final String startPiEvent = "start";

    @Autowired
    CheckingLastDate checkingLastDate;

    @Autowired
    TelegramBot telegramBot;

    @Autowired
    ApplicationProperties applicationProperties;

    static Logger logger = LogManager.getLogger(EventsService.class.getName());

    private static final String FIRST_MESSAGE = "pi включился!";

    public static final DateTimeFormatter clientDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    EventsResponse processEvents(Events events) {

        checkingLastDate.setPreviousConnectionTime(checkingLastDate.getTimeLastConnection());
        checkingLastDate.setTimeLastConnection(LocalDateTime.now());

        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();

        boolean wasRestart = false;

        int numberEvents = events.getEvents().size();
        for (int i = 0; i < numberEvents; i ++) {
            Event event = events.getEvents().get(i);
            Long id = event.getId();

            if (startPiEvent.equals(event.getNameEvent())) {

                wasRestart = true;

                if ( 0 == i ) {
                    processFirstEvent(event.getTimeEvent());
                } else {
                    Event previousEvent = events.getEvents().get(i - 1);
                    LocalDateTime startLocalDateTime =
                            LocalDateTime.parse(previousEvent.getTimeEvent(), clientDateTimeFormatter);
                    String startStr = checkingLastDate.format(startLocalDateTime);

                    LocalDateTime endLocalDateTime =
                            LocalDateTime.parse(event.getTimeEvent(), clientDateTimeFormatter);
                    String endStr = checkingLastDate.format(endLocalDateTime);

                    logAndSend("Электричества не было с " + startStr +
                            " до " + endStr);
                }

                checkingLastDate.isMessageOnlineSent = true;
            }

            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("time: " + event.getTimeEvent());
            logger.info("-------------------------------------------------------");


            eventsIdsDelivered.add(id);
        }

        if (wasRestart) {
            logAndSend("Соединение восстановлено!");
        }

        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(applicationProperties.getPeriodPing());

        return response;
    }

   private void processFirstEvent(String timeStr) {
       if (null == checkingLastDate.getPreviousConnectionTime()) {
           logAndSend(FIRST_MESSAGE);
       } else {

           LocalDateTime endLocalDateTime =
                   LocalDateTime.parse(timeStr, clientDateTimeFormatter);

           logAndSend("Электричества не было с "
                   + checkingLastDate.format(checkingLastDate.getPreviousConnectionTime()) +
                   " до " +
                   checkingLastDate.format(endLocalDateTime));
       }
   }

    private void logAndSend(String message) {
        logger.info(message);
        telegramBot.sendToAll(message);
    }

}