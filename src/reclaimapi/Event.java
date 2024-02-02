package reclaimapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@SuppressWarnings("unused") // Suppresses IntelliJ IDEA warnings for unused declarations
public class Event {
    private String eventId;
    private int calendarId;
    private String key;
    private String priority;
    private String prioritySource;
    private String title;
    private String titleSeenByOthers;
    private String status;

    /**
     * I see "MEETING", "WORK", "LOGISTICS", "PERSONAL" types. Can create enum.
     */
    private String type;

    private String subType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date eventStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date eventEnd;
    private int timeChunks;
    private int allocatedTimeChunks;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updated;
    private Boolean requiresTravel;
    private String rsvpStatus;
    private String etag;
    private String organizer;
    private Boolean recurringException;
    private String onlineMeetingUrl;
    private String color;
    private String category;
    private Boolean reclaimManaged;
    private Boolean recurring;
    private Boolean published;
    private String reclaimEventType;
    private Boolean underAssistControl;
    private Boolean free;
    private Boolean recurringInstance;
    private String scoredType;
    private int numAttendees;
    private Boolean personalSync;
    private Boolean conferenceCall;
    @JsonProperty("public")
    private Boolean isPublic;
    @JsonProperty("private")
    private Boolean isPrivate;
    private String version;

    public Event() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPrioritySource() {
        return prioritySource;
    }

    public void setPrioritySource(String prioritySource) {
        this.prioritySource = prioritySource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleSeenByOthers() {
        return titleSeenByOthers;
    }

    public void setTitleSeenByOthers(String titleSeenByOthers) {
        this.titleSeenByOthers = titleSeenByOthers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Date getEventStart() {
        return eventStart;
    }

    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
    }

    public int getTimeChunks() {
        return timeChunks;
    }

    public void setTimeChunks(int timeChunks) {
        this.timeChunks = timeChunks;
    }

    public int getAllocatedTimeChunks() {
        return allocatedTimeChunks;
    }

    public void setAllocatedTimeChunks(int allocatedTimeChunks) {
        this.allocatedTimeChunks = allocatedTimeChunks;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Boolean getRequiresTravel() {
        return requiresTravel;
    }

    public void setRequiresTravel(Boolean requiresTravel) {
        this.requiresTravel = requiresTravel;
    }

    public String getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(String rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Boolean getRecurringException() {
        return recurringException;
    }

    public void setRecurringException(Boolean recurringException) {
        this.recurringException = recurringException;
    }

    public String getOnlineMeetingUrl() {
        return onlineMeetingUrl;
    }

    public void setOnlineMeetingUrl(String onlineMeetingUrl) {
        this.onlineMeetingUrl = onlineMeetingUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getReclaimManaged() {
        return reclaimManaged;
    }

    public void setReclaimManaged(Boolean reclaimManaged) {
        this.reclaimManaged = reclaimManaged;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getReclaimEventType() {
        return reclaimEventType;
    }

    public void setReclaimEventType(String reclaimEventType) {
        this.reclaimEventType = reclaimEventType;
    }

    public Boolean getUnderAssistControl() {
        return underAssistControl;
    }

    public void setUnderAssistControl(Boolean underAssistControl) {
        this.underAssistControl = underAssistControl;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Boolean getRecurringInstance() {
        return recurringInstance;
    }

    public void setRecurringInstance(Boolean recurringInstance) {
        this.recurringInstance = recurringInstance;
    }

    public String getScoredType() {
        return scoredType;
    }

    public void setScoredType(String scoredType) {
        this.scoredType = scoredType;
    }

    public int getNumAttendees() {
        return numAttendees;
    }

    public void setNumAttendees(int numAttendees) {
        this.numAttendees = numAttendees;
    }

    public Boolean getPersonalSync() {
        return personalSync;
    }

    public void setPersonalSync(Boolean personalSync) {
        this.personalSync = personalSync;
    }

    public Boolean getConferenceCall() {
        return conferenceCall;
    }

    public void setConferenceCall(Boolean conferenceCall) {
        this.conferenceCall = conferenceCall;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
