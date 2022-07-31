package com.example.mixbox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixbox.R;
import com.example.mixbox.connection.RetroConnection;
import com.example.mixbox.databinding.FragmentEventBinding;
import com.example.mixbox.model.OuterClass;
import com.example.mixbox.service.AppService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EventFragment extends Fragment {
   FragmentEventBinding binding;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
      binding = FragmentEventBinding.inflate(inflater, container, false);

//      RetroConnection connection = new RetroConnection();
//      Retrofit retrofit = connection.retrofit;
//
//      AppService appService = retrofit.create(AppService.class);
//
//      appService.getAll(city).enqueue(new Callback<OuterClass>() {
//         @Override
//         public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
////            Log.e("--succ", response.body().get_embedded().getEvents().get(1).get_embedded().getVenues().get(0).getLocation().getLongitude() + "");
//            Log.e("succ", response.body().get_embedded().getEvents().size() + "");
//         }
//         @Override
//         public void onFailure(Call<OuterClass> call, Throwable t) {
//            Log.e("--faile", t.getMessage());
//         }
//      });

      return binding.getRoot();
   }
}