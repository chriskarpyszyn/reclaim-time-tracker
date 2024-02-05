package sredtimesheet;

import reclaimapi.Event;

import java.util.List;

public class EventProcessor
{
    private static final String PERSONAL_TYPE = "PERSONAL";
    private TimeEntry timeEntry;
    private final List<Event> eventsToProcess;

    public EventProcessor(List<Event> eventsToProcess) {
        timeEntry = new TimeEntry();
        this.eventsToProcess = eventsToProcess;
    }

    public TimeEntry processEvents() {
        for (Event e : eventsToProcess) {
            //todo-ck figure out a better way to filter out all-day time blocks, this <=30 hack should work for now
            if (e.getTimeChunks() <=30 && !e.getType().equals(PERSONAL_TYPE)) {
                this.timeEntry.addTime(e);
                this.timeEntry.addDescription(e);
            }
        }
        return this.timeEntry;
    }
}
