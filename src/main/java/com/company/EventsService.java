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
    applicationProperties applicationProperties;

    static Logger logger = LogManager.getLogger(EventsService.class.getName());

    private static final String FIRST_MESSAGE = "pi включился!";

    public static final DateTimeFormatter clientDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    EventsResponse processEvents(Events events) {

        LocalDateTime previousConnectionTime = checkingLastDate.getTimeLastConnection();
        checkingLastDate.setTimeLastConnection(LocalDateTime.now());

        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();

        int numberEvents = events.getEvents().size();
        for (int i = 0; i < numberEvents; i ++) {
            Event event = events.getEvents().get(i);
            Long id = event.getId();

            if (startPiEvent.equals(event.getNameEvent())) {
                if ( 0 == i ) {
                    processFirstEvent(event.getTimeEvent(), previousConnectionTime);
                } else {
                    Event previousEvent = events.getEvents().get(i - 1);
                    LocalDateTime startLocalDateTime =
                            LocalDateTime.parse(previousEvent.getTimeEvent(), clientDateTimeFormatter);
                    String startStr = cutStringTime(startLocalDateTime);

                    LocalDateTime endLocalDateTime =
                            LocalDateTime.parse(event.getTimeEvent(), clientDateTimeFormatter);
                    String endStr = cutStringTime(endLocalDateTime);

                    String message = "Электричества не было с " + startStr +
                            " до " + endStr;
                    logAndSend(message);
                }

                checkingLastDate.isMessageOnlineSent = true;
            }

            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("time: " + event.getTimeEvent());
            logger.info("-------------------------------------------------------");


            eventsIdsDelivered.add(id);
        }
        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(applicationProperties.getPeriodPing());

        return response;
    }

   private void processFirstEvent(String timeStr, LocalDateTime previousConnectionTime) {
       if (null == previousConnectionTime) {
           logAndSend(FIRST_MESSAGE);
       } else {

           LocalDateTime endLocalDateTime =
                   LocalDateTime.parse(timeStr, clientDateTimeFormatter);

           logAndSend("Соединение восстановлено! Электричества не было с "
                   + cutStringTime(previousConnectionTime) +
                   " до " +
                   cutStringTime(endLocalDateTime));
       }
   }

    // About precision
    private String cutStringTime(LocalDateTime time) {

        // greater or equal than minute -> don't show seconds:
        if (applicationProperties.getPeriodPing() * applicationProperties.getCoefficientNotification() >= 60) {
            return time.format(CheckingLastDate.dateTimeFormatterWithoutSeconds);
        } else {
            return time.format(CheckingLastDate.dateTimeFormatterSeconds);
        }
    }

    private void logAndSend(String message) {
        logger.info(message);
        telegramBot.sendToAll(message);
    }

}