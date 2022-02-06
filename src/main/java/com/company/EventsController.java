package com.company;

import com.company.models.EventsResponse;
import com.company.models.Event;
import com.company.models.Events;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventsController {



    private int periodSent = 10;

    @PostMapping("/event")
    public EventsResponse receivingEvents(@RequestBody Events events) {
        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();
        for (Event event : events.getEvents()) {
            Long id = event.getId();

            System.out.println("event name: " + event.getNameEvent());
            System.out.println("event id: " + id);
            System.out.println("device id: " + id);
            System.out.println();

            eventsIdsDelivered.add(id);
        }
        response.setEventsIdsDelivered(eventsIdsDelivered);
        response.setPeriodSent(periodSent);
        return response;
    }
}
