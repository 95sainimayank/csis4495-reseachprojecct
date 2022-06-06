package com.example.mixbox.model;

import java.util.List;

public class MusicEvent {
   String name;
   String id;
   String url;
   EventDate dates;
   List<Classification> classifications;
   _Embedded _embedded;

   public MusicEvent(String name, String id, String url, EventDate dates, List<Classification> classifications, _Embedded _embedded) {
      this.name = name;
      this.id = id;
      this.url = url;
      this.dates = dates;
      this.classifications = classifications;
      this._embedded = _embedded;
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

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public EventDate getDates() {
      return dates;
   }

   public void setDates(EventDate dates) {
      this.dates = dates;
   }

   public List<Classification> getClassifications() {
      return classifications;
   }

   public void setClassifications(List<Classification> classifications) {
      this.classifications = classifications;
   }

  public _Embedded get_embedded() {
      return _embedded;
   }

   public void set_embedded(_Embedded _embedded) {
      this._embedded = _embedded;
   }
}
