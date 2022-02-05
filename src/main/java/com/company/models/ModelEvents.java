package com.company.models;

import java.util.List;

public class ModelEvents {
    private List<ModelEvent> modelEvents;
    private int deviceId;


    public List<ModelEvent> getEvents() {
        return modelEvents;
    }

    public void setEvents(List<ModelEvent> modelEvents) {
        this.modelEvents = modelEvents;
    }

    public int getDeviceId() {
        return deviceId;
    }
}