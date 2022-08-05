package com.example.mixbox.fragments;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.mixbox.databinding.FragmentSearchSongBinding;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.SongListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchSongFragment extends Fragment {
   FragmentSearchSongBinding binding;
   FirebaseDatabase db;
   ArrayList<SongListModel> allSongListItems;
   RecyclerSongListAdapter songListAdapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentSearchSongBinding.inflate(inflater, container, false);

      binding.searchSongToolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
         }
      });

      binding.searchedSongRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      allSongListItems = new ArrayList<>();
      songListAdapter = new RecyclerSongListAdapter(getActivity(), allSongListItems, "",null);
      binding.searchedSongRecyclerView.setAdapter(songListAdapter);

      binding.btnSearchSong.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String songName = binding.songText.getText().toString();

            getSelectedSongs(songName);
         }
      });

      return binding.getRoot();
   }

   public void getSelectedSongs(String songName) {
      allSongListItems.clear();
      db = FirebaseDatabase.getInstance();

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
                        Log.e("filter", eachSong.get("songName").toString());

                        if (eachSong.get("songName").toString().toLowerCase().contains(songName.toLowerCase())) {
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

                           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:mm");


                           recyclerViewModelObject.
                             setSong(new Song(eachSong.get("songName").toString(),
                               Integer.parseInt(eachSong.get("timesPlayed").toString()),
                               LocalDateTime.parse(d, formatter),
                               null));

                           Log.e("---", recyclerViewModelObject.getSong().getSongName());

                           allSongListItems.add(recyclerViewModelObject);
                        }

                     }
                  }
               }
               
               if(allSongListItems.size() == 0){
                  Toast.makeText(getActivity(), "No Song with that name exists !", Toast.LENGTH_SHORT).show();
               }

               songListAdapter.notifyDataSetChanged();
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

}









































