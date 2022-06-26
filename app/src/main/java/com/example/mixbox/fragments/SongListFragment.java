package com.example.mixbox.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mixbox.R;
import com.example.mixbox.adapter.SongListAdapter;
import com.example.mixbox.databinding.FragmentSongListBinding;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.SongListModel;
import com.example.mixbox.utilities.SortSongByLatest;
import com.example.mixbox.utilities.SortSongByPlayCount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class SongListFragment extends Fragment {
   FragmentSongListBinding binding;
   FirebaseDatabase db;
   ArrayList<SongListModel> allSongListItems;
   SongListAdapter songListAdapter;

   public SongListFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentSongListBinding.inflate(inflater, container, false);

      db = FirebaseDatabase.getInstance();

      binding.songlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      allSongListItems = new ArrayList<>();

      songListAdapter = new SongListAdapter(getActivity(), allSongListItems);
      binding.songlistRecyclerView.setAdapter(songListAdapter);

      String type = getArguments().get("type").toString();

      binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
         }
      });


      switch (type) {
         case "rock":
            showSongBasedOnCategory("rock");
            break;
         case "edm":
            showSongBasedOnCategory("edm");
            break;
         case "rnb":
            showSongBasedOnCategory("rnb");
            break;
         case "latest":
            showSongBasedOnMainCards("latest");
            break;
         case "mostPlayed":
            showSongBasedOnMainCards("mostPlayed");
            break;
         default:
            break;
      }
      return binding.getRoot();
   }

   @Override
   public void onResume() {
      super.onResume();
      ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
   }

   @Override
   public void onStop() {
      super.onStop();
      ((AppCompatActivity) getActivity()).getSupportActionBar().show();
   }

   public void showSongBasedOnCategory(String category) {
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;


                  HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                  if (eachUserSongs != null) {
                     for (Object song : eachUserSongs.values()) {
                        HashMap<String, Object> eachSong = (HashMap<String, Object>) song;


                        HashMap<String, Object> genreMap = (HashMap<String, Object>) eachSong.get("genre");

                        for (Object genre : genreMap.values()) {
                           if (genre.toString().equals(category)) {
                              SongListModel recyclerViewModelObject = new SongListModel();

                              String name = eachUser.get("fullName").toString();
                              recyclerViewModelObject.setArtistName(name);

                              recyclerViewModelObject.
                                setSong(new Song(eachSong.get("songName").toString(),
                                  Integer.parseInt(eachSong.get("timesPlayed").toString()),
                                  LocalDateTime.parse(eachSong.get("dateTime").toString()),
                                  null));

                              Log.e("---", recyclerViewModelObject.getSong().getSongName());

                              allSongListItems.add(recyclerViewModelObject);
                           }
                        }
                     }
                  }
               }

               songListAdapter.notifyDataSetChanged();
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
   }

   public void showSongBasedOnMainCards(String cardType){
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;


                  HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                  if (eachUserSongs != null) {
                     for (Object song : eachUserSongs.values()) {
                        HashMap<String, Object> eachSong = (HashMap<String, Object>) song;


                        HashMap<String, Object> genreMap = (HashMap<String, Object>) eachSong.get("genre");


                        SongListModel recyclerViewModelObject = new SongListModel();

                        String name = eachUser.get("fullName").toString();
                        recyclerViewModelObject.setArtistName(name);

                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(eachSong.get("dateTime").toString()),
                            null));

                        Log.e("---", recyclerViewModelObject.getSong().getSongName());


                        allSongListItems.add(recyclerViewModelObject);

                     }
                  }
               }
               if(cardType.equals("latest")){
                  allSongListItems.sort(new SortSongByLatest());
               }
               else {
                  allSongListItems.sort(new SortSongByPlayCount());
               }

               songListAdapter.notifyDataSetChanged();
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
   }

}