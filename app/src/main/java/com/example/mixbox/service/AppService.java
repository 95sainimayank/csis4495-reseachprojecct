package com.example.mixbox.service;

import com.example.mixbox.model.OuterClass;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AppService {
   @GET("/discovery/v2/events?apikey=WoxMvvr1Q3d6mpyCG3n67BwAD55NNUcZ&locale=*&city=Burnaby&countryCode=CA&preferredCountry=%20ca")
   Call<OuterClass> getAll();
}
