package com.company.models;

import java.util.List;

public class Events {
    private List<Event> events;
    private int deviceId;


    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public int getDeviceId() {
        return deviceId;
    }
}