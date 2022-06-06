package com.example.mixbox.connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroConnection {
   String BASE_URL = "https://app.ticketmaster.com/";

   public Retrofit retrofit = new Retrofit.Builder()
     .baseUrl(BASE_URL)
     .addConverterFactory(GsonConverterFactory.create())
     .build();
}