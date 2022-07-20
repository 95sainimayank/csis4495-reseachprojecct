package com.example.mixbox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentFavoritesBinding;

public class FavoritesFragment extends Fragment {
   FragmentFavoritesBinding binding;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
      binding = FragmentFavoritesBinding.inflate(inflater, container, false);

      return binding.getRoot();
   }
}
































