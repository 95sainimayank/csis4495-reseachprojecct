package com.example.mixbox.model;

import android.location.Location;

public class Venue {
   private String name;
   private String id;
   private Location location;

   public Venue(String name, String id, Location location) {
      this.name = name;
      this.id = id;
      this.location = location;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Location getLocation() {
      return location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }
}
