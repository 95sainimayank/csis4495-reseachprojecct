package com.example.mixbox.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixbox.R;
import com.example.mixbox.fragments.PlaylistFragment;
import com.example.mixbox.fragments.SongListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.CustomViewHolder> {
   List<String> allPlaylists;
   String songName;
   Context context;
   String page;
   String action;

   public PlaylistAdapter(List<String> allPlaylists, String sName, Context ctx, String p, String a) {
      this.allPlaylists = allPlaylists;
      songName = sName;
      context = ctx;
      page = p;
      action = a;
   }

   @NonNull
   @Override
   public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
      PlaylistAdapter.CustomViewHolder viewHolder = new CustomViewHolder(v);

      return viewHolder;
   }

   @Override
   public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
      holder.playlistName.setText(allPlaylists.get(position));

      holder.optionsOfMenu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            PopupMenu popup = new PopupMenu(context, holder.optionsOfMenu);
            popup.inflate(R.menu.playlist_menu);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem menuItem) {
                  switch (menuItem.getItemId()){
                     case R.id.deletePlaylist:
                        deletePlaylist(allPlaylists.get(position));
                        break;

                  }
                  return  false;
               }
            });

            popup.show();
         }
      });

      holder.playlistName.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseDatabase db = FirebaseDatabase.getInstance();

            if (page.equals("dialog")) {
               if(action.equals("Add")){

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

                                       if (eachPlaylist.get("name").toString().equals(allPlaylists.get(position))) {

                                          HashMap<String, Object> songs = (HashMap<String, Object>) eachPlaylist.get("songs");


                                          boolean songExists = false;

                                          if (songs != null) {
                                             for (Object obj : songs.values()) {
                                                if (obj.equals(songName)) {
                                                   songExists = true;
                                                   Toast.makeText(context, "Songs already in the playlist " + eachPlaylist.get("name"), Toast.LENGTH_SHORT).show();
                                                }
                                             }
                                          }

                                          if (!songExists) {
                                             if (songs == null)
                                                songs = new HashMap<>();

                                             songs.put(UUID.randomUUID().toString(), songName);
                                             eachPlaylist.put("songs", songs);

                                             db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()) {
                                                      Toast.makeText(context, "Successfully added to playlist " + eachPlaylist.get("name"), Toast.LENGTH_SHORT).show();
                                                   } else {
                                                      Toast.makeText(context, "Failed to add to playlist! Try again later!", Toast.LENGTH_SHORT).show();
                                                      Log.e("--", task.getException().getMessage());
                                                   }
                                                }
                                             });
                                          }

                                       }
                                    }
                                 }

                                 break;
                              }

                           }
                        } else {
                           Log.e("---", task.getException().toString());
                        }
                     }
                  });
               }
               else{

                  String playlistName = allPlaylists.get(position);

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

                                          boolean containsSong = false;
                                          String songKey = "";

                                          if(songs != null){
                                             Object[] obj = songs.keySet().toArray();


                                             for(int i = 0; i < songs.keySet().size(); i++){
                                                if(songs.get(obj[i].toString()).toString().equals(songName)){
                                                   containsSong = true;
                                                   songKey = obj[i].toString();
                                                }
                                             }

                                             if(containsSong){
                                                songs.remove(songKey);
                                                eachPlaylist.put("songs", songs);

                                                db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                      if(task.isSuccessful()){
                                                         Toast.makeText(context, "Successfully removed from playlist!", Toast.LENGTH_SHORT).show();

                                                         AppCompatActivity activity = (AppCompatActivity) context;
                                                         Bundle bundle = new Bundle();
                                                         bundle.putString("playlistName", playlistName);
                                                         SongListFragment fragment = new SongListFragment();
                                                         fragment.setArguments(bundle);
                                                         activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                                                      }
                                                      else{
                                                         Toast.makeText(context, "Failed to remove! Try again later!", Toast.LENGTH_SHORT).show();
                                                         Log.e("--", task.getException().getMessage());
                                                      }
                                                   }
                                                });
                                             }
                                             else{
                                                Toast.makeText(context, "Not present in the playlist!", Toast.LENGTH_SHORT).show();
                                             }
                                          }
                                          else{
                                             Toast.makeText(context, "Song does not exist in the playlist!", Toast.LENGTH_SHORT).show();
                                          }


                                       }
                                    }
                                 }

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
            else{
               Bundle bundle = new Bundle();
               bundle.putString("playlistName", allPlaylists.get(position));
               SongListFragment songListFragment = new SongListFragment();
               songListFragment.setArguments(bundle);
               ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, songListFragment).commit();
            }
         }
      });
   }

   private void deletePlaylist(String playlistName) {
      FirebaseAuth auth = FirebaseAuth.getInstance();
      FirebaseDatabase db = FirebaseDatabase.getInstance();

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
                              playlists.remove(s);
                              break;
                           }
                        }

                        eachUser.put("playlists", playlists);

                        db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                 Toast.makeText(context, "Successfully removed playlist!", Toast.LENGTH_SHORT).show();

                                 AppCompatActivity activity = (AppCompatActivity) context;
                                 activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PlaylistFragment()).commit();

                              }
                              else{
                                 Toast.makeText(context, "Failed to remove playlist! Try again later!", Toast.LENGTH_SHORT).show();
                                 Log.e("--", task.getException().getMessage());
                              }
                           }
                        });

                     }

                     break;
                  }

               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });


   }

   @Override
   public int getItemCount() {
      return allPlaylists.size();
   }

   public class CustomViewHolder extends RecyclerView.ViewHolder {
      TextView playlistName;
      TextView optionsOfMenu;

      public CustomViewHolder(@NonNull View itemView) {
         super(itemView);
         playlistName = itemView.findViewById(R.id.playlistName);
         optionsOfMenu = itemView.findViewById(R.id.deletePlaylistView);
      }
   }

}









































