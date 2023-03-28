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

    public static final String MESSAGE_FIRST = "Raspberry Pi включился в ";
    public static final String MESSAGE_ELECTRICITY_OFF = "Электричества не было с ";
    public static final String MESSAGE_UNTIL = " до ";
    public static final String MESSAGE_RECONNECTED = "Сейчас соединение восстановлено.";

    public static final DateTimeFormatter clientDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    EventsResponse processEvents(Events events) {

        checkingLastDate.setPreviousConnectionTime(checkingLastDate.getTimeLastConnection());
        checkingLastDate.setTimeLastConnection(LocalDateTime.now());

        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();

        StringBuilder message = new StringBuilder();
        int numberEvents = events.getEvents().size();
        for (int i = 0; i < numberEvents; i ++) {
            Event event = events.getEvents().get(i);
            Long id = event.getId();

            if (startPiEvent.equals(event.getNameEvent())) {
                if ( 0 == i ) {
                    message.append(processFirstEvent(event.getTimeEvent()));
                } else {
                    Event previousEvent = events.getEvents().get(i - 1);
                    LocalDateTime startLocalDateTime =
                            LocalDateTime.parse(previousEvent.getTimeEvent(), clientDateTimeFormatter);
                    String startStr = checkingLastDate.format(startLocalDateTime);

                    LocalDateTime endLocalDateTime =
                            LocalDateTime.parse(event.getTimeEvent(), clientDateTimeFormatter);
                    String endStr = checkingLastDate.format(endLocalDateTime);


                    message.append(MESSAGE_ELECTRICITY_OFF).append(startStr).append(MESSAGE_UNTIL)
                            .append(endStr).append(";\n");
                }

                checkingLastDate.isMessageOnlineSent = true;
            }

            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("time: " + event.getTimeEvent());
            logger.info("-------------------------------------------------------");


            eventsIdsDelivered.add(id);
        }
        if (!message.isEmpty()) {
            message.append(MESSAGE_RECONNECTED);
            logAndSend(message.toString());
        }

        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(applicationProperties.getPeriodPing());

        return response;
    }

    private String processFirstEvent(String timeStr) {
        LocalDateTime time =
               LocalDateTime.parse(timeStr, clientDateTimeFormatter);

        timeStr = checkingLastDate.format(time);

        if (null == checkingLastDate.getPreviousConnectionTime()) {
            return MESSAGE_FIRST + timeStr + ";\n";
        }

        return MESSAGE_ELECTRICITY_OFF
               + checkingLastDate.format(checkingLastDate.getPreviousConnectionTime()) +
               MESSAGE_UNTIL +
               timeStr + ";\n";
   }

    private void logAndSend(String message) {
        logger.info(message);
        telegramBot.sendToAll(message);
    }

}