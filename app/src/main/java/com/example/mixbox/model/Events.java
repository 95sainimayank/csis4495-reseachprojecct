package com.example.mixbox.model;

import java.util.List;

public class Events {
   List<MusicEvent> events;

   public Events(List<MusicEvent> events) {
      this.events = events;
   }

   public List<MusicEvent> getEvents() {
      return events;
   }

   public void setEvents(List<MusicEvent> events) {
      this.events = events;
   }
}
