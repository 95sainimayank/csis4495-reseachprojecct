package com.example.mixbox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentHomeFragementBinding;
import com.example.mixbox.databinding.FragmentSongListBinding;

public class SongListFragment extends Fragment {
   FragmentSongListBinding binding;

   public SongListFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentSongListBinding.inflate(inflater, container, false);

      binding.btnToHome.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate(R.id.fromSongListToHome);
         }
      });

      return binding.getRoot();
   }
}