package com.example.mixbox.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixbox.R;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.SongListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaylistDialog extends AppCompatDialogFragment {
   RecyclerView recyclerView;
   Context context ;
   String sName ;
   List<String> allPlaylists;
   FirebaseDatabase db;
   FirebaseAuth auth;

   public PlaylistDialog(Context ctx, String songName){
      context = ctx;
      this.sName = songName;
      allPlaylists = new ArrayList<>();
   }


   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      allPlaylists.clear();
      db = FirebaseDatabase.getInstance();
      auth = FirebaseAuth.getInstance();

      getAllPlayLists();

      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      LayoutInflater inflater = getActivity().getLayoutInflater();
      View view = inflater.inflate(R.layout.layout_addplaylist, null);

      recyclerView = view.findViewById(R.id.playlistRecyclerView);

      builder.setView(view).setTitle("Add to playlist");

      return builder.create();
   }

   public void getAllPlayLists(){
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

                  HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("playlists");

                  if (eachUserSongs != null) {
                     for (Object song : eachUserSongs.values()) {
                        HashMap<String, Object> eachSong = (HashMap<String, Object>) song;

                        Log.e("---", eachSong.get("songName").toString());

                        SongListModel recyclerViewModelObject = new SongListModel();

                        String name = eachUser.get("fullName").toString();
                        recyclerViewModelObject.setArtistName(name);

                        recyclerViewModelObject.
                          setSong(new Song(eachSong.get("songName").toString(),
                            Integer.parseInt(eachSong.get("timesPlayed").toString()),
                            LocalDateTime.parse(eachSong.get("dateTime").toString()),
                            null));

                       // allSongs.add(recyclerViewModelObject);

                     }
                  }
               }

            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });


   }
}











































