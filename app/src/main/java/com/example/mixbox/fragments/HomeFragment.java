package com.example.mixbox.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

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
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentHomeBinding.inflate(inflater, container, false);

      addListeners();

      return binding.getRoot();
   }

   public void addListeners(){
      SongListFragment fragment = new SongListFragment();

      binding.rockFab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "rock");
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
         }
      });

      binding.edmFab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "edm");
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
         }
      });

      binding.rnbFab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "rnb");
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
         }
      });

      binding.latestCardView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "latest");
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
         }
      });

      binding.mostCardView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "mostPlayed");
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();

         }
      });
   }

}