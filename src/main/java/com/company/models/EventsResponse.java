package com.company.models;

import java.util.List;

public class EventsResponse {
    private List<Long> eventsIdsDelivered;

    public void setEventsIdsDelivered(List<Long> eventsIdsDelivered) {
        this.eventsIdsDelivered = eventsIdsDelivered;
    }

    private int periodSent;

    public List<Long> getEventsIdsDelivered() {
        return eventsIdsDelivered;
    }

    public void setEventsArrayDeliveryConfirmation(List<Long> eventsIdsDelivered) {
        this.eventsIdsDelivered = eventsIdsDelivered;
    }

    public int getPeriodSent() {
        return periodSent;
    }

    public void setPeriodSent(int periodSent) {
        this.periodSent = periodSent;
    }
}
