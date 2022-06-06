package com.example.mixbox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
   FragmentHomeBinding binding;

   public HomeFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
      binding = FragmentHomeBinding.inflate(inflater, container, false);


      /*binding.btnNext.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate(R.id.homeToSongList);
         }
      });*/

      return binding.getRoot();
   }
}