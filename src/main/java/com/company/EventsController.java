package com.company;

import com.company.models.EventsResponse;
import com.company.models.Event;
import com.company.models.Events;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EventsController {
    static Logger logger = LogManager.getLogger(Db.class.getName());

    @Autowired
    Db db;

    @Autowired
    CheckingLastDate checkingLastDate;

    private int periodSent = 10;

    @PostMapping("/event")
    public EventsResponse receivingEvents(@RequestBody Events events) {

        checkingLastDate.setTimeLastConnection(LocalDateTime.now());
        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();
        for (Event event : events.getEvents()) {
            Long id = event.getId();


            logger.info("event name: " + event.getNameEvent());
            logger.info("event id: " + id);
            logger.info("device id: " + id);
            logger.info("time: " + event.getTimeEvent());


            eventsIdsDelivered.add(id);
        }
        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(periodSent);

        logger.info(db.processingProperties.getUrlForDb());

        return response;
    }
}
