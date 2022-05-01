package com.company;

import com.company.models.EventsResponse;
import com.company.models.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventsController {

    @Autowired
    EventsService eventsService;

    @PostMapping("/event")
    public EventsResponse receivingEvents(@RequestBody Events events) {
        return eventsService.processEvents(events);
    }
}
