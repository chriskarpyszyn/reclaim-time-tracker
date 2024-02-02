package sredtimesheet;

import reclaimapi.Event;
import java.time.LocalDate;


public class TimeEntry
{
    private float totalTime;
    private float sredableTime;
    private float irapableTime;
    private LocalDate entryDate;

    public TimeEntry() {
        this.entryDate = LocalDate.now();
        this.totalTime = 0;
        this.sredableTime = 0;
        this.irapableTime = 0;
    }

    public void addTime(Event e) {
        if (e.getTitle().contains("SRED:")) {
            sredableTime += calculateHoursFromEvent(e);
        } else if (e.getTitle().contains("IRAP: ")) {
            irapableTime += calculateHoursFromEvent(e);
        }
        totalTime += calculateHoursFromEvent(e);
    }

    private float calculateHoursFromEvent(Event e) {
        return (float)(e.getTimeChunks() * 15) /60;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public float getSredableTime() {
        return sredableTime;
    }

    public float getIrapableTime() {
        return irapableTime;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }
}
