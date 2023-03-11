package com.company;

import com.company.models.Event;
import com.company.models.Events;
import com.company.models.EventsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    static Logger logger = LogManager.getLogger(Db.class.getName());

    EventsResponse processEvents(Events events) {
        checkingLastDate.setTimeLastConnection(LocalDateTime.now());
        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();

        int numberEvents = events.getEvents().size();
        for (int i = 0; i < numberEvents; i ++) {
            Event event = events.getEvents().get(i);
            Long id = event.getId();

            if (startPiEvent.equals(event.getNameEvent())) {
                if ( 0 == i ) {
                    processFirstEvent(event, numberEvents);
                } else {
                    Event previousEvent = events.getEvents().get(i - 1);
                    String start = cutStringTime(previousEvent.getTimeEvent());

                    String end = cutStringTime(event.getTimeEvent());

                    telegramBot.sendToAll("pi was off from " + start +
                            " to " + end);
                }
            }

            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("device id: " + id);
            logger.info("time: " + event.getTimeEvent());

            eventsIdsDelivered.add(id);
        }
        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(applicationProperties.getPeriodPing());

        return response;
    }

   private void processFirstEvent(Event event, int numberEvents) {
       if (1 == numberEvents) {

           // This is the last event. Don't type time because it is current time
           telegramBot.sendToAll("pi started");
       } else {
           telegramBot.sendToAll("pi started at " +
                   cutStringTime(event.getTimeEvent()));
       }

       checkingLastDate.isMessageOnlineSent = true;

   }

    // About precision
    private String cutStringTime(String time) {
        if (applicationProperties.getPeriodPing() * CheckingLastDate.COEFFICIENT >= 60) {
            // greater or equal than minute -> don't show seconds and milliseconds:
            return time.substring(0, time.length() - 7);
        } else {
            // less than minute --> don't show milliseconds
            return time.substring(0, time.length() - 4);
        }
    }

}
