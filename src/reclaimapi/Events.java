package reclaimapi;

import java.util.List;

public class Events {
    private List<Event> events;

    public Events() {

    }
    public Events(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
