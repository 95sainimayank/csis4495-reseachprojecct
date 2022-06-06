package com.example.mixbox.model;

public class Location {
   private String longitude;
   private String latitude;

   public Location(){

   }

   // Getter Methods
   public String getLongitude() {
      return longitude;
   }
   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   // Setter Methods
   public String getLatitude() {
      return latitude;
   }
   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }
}