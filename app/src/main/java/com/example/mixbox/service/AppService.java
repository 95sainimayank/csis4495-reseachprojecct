package com.example.mixbox.service;

import com.example.mixbox.model.OuterClass;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppService {
   @GET("/discovery/v2/events")
   Call<OuterClass> getByCity(@Query("apikey")String apikey, @Query("locale") String locale, @Query("city") String city);

}
