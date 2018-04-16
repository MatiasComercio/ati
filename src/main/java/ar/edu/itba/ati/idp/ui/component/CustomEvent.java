package ar.edu.itba.ati.idp.ui.component;

import javafx.event.Event;
import javafx.event.EventType;

public enum CustomEvent {
  NEW_VALID_INPUT("NEW_VALID_INPUT"),
  NEW_INVALID_INPUT("NEW_INVALID_INPUT");

  private final EventType<Event> eventType;
  private final Event event;

  CustomEvent(final String name) {
    this.eventType = new EventType<>(name);
    this.event = new Event(this.eventType);
  }

  public EventType<Event> getEventType() {
    return eventType;
  }

  public Event getEvent() {
    return event;
  }
}
