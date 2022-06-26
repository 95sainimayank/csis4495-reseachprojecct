package com.example.mixbox.model;

public class Start {
   private String localDate;
   private String localTime;
   private String dateTime;
   private boolean dateTBD;
   private boolean dateTBA;
   private boolean timeTBA;
   private boolean noSpecificTime;

   public Start(String localDate, String localTime, String dateTime, boolean dateTBD, boolean dateTBA, boolean timeTBA, boolean noSpecificTime) {
      this.localDate = localDate;
      this.localTime = localTime;
      this.dateTime = dateTime;
      this.dateTBD = dateTBD;
      this.dateTBA = dateTBA;
      this.timeTBA = timeTBA;
      this.noSpecificTime = noSpecificTime;
   }

   // Getter Methods
   public String getLocalDate() {
      return localDate;
   }

   public String getLocalTime() {
      return localTime;
   }

   public String getDateTime() {
      return dateTime;
   }

   public boolean getDateTBD() {
      return dateTBD;
   }

   public boolean getDateTBA() {
      return dateTBA;
   }

   public boolean getTimeTBA() {
      return timeTBA;
   }

   public boolean getNoSpecificTime() {
      return noSpecificTime;
   }

   // Setter Methods
   public void setLocalDate(String localDate) {
      this.localDate = localDate;
   }

   public void setLocalTime(String localTime) {
      this.localTime = localTime;
   }

   public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
   }

   public void setDateTBD(boolean dateTBD) {
      this.dateTBD = dateTBD;
   }

   public void setDateTBA(boolean dateTBA) {
      this.dateTBA = dateTBA;
   }

   public void setTimeTBA(boolean timeTBA) {
      this.timeTBA = timeTBA;
   }

   public void setNoSpecificTime(boolean noSpecificTime) {
      this.noSpecificTime = noSpecificTime;
   }
}