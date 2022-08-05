package com.example.mixbox.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mixbox.R;

import androidx.cardview.widget.CardView;

import com.example.mixbox.fragments.OnSongClickListener;

import com.example.mixbox.fragments.SearchSongFragment;
import com.example.mixbox.fragments.SongListFragment;
import com.example.mixbox.fragments.SongPlayFragment;
import com.example.mixbox.model.FragmentInfo;
import com.example.mixbox.model.SongListModel;
import com.example.mixbox.utilities.PlaylistDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class RecyclerSongListAdapter extends RecyclerView.Adapter<RecyclerSongListAdapter.ViewHolder> {
   private Context context;
   //String type;
   //String playlistName;
   FragmentInfo fInfo;
   ArrayList<SongListModel> songList;
   FirebaseDatabase db;
   String currentUserEmail;
   OnSongClickListener listener;
   FirebaseStorage storage;
   StorageReference storageRef;

   //HashSet<String> typeSet = new HashSet<>(Arrays.asList("rock", "edm", "rnb", "latest", "mostPlayed", "favorite"));

   public RecyclerSongListAdapter(Context context, ArrayList<SongListModel> songList, FragmentInfo info, OnSongClickListener listener){
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.fInfo = info;

    }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item2, parent, false);
      ViewHolder viewHolder = new ViewHolder(v);
      db = FirebaseDatabase.getInstance();
      storage = FirebaseStorage.getInstance();
      storageRef = storage.getReference();
      FirebaseAuth auth = FirebaseAuth.getInstance();
      currentUserEmail = auth.getCurrentUser().getEmail().toString();

      /*
      storage = FirebaseStorage.getInstance();
      storageRef = storage.getReference();-0
      isOwner = true;
      //playerControlView = binding.playerControlViewScroll;

       */

      return viewHolder;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      SongListModel songItem = songList.get(position);
      holder.artistName.setText(songItem.getArtistName());
      holder.songTitle.setText(songItem.getSong().getSongName());
      holder.playCount.setText(Integer.toString(songItem.getSong().getTimesPlayed()));

      String albumCoverName = songItem.getSong().getSongName().split("\\.")[0] + ".png";
      Log.d("---", "album name: " + albumCoverName);

      storageRef.child("albumcover/"+albumCoverName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

         @Override
         public void onSuccess(Uri uri) {
            Log.d("---", "image URI : " + uri);
            Glide.with(context ) //context
                    .load(uri)
                    .into(holder.imageView);

         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Log.e("---", "Error: " + e);
         }
      });


      holder.cardView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if(listener != null){
               listener.onSongClick(songItem);
             }
         }
      });

      //classwork 12
      holder.menuOptions.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            PopupMenu popup = new PopupMenu(context, holder.menuOptions);
            popup.inflate(R.menu.options_menu);



            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                  switch (item.getItemId()) {
                     case R.id.play:
                        //handle menu1 click
                        listener.onPlayerStop();
                        Bundle bundle = new Bundle();
                        if(fInfo.getType() != ""){
                           bundle.putString("type", fInfo.getType());
                           if(fInfo.getType() == "search") {
                              //bundle.putString("searchKeyword", fInfo.getSearchKeyword());
                           }
                           Log.d("---", " [RecyclerSongListAdapter] Song List Type = " + fInfo.getType());
                        }
                        if(fInfo.getPlaylistName() != ""){
                           bundle.putString("playlistName",fInfo.getPlaylistName());
                           Log.d("---", " [RecyclerSongListAdapter] Song List playlistName = " + fInfo.getPlaylistName());
                        }

                        bundle.putString("title", songItem.getSong().getSongName());
                        bundle.putString("artist", songItem.getArtistName());
                        String albumCoverName = songItem.getSong().getSongName().split("\\.")[0] + ".png";
                        bundle.putString("albumCover",albumCoverName);
                        SongPlayFragment fragment = new SongPlayFragment();
                        fragment.setArguments(bundle);
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();

                        break;
                     case R.id.addFav:
                        addToFavorite(songItem.getSong().getSongName());
                        break;
                     case R.id.removeFav:
                        removeFromFavorite(songItem.getSong().getSongName());
                        break;
                     case R.id.addToPlaylist:
                        addToPlaylist(songItem.getSong().getSongName());
                        break;
                     case R.id.removeFromPlaylist:
                        removeFromPlaylist(songItem.getSong().getSongName());
                        break;
                  }
                  return false;
               }
            });
            popup.show();

         }
      });
   }

   private void removeFromPlaylist(String songName) {
      PlaylistDialog dialog = new PlaylistDialog(context, songName, "Remove");
      FragmentActivity f = (FragmentActivity)context;
      dialog.show(f.getSupportFragmentManager(), "Remove from playlist");
   }

   private void addToPlaylist(String songName) {
      PlaylistDialog dialog = new PlaylistDialog(context, songName, "Add");
      FragmentActivity f = (FragmentActivity)context;
      dialog.show(f.getSupportFragmentManager(), "Add to Playlist");
   }

   private void removeFromFavorite(String songName) {
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  if(eachUser.get("email").equals(currentUserEmail)){
                     HashMap<String, Object> favoriteSongs = (HashMap<String, Object>) eachUser.get("favoriteSongs");

                     if(favoriteSongs == null){
                        Toast.makeText(context, "Not a favorite song!!", Toast.LENGTH_SHORT).show();
                     }
                     else{
                        Log.e("here", "here");
                        boolean isFav = false;
                        String remove = "";

                        Object[] obj = favoriteSongs.keySet().toArray();

                        for(int i = 0; i < favoriteSongs.keySet().size(); i++){
                           if(favoriteSongs.get(obj[i].toString()).toString().equals(songName)){
                              isFav = true;
                              remove = obj[i].toString();
                           }
                        }

                        if(isFav){
                           favoriteSongs.remove(remove);
                           eachUser.put("favoriteSongs", favoriteSongs);

                           db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                    Toast.makeText(context, "Successfully removed from favorites!", Toast.LENGTH_SHORT).show();

                                    AppCompatActivity activity = (AppCompatActivity) context;

                                    Bundle bundle = new Bundle();
                                    bundle.putString("type", "favorite");
                                    SongListFragment fragment = new SongListFragment();
                                    fragment.setArguments(bundle);
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                                 }
                                 else{
                                    Toast.makeText(context, "Failed to add to favorite! Try again later!", Toast.LENGTH_SHORT).show();
                                    Log.e("--", task.getException().getMessage());
                                 }
                              }
                           });
                        }
                        else{
                           Toast.makeText(context, "Not a favorite Song !!", Toast.LENGTH_SHORT).show();
                        }
                     }
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
      return songList.size();
   }

   private void addToFavorite(String songName) {
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  if(eachUser.get("email").equals(currentUserEmail)){
                     HashMap<String, Object> favoriteSongs = (HashMap<String, Object>) eachUser.get("favoriteSongs");
                     boolean alreadyFavorite = false;

                     if(favoriteSongs != null){
                        for( Object o : favoriteSongs.values()){
                           if(o.toString().equals(songName)){
                              Toast.makeText(context, "Already a favorite song !!", Toast.LENGTH_SHORT).show();
                              alreadyFavorite = true;
                           }
                        }
                     }


                     if(!alreadyFavorite){
                        if(favoriteSongs == null){
                           favoriteSongs = new HashMap<>();
                        }

                        String u = UUID.randomUUID().toString();
                        favoriteSongs.put(u, songName);
                        eachUser.put("favoriteSongs", favoriteSongs);

                        db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                 Toast.makeText(context, "Successfully added to favorites!", Toast.LENGTH_SHORT).show();
                              }
                              else{
                                 Toast.makeText(context, "Failed to add to favorite! Try again later!", Toast.LENGTH_SHORT).show();
                                 Log.e("--", task.getException().getMessage());
                              }
                           }
                        });
                     }
                  }
               }
            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });

   }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView songTitle;
       TextView artistName;
       TextView playCount;
       TextView menuOptions;
       CardView cardView;
       ImageView imageView;

       public ViewHolder(@NonNull View itemView) {
          super(itemView);
          songTitle = itemView.findViewById(R.id.music_title);
          artistName = itemView.findViewById(R.id.music_artist);
          menuOptions = itemView.findViewById(R.id.menu_options);
          playCount = itemView.findViewById(R.id.play_count);
          cardView = itemView.findViewById(R.id.card_view);
          imageView = itemView.findViewById(R.id.music_image);
       }

    }

}







































