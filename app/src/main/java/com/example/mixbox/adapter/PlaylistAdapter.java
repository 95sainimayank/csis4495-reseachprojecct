package com.example.mixbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixbox.R;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.CustomViewHolder> {
   List<String> allPlaylists;

   public PlaylistAdapter(List<String> allPlaylists){
      this.allPlaylists = allPlaylists;
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
   }

   @Override
   public int getItemCount() {
      return allPlaylists.size();
   }

   public class CustomViewHolder extends RecyclerView.ViewHolder{
      TextView playlistName;

      public CustomViewHolder(@NonNull View itemView) {
         super(itemView);
         playlistName = itemView.findViewById(R.id.playlistName);
      }
   }

}









































