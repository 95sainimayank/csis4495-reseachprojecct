package com.example.mixbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixbox.R;
import com.example.mixbox.model.SongListModel;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {
   Context context;
   ArrayList<SongListModel> songList;

   public SongListAdapter(Context context, ArrayList<SongListModel> songList) {
      this.context = context;
      this.songList = songList;
   }


   @NonNull
   @Override
   public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
      SongViewHolder viewHolder = new SongViewHolder(v);
      return viewHolder;
   }

   @Override
   public int getItemCount() {
      return songList.size();
   }

   @Override
   public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
      SongListModel songItem = songList.get(position);
      holder.artistName.setText(songItem.getArtistName());
      holder.songTitle.setText(songItem.getSong().getSongName());
      holder.playCount.setText(Integer.toString(songItem.getSong().getTimesPlayed()));
   }

   public class SongViewHolder extends RecyclerView.ViewHolder {
      TextView songTitle;
      TextView artistName;
      TextView playCount;

      public SongViewHolder(@NonNull View itemView) {
         super(itemView);
         songTitle = itemView.findViewById(R.id.songTitle);
         artistName = itemView.findViewById(R.id.artistName);
         playCount = itemView.findViewById(R.id.playCount);
      }

   }

}
