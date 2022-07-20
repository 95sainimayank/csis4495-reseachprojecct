package com.example.mixbox.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mixbox.adapter.PlaylistAdapter;
import com.example.mixbox.databinding.FragmentPlaylistBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaylistFragment extends Fragment {
   FragmentPlaylistBinding binding;
   FirebaseDatabase db;
   FirebaseAuth auth;
   List<String> allPlaylists;
   PlaylistAdapter adapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentPlaylistBinding.inflate(inflater, container, false);

      db = FirebaseDatabase.getInstance();
      auth = FirebaseAuth.getInstance();
      allPlaylists = new ArrayList<>();

      binding.playlistRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
      adapter = new PlaylistAdapter(allPlaylists);

      binding.playlistRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

      binding.playlistRecycler.setAdapter(adapter);

      getAllPlaylists();

      binding.btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
//            String playlistName =
         }
      });

      return binding.getRoot();
   }

   public void getAllPlaylists() {
      allPlaylists.clear();

      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  HashMap<String, Object> playlists = (HashMap<String, Object>) eachUser.get("playlists");

                  if (playlists != null) {
                     for (Object eachPlaylist : playlists.values()) {
                        HashMap<String, Object> playlist = (HashMap<String, Object>) eachPlaylist;

                        String playlistName = playlist.get("name").toString();
                        allPlaylists.add(playlistName);

                        //for all songs
//                        HashMap<String, Object> songs = (HashMap<String, Object>) playlist.get("songs");
//
//                        if (songs != null) {
//                           for(Object eachSong : songs.values()){
//                              Log.e("jaja song", eachSong.toString());
//                           }
//                        }
                     }
                  }
               }
               adapter.notifyDataSetChanged();
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });


   }
}



































