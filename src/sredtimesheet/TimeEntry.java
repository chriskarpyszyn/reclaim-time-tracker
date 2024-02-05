package sredtimesheet;

import reclaimapi.Event;
import java.time.LocalDate;


public class TimeEntry
{
    private LocalDate entryDate;
    private float totalTime;
    private float sredableTime;
    private float irapableTime;
    private String sredDescription;
    private String irapDescription;


    public TimeEntry() {
        this.entryDate = LocalDate.now();
        this.totalTime = 0;
        this.sredableTime = 0;
        this.irapableTime = 0;
        this.sredDescription = "";
        this.irapDescription = "";
    }

    public void addTime(Event e) {
        if (isSred(e)) {
            sredableTime += calculateHoursFromEvent(e);
        } else if (isIrap(e)) {
            irapableTime += calculateHoursFromEvent(e);
        }
        totalTime += calculateHoursFromEvent(e);
    }

    public void addDescription(Event e) {
        if (isSred(e)) {
            sredDescription = createOrAppendDescription(e, sredDescription);
        } else if (isIrap(e)) {
            irapDescription = createOrAppendDescription(e, irapDescription);
        }
    }

    //todo-ck instead of substring, remove the word "SRED:" or "IRAP:" and maybe icons
    //that reclaim adds
    private String createOrAppendDescription(Event e, String description) {
        int subStringStartIndex = 6;
        if (description.equals("")) {
            return e.getTitle().substring(subStringStartIndex);
        } else {
            return description + " & " + e.getTitle().substring(subStringStartIndex);
        }
    }

    private boolean isSred(Event e) {
        return e.getTitle().contains("SRED:");
    }

    private boolean isIrap(Event e) {
        return e.getTitle().contains("IRAP:");
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

    public String getSredDescription() {
        return sredDescription;
    }

    public String getIrapDescription() {
        return irapDescription;
    }
}
