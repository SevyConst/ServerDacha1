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
    ProcessingProperties processingProperties;

    static Logger logger = LogManager.getLogger(Db.class.getName());

    EventsResponse processEvents(Events events) {
        checkingLastDate.setTimeLastConnection(LocalDateTime.now());
        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();

        for (int i = 0; i < events.getEvents().size(); i ++) {
            Event event = events.getEvents().get(i);
            Long id = event.getId();

            if (startPiEvent.equals(event.getNameEvent())) {
                if (i != 0) {
                    Event previousEvent = events.getEvents().get(i - 1);

                    String start = previousEvent.getTimeEvent().substring(0, previousEvent.getTimeEvent().length()
                                    - 7);  // Don't show milliseconds and seconds

                    String end = event.getTimeEvent().substring(0, event.getTimeEvent().length()
                                    - 7);  // Don't show milliseconds and seconds

                    telegramBot.sendToAll("was off from " + start +
                            " to " + end);  // Not show milliseconds
                } else {
                    telegramBot.sendToAll("pi started!");
                }
            }

            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("device id: " + id);
            logger.info("time: " + event.getTimeEvent());


            eventsIdsDelivered.add(id);
        }
        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(processingProperties.getPeriodPing());

        return response;
    }
}
