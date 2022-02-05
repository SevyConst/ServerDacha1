package com.company;

import com.company.models.EventsResponse;
import com.company.models.ModelEvent;
import com.company.models.ModelEvents;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventsController {



    private int periodSent = 10;

    @PostMapping("/event")
    public EventsResponse receivingEvents(@RequestBody ModelEvents modelEvents) {
        EventsResponse response = new EventsResponse();
        List<Long> eventsIdsDelivered = new ArrayList<>();
        for (ModelEvent modelEvent : modelEvents.getEvents()) {
            Long id = modelEvent.getId();

            System.out.println("event name: " + modelEvent.getNameEvent());
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
