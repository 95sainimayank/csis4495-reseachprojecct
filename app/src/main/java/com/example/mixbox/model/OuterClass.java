package com.example.mixbox.model;

public class OuterClass {
   _MainEmbedded _embedded;

   public OuterClass(_MainEmbedded _embedded) {
      this._embedded = _embedded;
   }

   public _MainEmbedded get_embedded() {
      return _embedded;
   }

   public void set_embedded(_MainEmbedded _embedded) {
      this._embedded = _embedded;
   }
}
