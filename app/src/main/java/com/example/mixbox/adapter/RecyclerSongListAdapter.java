package com.example.mixbox.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.mixbox.R;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mixbox.fragments.HomeFragment;
import com.example.mixbox.fragments.SongPlayFragment;
import com.example.mixbox.model.SongListModel;

import java.util.ArrayList;


public class RecyclerSongListAdapter extends RecyclerView.Adapter<RecyclerSongListAdapter.ViewHolder> {

    ArrayList<SongListModel> songList;
    private Context context;

    public RecyclerSongListAdapter(Context context, ArrayList<SongListModel> songList){
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item2, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongListModel songItem = songList.get(position);
        holder.artistName.setText(songItem.getArtistName());
        holder.songTitle.setText(songItem.getSong().getSongName());
        holder.playCount.setText(Integer.toString(songItem.getSong().getTimesPlayed()));

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
                                Bundle bundle = new Bundle();
                                bundle.putString("title", songItem.getSong().getSongName());
                                bundle.putString("artist", songItem.getArtistName());
                                SongPlayFragment fragment = new SongPlayFragment();
                                fragment.setArguments(bundle);
                                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();

                                break;
                            case R.id.addFav:
                                //handle menu2 click
                                break;
                            case R.id.removeFav:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView artistName;
        TextView playCount;
        TextView menuOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.music_title);
            artistName = itemView.findViewById(R.id.music_artist);
            menuOptions =itemView.findViewById(R.id.menu_options);
            playCount = itemView.findViewById(R.id.play_count);

        }

    }
}
