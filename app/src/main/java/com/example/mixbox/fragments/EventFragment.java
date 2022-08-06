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

      return binding.getRoot();
   }
}