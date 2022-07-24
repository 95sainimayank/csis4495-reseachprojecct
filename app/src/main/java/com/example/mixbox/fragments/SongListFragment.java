package com.example.mixbox.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mixbox.R;
import com.example.mixbox.adapter.RecyclerSongListAdapter;
import com.example.mixbox.databinding.FragmentSongListBinding;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.SongListModel;
import com.example.mixbox.utilities.SortSongByLatest;
import com.example.mixbox.utilities.SortSongByPlayCount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class SongListFragment extends Fragment implements OnSongClickListener {
   FragmentSongListBinding binding;
   FirebaseDatabase db;
   ArrayList<SongListModel> allSongListItems;
   RecyclerSongListAdapter songListAdapter;
   FirebaseAuth auth;
   ArrayList<SongListModel> allSongs;

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
      auth = FirebaseAuth.getInstance();
      binding.songlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      allSongListItems = new ArrayList<>();
      allSongs = new ArrayList<>();

      songListAdapter = new RecyclerSongListAdapter(getActivity(), allSongListItems, this);

      binding.songlistRecyclerView.setAdapter(songListAdapter);

      binding.sTitle.setText("Not selected");
      binding.sArtist.setText("Not selected");
      String type = "";

      if(getArguments().get("type") != null)
         type = getArguments().get("type").toString();

      binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if(getArguments().get("playlistName") == null)
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
            else
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PlaylistFragment()).commit();
         }
      });

      if(getArguments().get("playlistName") != null){
         getAllSongs("playlist");
         showPlaylistSongs(getArguments().get("playlistName").toString());
      }

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
         case "favorite":
            getAllSongs("favorite");
         default:
            break;
      }
      return binding.getRoot();
   }

   private void showPlaylistSongs(String playlistName) {
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  if (eachUser.get("email").equals(auth.getCurrentUser().getEmail())) {
                     HashMap<String, Object> playlists = (HashMap<String, Object>) eachUser.get("playlists");

                     if (playlists != null) {
                        Set<String> strings = playlists.keySet();

                        for (String s : strings) {
                           HashMap<String, Object> eachPlaylist = (HashMap<String, Object>) playlists.get(s);

                           if (eachPlaylist.get("name").toString().equals(playlistName)) {

                              HashMap<String, Object> songs = (HashMap<String, Object>) eachPlaylist.get("songs");

                              if(songs != null){
                                 for(Object obj : songs.values()){
                                    String songName = obj.toString();

                                    for(int i = 0; i < allSongs.size(); i++){
                                       if(allSongs.get(i).getSong().getSongName().equals(songName)){
                                          allSongListItems.add(allSongs.get(i));
                                       }
                                    }

                                 }
                              }

                           }
                        }
                     }

                     songListAdapter.notifyDataSetChanged();
                  }

               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
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

   public void getAllSongs(String val) {
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

                        SongListModel recyclerViewModelObject = new SongListModel();

                        String name = eachUser.get("fullName").toString();
                        recyclerViewModelObject.setArtistName(name);

                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(eachSong.get("dateTime").toString()),
                            null));

                        allSongs.add(recyclerViewModelObject);

                     }
                  }
               }

            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });

      if(val.equals("favorite"))
         showFavoriteList();
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

   public void showSongBasedOnMainCards(String cardType) {
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

                        allSongListItems.add(recyclerViewModelObject);

                     }
                  }
               }

               if (cardType.equals("latest")) {
                  allSongListItems.sort(new SortSongByLatest());
               } else {
                  allSongListItems.sort(new SortSongByPlayCount());
               }

               songListAdapter.notifyDataSetChanged();
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
   }

   private void showFavoriteList() {
      String email = auth.getCurrentUser().getEmail();

      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  if (eachUser.get("email").equals(email)) {
                     HashMap<String, Object> favoriteSongsFromDb = (HashMap<String, Object>) eachUser.get("favoriteSongs");

                     if (favoriteSongsFromDb != null) {
                        for (Object song : favoriteSongsFromDb.values()) {
                          String songName = song.toString();

                          for(SongListModel s : allSongs){
                             if(s.getSong().getSongName().equals(songName)){
                                SongListModel recyclerViewModelObject = new SongListModel();

                                String name = eachUser.get("fullName").toString();
                                recyclerViewModelObject.setArtistName(name);

                                recyclerViewModelObject.
                                  setSong(new Song(s.getSong().getSongName().toString(),
                                    s.getSong().getTimesPlayed(),
                                    LocalDateTime.parse(s.getSong().getDateTime().toString()),
                                    null));

                                allSongListItems.add(recyclerViewModelObject);
                                return;
                             }
                          }
                        }
                     } else {
                        Toast.makeText(getActivity(), "No Favorite song present !!", Toast.LENGTH_SHORT).show();
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

   @Override
   public void onSongClick(SongListModel songListModel) {
      binding.sTitle.setText(songListModel.getSong().getSongName());
      binding.sArtist.setText(songListModel.getArtistName());
   }
}











































