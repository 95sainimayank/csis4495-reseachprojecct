package com.example.mixbox.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixbox.R;
import com.example.mixbox.adapter.PlaylistAdapter;
import com.example.mixbox.adapter.RecyclerSongListAdapter;
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
   Context context;
   String sName;
   List<String> allPlaylists;
   FirebaseDatabase db;
   FirebaseAuth auth;
   String action;

   public PlaylistDialog(Context ctx, String songName, String ac) {
      context = ctx;
      this.sName = songName;
      allPlaylists = new ArrayList<>();
      action = ac;
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      allPlaylists.clear();
      db = FirebaseDatabase.getInstance();
      auth = FirebaseAuth.getInstance();

      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      LayoutInflater inflater = getActivity().getLayoutInflater();
      View view = inflater.inflate(R.layout.layout_addplaylist, null);

      recyclerView = view.findViewById(R.id.playlistRecyclerView);

      builder.setView(view).setTitle("Add/Remove");

      getAllPlayLists();
      return builder.create();
   }

   public void getAllPlayLists() {
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

                  if (eachUser.get("email").equals(auth.getCurrentUser().getEmail())) {
                     HashMap<String, Object> playlists = (HashMap<String, Object>) eachUser.get("playlists");

                     if (playlists != null) {
                        for (Object eachPlaylist : playlists.values()) {
                           HashMap<String, Object> playlist = (HashMap<String, Object>) eachPlaylist;

                           String playlistName = playlist.get("name").toString();
                           allPlaylists.add(playlistName);
                        }
                     }

                     recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                     recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                     PlaylistAdapter adapter = new PlaylistAdapter(allPlaylists, sName, getActivity(), "dialog", action);
                     recyclerView.setAdapter(adapter);

                     break;
                  }
               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });
   }
}











































