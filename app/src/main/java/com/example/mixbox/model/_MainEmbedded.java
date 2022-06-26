package com.example.mixbox.model;

import java.util.List;

public class _MainEmbedded {
   List<MusicEvent> events;

   public _MainEmbedded(List<MusicEvent> events) {
      this.events = events;
   }

   public List<MusicEvent> getEvents() {
      return events;
   }

   public void setEvents(List<MusicEvent> events) {
      this.events = events;
   }
}
