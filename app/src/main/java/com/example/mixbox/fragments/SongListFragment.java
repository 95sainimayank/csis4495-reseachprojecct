package com.example.mixbox.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mixbox.R;
import com.example.mixbox.adapter.RecyclerSongListAdapter;
import com.example.mixbox.databinding.FragmentSongListBinding;
import com.example.mixbox.model.FragmentInfo;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.SongListModel;
import com.example.mixbox.service.CreateNotification;
import com.example.mixbox.utilities.SortSongByLatest;
import com.example.mixbox.utilities.SortSongByPlayCount;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SongListFragment extends Fragment implements OnSongClickListener {
   private static final String ACTION_VIEW = "com.example.mixbox.fragments.action.VIEW";
   private static final String EXTENSION_EXTRA = "extension";
   private static final String DRM_SCHEME_EXTRA = "drm_scheme";
   private static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
   private static final String OWNER_EXTRA = "owner";
   @Nullable
   private static ExoPlayer player;
   private static String DEFAULT_MEDIA_URI = "";
   FragmentSongListBinding binding;
   FirebaseDatabase db;
   ArrayList<SongListModel> allSongListItems;
   RecyclerSongListAdapter songListAdapter;
   FirebaseAuth auth;
   ArrayList<SongListModel> allSongs;
   //For Player -------------------------------------------------start
   FirebaseStorage storage;
   StorageReference storageRef;
   private boolean isPlayerStart = false;
   @Nullable
   private PlayerControlView playerControlView;
   //For Player -------------------------------------------------end
   NotificationManager notificationManager;
   String type;

   public SongListFragment() {
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentSongListBinding.inflate(inflater, container, false);

      Log.d("---","[SongListFragment#onCreateView]");
      db = FirebaseDatabase.getInstance();
      auth = FirebaseAuth.getInstance();

      allSongListItems = new ArrayList<>();
      allSongs = new ArrayList<>();

      binding.sTitleScroll.setText("Song Title");
      binding.sTitleScroll.setSelected(true);
      binding.sArtistScroll.setText("Artist");
      binding.sTitleScroll.setSelected(true);
      binding.sSongInfoScroll.setText("Detail Song Information");

      storage = FirebaseStorage.getInstance();
      storageRef = storage.getReference();

      playerControlView = binding.playerControlViewScroll;

      type = "";

      if (getArguments().get("type") != null)
         type = getArguments().get("type").toString();

      Log.d("---", " [SongListFragment] Song List Type = " + type);
      binding.songlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      FragmentInfo info = new FragmentInfo(type, getArguments().get("playlistName") == null ? "":getArguments().get("playlistName").toString());

      if(getArguments().get("profile") == null){
         songListAdapter = new RecyclerSongListAdapter(getActivity(), allSongListItems, info, this, false);
      }
      else{
         songListAdapter = new RecyclerSongListAdapter(getActivity(), allSongListItems, info, this, true);
      }

      binding.songlistRecyclerView.setAdapter(songListAdapter);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         createChannel();
         getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
      }

      binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Log.d("---", "Back to HomeFragment");
            if (player != null) {
               player.stop();
            }

            if (getArguments().get("playlistName") == null && getArguments().get("profile") == null) {
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
            }
            else if (getArguments().get("profile") != null){
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new UserProfileFragment()).commit();
            }
              else {
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PlaylistFragment()).commit();
            }
         }
      });

      if (getArguments().get("playlistName") != null) {
         getAllSongs("playlist");
         showPlaylistSongs(getArguments().get("playlistName").toString());
      }

      if(getArguments().get("profile") != null){
         getSongsOfCurrentUser();
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
            showFavoriteList();
         default:
            break;
      }

      Log.d("---", "Initialize ExoPlayer.");
      initializePlayer();


      return binding.getRoot();
   }

   private void getSongsOfCurrentUser() {
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
                     HashMap<String, Object> currentUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                     if(currentUserSongs == null){
                        Toast.makeText(getActivity(), "No Songs uploaded!", Toast.LENGTH_SHORT).show();
                     }
                     else{
                        for(Object song : currentUserSongs.values()){
                           HashMap<String, Object> eachSong = (HashMap<String, Object>) song;

                           SongListModel songListModel = new SongListModel();
                           songListModel.setArtistName(auth.getCurrentUser().getEmail());

                           String d = "";

                           JSONObject jobj = new JSONObject();
                           try {
                              jobj = new JSONObject(eachSong.get("dateTime").toString());
                           } catch (JSONException e) {
                              e.printStackTrace();
                           }

                           try {
                              int ho = 0;
                              if(Integer.parseInt(jobj.getString("hour")) > 12){
                                 ho = Integer.parseInt(jobj.getString("hour")) - 12;
                              }

                              d = jobj.getString("year") + "-"+
                                jobj.getString("monthValue") + "-" + jobj.getString("dayOfMonth") + " " + ho + ":" + jobj.getString("minute");
                           } catch (JSONException e) {
                              Log.e("hahah", e.getMessage().toString());
                           }

                           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m");

                           List<String> genres = new ArrayList<>();
                           HashMap<String, Object> genreMap = (HashMap<String, Object>) eachSong.get("genre");
                           for (Object genre : genreMap.values()) {
                              genres.add(genre.toString());
                           }

                           Song s = new Song(
                                            eachSong.get("songName").toString(),
                                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                                            LocalDateTime.parse(d, formatter),
                                            genres);

                           songListModel.setSong(s);

                           allSongListItems.add(songListModel);
                        }
                     }

                     Log.d("---", "getSongsOfCurrentUser_Adapter.notifyDataSetChanged");
                     songListAdapter.notifyDataSetChanged();
                  }

               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });

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

                              if (songs != null) {
                                 for (Object obj : songs.values()) {
                                    String songName = obj.toString();

                                    for (int i = 0; i < allSongs.size(); i++) {
                                       if (allSongs.get(i).getSong().getSongName().equals(songName)) {
                                          allSongListItems.add(allSongs.get(i));
                                       }
                                    }

                                 }
                              }

                           }
                        }
                     }

                     Log.d("---", "showPlaylistSongs_Adapter.notifyDataSetChanged");
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
   public void onPause() {
      super.onPause();
      Log.d("---","[SongListFragment#onPause]");
   }

   @Override
   public void onResume() {
      super.onResume();
      Log.d("---","[SongListFragment#onResume]");
      ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
   }

   @Override
   public void onStop() {
      super.onStop();

      if(player != null && isPlayerStart){
         if(player.isPlaying()){
            Log.d("---", "play.setOnClickListener_call onTrackPause");
            onTrackPlay();
         }else{
            Log.d("---", "play.setOnClickListener_call onTrackPlay");
            onTrackPause();
         }
      }

      Log.d("---","[SongListFragment#onStop]");
      ((AppCompatActivity) getActivity()).getSupportActionBar().show();
   }

   @Override
   public void onStart() {
      super.onStart();
      Log.d("---","[SongListFragment#onStart]");
      if(player != null && isPlayerStart){
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
         }
      }

   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      Log.d("---","[SongListFragment#onDetroy]");
      if (player != null) {
         player.stop();
         player.release();
         player = null;
      }

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         notificationManager.cancelAll();
      }
      getActivity().unregisterReceiver(broadcastReceiver);
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


                        String d = "";

                        JSONObject jobj = new JSONObject();
                        try {
                           jobj = new JSONObject(eachSong.get("dateTime").toString());
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }

                        try {
                           int ho = 0;
                           if(Integer.parseInt(jobj.getString("hour")) > 12){
                              ho = Integer.parseInt(jobj.getString("hour")) - 12;
                           }

                           d = jobj.getString("year") + "-"+
                             jobj.getString("monthValue") + "-" + jobj.getString("dayOfMonth") + " " + ho + ":" + jobj.getString("minute");
                        } catch (JSONException e) {
                           Log.e("hahah", e.getMessage().toString());
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m");

                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(d, formatter),
                            null));

                        allSongs.add(recyclerViewModelObject);

                        Log.d("---", "getAllSongs");

                     }
                  }
               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
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
                        List<String> genres = new ArrayList<>();
                          for (Object genre : genreMap.values()) {
                           genres.add(genre.toString());
                        }


                        for (Object genre : genreMap.values()) {
                           if (genre.toString().equals(category)) {
                              SongListModel recyclerViewModelObject = new SongListModel();

                              String name = eachUser.get("fullName").toString();
                              recyclerViewModelObject.setArtistName(name);

                              String d = "";

                              JSONObject jobj = new JSONObject();
                              try {
                                 jobj = new JSONObject(eachSong.get("dateTime").toString());
                              } catch (JSONException e) {
                                 e.printStackTrace();
                              }

                              try {
                                 int ho = 0;
                                 if(Integer.parseInt(jobj.getString("hour")) > 12){
                                    ho = Integer.parseInt(jobj.getString("hour")) - 12;
                                 }

                                 d = jobj.getString("year") + "-"+
                                   jobj.getString("monthValue") + "-" + jobj.getString("dayOfMonth") + " " + ho + ":" + jobj.getString("minute");
                              } catch (JSONException e) {
                                 Log.e("hahah", e.getMessage().toString());
                              }

                              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m");

                              recyclerViewModelObject.
                                setSong(new Song(eachSong.get("songName").toString(),
                                  Integer.parseInt(eachSong.get("timesPlayed").toString()),

                                  LocalDateTime.parse(d, formatter),
                                  genres));

                              allSongListItems.add(recyclerViewModelObject);
                           }
                        }
                     }
                  }
               }
               Log.d("---", "showSongBasedOnCategory_Adapter.notifyDataSetChanged");
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
                        List<String> genres = new ArrayList<>();
                        for (Object genre : genreMap.values()) {
                           genres.add(genre.toString());
                        }

                        SongListModel recyclerViewModelObject = new SongListModel();

                        String name = eachUser.get("fullName").toString();
                        recyclerViewModelObject.setArtistName(name);

                        String d = "";

                        JSONObject jobj = new JSONObject();
                        try {
                           jobj = new JSONObject(eachSong.get("dateTime").toString());
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }

                        try {
                           int ho = 0;
                           if(Integer.parseInt(jobj.getString("hour")) > 12){
                              ho = Integer.parseInt(jobj.getString("hour")) - 12;
                           }

                           d = jobj.getString("year") + "-"+
                             jobj.getString("monthValue") + "-" + jobj.getString("dayOfMonth") + " " + ho + ":" + jobj.getString("minute");
                        } catch (JSONException e) {
                           Log.e("hahah", e.getMessage().toString());
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m");


                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(d, formatter),
                            genres));

                        allSongListItems.add(recyclerViewModelObject);

                     }
                  }
               }

               if (cardType.equals("latest")) {
                  allSongListItems.sort(new SortSongByLatest());
               } else {
                  allSongListItems.sort(new SortSongByPlayCount());
               }

               Log.d("---", "showSongBasedOnMainCards_Adapter.notifyDataSetChanged");
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

                  HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                  if (eachUserSongs != null) {
                     for (Object song : eachUserSongs.values()) {
                        HashMap<String, Object> eachSong = (HashMap<String, Object>) song;

                        SongListModel recyclerViewModelObject = new SongListModel();

                        String name = eachUser.get("fullName").toString();
                        recyclerViewModelObject.setArtistName(name);

                        String d = "";

                        JSONObject jobj = new JSONObject();
                        try {
                           jobj = new JSONObject(eachSong.get("dateTime").toString());
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }

                        try {
                           int ho = 0;
                           if(Integer.parseInt(jobj.getString("hour")) > 12){
                              ho = Integer.parseInt(jobj.getString("hour")) - 12;
                           }

                           d = jobj.getString("year") + "-"+
                             jobj.getString("monthValue") + "-" + jobj.getString("dayOfMonth") + " " + ho + ":" + jobj.getString("minute");
                        } catch (JSONException e) {
                           Log.e("hahah", e.getMessage().toString());
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m");

                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(d, formatter),
                            null));

                        allSongs.add(recyclerViewModelObject);

                     }
                  }
               }

               db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DataSnapshot> task) {
                     if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        HashMap<String, Object> outerMapFav = (HashMap<String, Object>) snapshot.getValue();
                        HashMap<String, Object> allUsersFav = (HashMap<String, Object>) outerMapFav.get("allUsers");


                        for (Object value : allUsersFav.values()) {
                           HashMap<String, Object> eachUserFav = (HashMap<String, Object>) value;

                           if (eachUserFav.get("email").equals(email)) {
                              HashMap<String, Object> favoriteSongsFromDbFav = (HashMap<String, Object>) eachUserFav.get("favoriteSongs");


                              if (favoriteSongsFromDbFav != null) {

                                 for (Object song : favoriteSongsFromDbFav.values()) {
                                    String songName = song.toString();

                                    for (SongListModel s : allSongs) {
                                       if (s.getSong().getSongName().equals(songName)) {
                                          SongListModel recyclerViewModelObject = new SongListModel();

                                          String name = eachUserFav.get("fullName").toString();
                                          recyclerViewModelObject.setArtistName(name);

                                          recyclerViewModelObject.
                                            setSong(new Song(s.getSong().getSongName().toString(),
                                              s.getSong().getTimesPlayed(),
                                              LocalDateTime.parse(s.getSong().getDateTime().toString()),
                                              s.getSong().getGenres()));

                                          allSongListItems.add(recyclerViewModelObject);
                                       }
                                    }
                                 }
                              } else {
                                 Toast.makeText(getActivity(), "No Favorite song present !!", Toast.LENGTH_SHORT).show();
                              }
                           }
                        }

                        Log.d("---", "showFavoriteList_Adapter.notifyDataSetChanged");
                        songListAdapter.notifyDataSetChanged();
                     } else {
                        Log.e("---", task.getException().toString());
                     }
                  }
               });

            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });


   }

   @Override
   public void onSongClick(SongListModel songListModel) {
      binding.sTitleScroll.setText(songListModel.getSong().getSongName());
      binding.sArtistScroll.setText("Artist: " + songListModel.getArtistName());

      String detailInfo = "";
      //"Song Title : " + songListModel.getSong().getSongName().split("\\.")[0] + "\n";
      //detailInfo += "Artist : " + songListModel.getArtistName() + "\n";

      if(songListModel.getSong().getGenres() != null){
         String genres = "";
         for(String genre: songListModel.getSong().getGenres()){
            genres += genre + "  ";
         }
         detailInfo += "Genre: " + genres + "\n";
      }

      detailInfo += "It is played " +songListModel.getSong().getTimesPlayed()+ " times by users.\n";

      switch (type) {
         case "latest":
            detailInfo += "This song is one of the latest song.";
            break;
         case "mostPlayed":
            detailInfo += "This song is one of the most played song. ";
            break;
         case "favorite":
            detailInfo += "This song is one of your favourite song.";
         default:
            break;
      }

      binding.sSongInfoScroll.setText(detailInfo);

      if (player != null) {
         player.stop();
      }

      String albumCoverName = songListModel.getSong().getSongName().split("\\.")[0] + ".png";
      storageRef.child("albumcover/" + albumCoverName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

         @Override
         public void onSuccess(Uri uri) {
            Log.d("---", "image URI : " + uri);
            Glide.with(getActivity()) //context
              .load(uri)
              .into(binding.albumImageScroll);
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Log.e("---", "Error: " + e);
         }
      });


      String fileName = songListModel.getSong().getSongName();

      storageRef.child("music/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

         @Override
         public void onSuccess(Uri uri) {
            //DEFAULT_MEDIA_URI = uri.toString();
            Log.d("---", "URI : " + uri.toString());
            startPlayer(uri.toString());

         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Log.e("---", "Error: " + e);
         }
      });
   }

   @Override
   public void onPlayerStop() {
      if (player != null) {
         player.stop();
      }
   }


   private void initializePlayer() {
      if (player == null) {
         player = new ExoPlayer.Builder(getActivity()).build();
         Log.d("---", "Initialize Player. Player is created.");
      } else {
         Log.d("---", "Initialize Player. Player is alredy existed.");
      }
      playerControlView.setPlayer(player);
      playerControlView.show();
   }

   private void startPlayer(String mediaUri) {
      //Bundle bundle = getArguments();
      Intent intent = null;
      Uri data = null;
      //String action = intent.getAction();

      Log.d("---", "startPlayer play: uri - " + mediaUri);
      //DEFAULT_MEDIA_URI = "https://firebasestorage.googleapis.com/v0/b/hkkofirstproject.appspot.com/o/hopeful-piano-112621.mp3?alt=media&token=00a85881-aaf7-4063-be78-db9b16dfc8e7";

      String action = "";
      Uri uri =
        ACTION_VIEW.equals(action)
          //? Assertions.checkNotNull(intent.getData())
          ? Assertions.checkNotNull(data)
          : Uri.parse(mediaUri);

      DrmSessionManager drmSessionManager;
      if (intent != null && intent.hasExtra(DRM_SCHEME_EXTRA)) {
         String drmScheme = Assertions.checkNotNull(intent.getStringExtra(DRM_SCHEME_EXTRA));
         String drmLicenseUrl = Assertions.checkNotNull(intent.getStringExtra(DRM_LICENSE_URL_EXTRA));
         UUID drmSchemeUuid = Assertions.checkNotNull(Util.getDrmUuid(drmScheme));
         HttpDataSource.Factory licenseDataSourceFactory = new DefaultHttpDataSource.Factory();
         HttpMediaDrmCallback drmCallback =
           new HttpMediaDrmCallback(drmLicenseUrl, licenseDataSourceFactory);
         drmSessionManager =
           new DefaultDrmSessionManager.Builder()
             .setUuidAndExoMediaDrmProvider(drmSchemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
             .build(drmCallback);
      } else {
         drmSessionManager = DrmSessionManager.DRM_UNSUPPORTED;
      }

      DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(getActivity());

      MediaSource mediaSource;
      @C.ContentType int type = Util.inferContentType(uri, (intent == null ? null : intent.getStringExtra(EXTENSION_EXTRA)));

      Log.d("---", "startPlayer play___#4 type:" + Integer.toString(type) + "---");
      if (type == C.TYPE_DASH) {
         mediaSource =
           new DashMediaSource.Factory(dataSourceFactory)
             .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
             .createMediaSource(MediaItem.fromUri(uri));
      } else if (type == C.TYPE_OTHER) {
         mediaSource =
           new ProgressiveMediaSource.Factory(dataSourceFactory)
             .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
             .createMediaSource(MediaItem.fromUri(uri));
      } else {
         throw new IllegalStateException();
      }

      //ExoPlayer player = new ExoPlayer.Builder(getActivity()).build();

      player.setMediaSource(mediaSource);
      player.prepare();
      player.play();
      player.setRepeatMode(Player.REPEAT_MODE_ALL);
      isPlayerStart = true;
   }

   private void createChannel() {
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                 "MIXBOX", NotificationManager.IMPORTANCE_LOW);

         notificationManager = requireContext().getSystemService(NotificationManager.class);
         if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
         }
      }
   }

   BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getExtras().getString("actionname");
         switch(action){
            case CreateNotification.ACTION_PREVIOUS:
               //
               break;
            case CreateNotification.ACTION_PLAY:
               if(player != null && player.isPlaying()){
                  Log.d("---", "Broadcastreceiver_call onTrackPause");
                  onTrackPause();
               }else{
                  Log.d("---", "Broadcastreceiver_call onTrackPlay");
                  onTrackPlay();
               }
               break;
            case CreateNotification.ACTION_NEXT:
               //
               break;
         }
      }
   };

   private void onTrackPlay() {

      Bitmap icon  =((BitmapDrawable)binding.albumImageScroll.getDrawable()).getBitmap();
      CreateNotification.createNotification(getActivity(), binding.sTitleScroll.getText().toString(),
              binding.sArtistScroll.getText().toString(), icon, R.drawable.ic_pause_24);
      if(player != null & !player.isPlaying()){
         player.play();
      }
   }

   private void onTrackPause() {
      Bitmap icon =((BitmapDrawable)binding.albumImageScroll.getDrawable()).getBitmap();
      CreateNotification.createNotification(getActivity(), binding.sTitleScroll.getText().toString(),
              binding.sArtistScroll.getText().toString(), icon, R.drawable.ic_play_arrow_24);

      if(player != null && player.isPlaying()){
         player.pause();
      }
   }
}











































