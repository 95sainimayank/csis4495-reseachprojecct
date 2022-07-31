package com.example.mixbox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentPlaylistSongsBinding;

public class PlaylistSongsFragment extends Fragment {
   FragmentPlaylistSongsBinding binding;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
      binding = FragmentPlaylistSongsBinding.inflate(inflater, container, false);

      binding.playlistToolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PlaylistFragment()).commit();
         }
      });

      return binding.getRoot();
   }
}









































