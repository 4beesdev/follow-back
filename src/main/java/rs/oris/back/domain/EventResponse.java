package rs.oris.back.domain;

import java.util.List;

public class EventResponse {
    private List<EventKeyContact> eventKeyContactList;
    private List<EventLocation> eventLocationList;
    private List<EventSpeed> eventSpeedList;
    private List<EventUnauthorized> eventUnauthorizedList;

    public EventResponse() {
    }

    public EventResponse(List<EventKeyContact> eventKeyContactList, List<EventLocation> eventLocationList, List<EventSpeed> eventSpeedList, List<EventUnauthorized> eventUnauthorizedList) {
        this.eventKeyContactList = eventKeyContactList;
        this.eventLocationList = eventLocationList;
        this.eventSpeedList = eventSpeedList;
        this.eventUnauthorizedList = eventUnauthorizedList;
    }

    public List<EventKeyContact> getEventKeyContactList() {
        return eventKeyContactList;
    }

    public void setEventKeyContactList(List<EventKeyContact> eventKeyContactList) {
        this.eventKeyContactList = eventKeyContactList;
    }

    public List<EventLocation> getEventLocationList() {
        return eventLocationList;
    }

    public void setEventLocationList(List<EventLocation> eventLocationList) {
        this.eventLocationList = eventLocationList;
    }

    public List<EventSpeed> getEventSpeedList() {
        return eventSpeedList;
    }

    public void setEventSpeedList(List<EventSpeed> eventSpeedList) {
        this.eventSpeedList = eventSpeedList;
    }

    public List<EventUnauthorized> getEventUnauthorizedList() {
        return eventUnauthorizedList;
    }

    public void setEventUnauthorizedList(List<EventUnauthorized> eventUnauthorizedList) {
        this.eventUnauthorizedList = eventUnauthorizedList;
    }
}
