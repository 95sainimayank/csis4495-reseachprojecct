package com.example.mixbox.model;

public class Classification {
   Boolean primary;
   Segment segment;

   public Classification(Boolean primary, Segment segment) {
      this.primary = primary;
      this.segment = segment;
   }

   public Boolean getPrimary() {
      return primary;
   }

   public void setPrimary(Boolean primary) {
      this.primary = primary;
   }

   public Segment getSegment() {
      return segment;
   }

   public void setSegment(Segment segment) {
      this.segment = segment;
   }
}
