package com.example.mixbox.model;

import java.util.List;

public class _Embedded {
   List<Venue> venues;

   public _Embedded(List<Venue> venues) {
      this.venues = venues;
   }

   public List<Venue> getVenues() {
      return venues;
   }

   public void setVenues(List<Venue> venues) {
      this.venues = venues;
   }
}