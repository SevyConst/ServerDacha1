package com.company;

import com.company.models.EventsResponse;
import com.company.models.Events;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventsController {

    static Logger logger = LogManager.getLogger(EventsController.class.getName());

    @Autowired
    EventsService eventsService;

    @PostMapping("/event")
    public EventsResponse receivingEvents(@RequestBody Events events) {

        EventsResponse eventsResponse =  eventsService.processEvents(events);

        logger.info("RESPONSE DATA: " + new Gson().toJson(eventsResponse));
        return eventsResponse;
    }
}
