package com.example.mixbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.mixbox.R;

import androidx.recyclerview.widget.RecyclerView;


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

        //classwork 12
        holder.menuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.menuOptions);

                //popup.inflate(R.menu.options_menu)
                /*
                Intent intent = new Intent(context, DetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("title", movie.getTitle());
                bundle.putString("overview", movie.getOverview());
                bundle.putString("poster", movie.getPoster());
                bundle.putDouble("rating", movie.getRating());

                intent.putExtras(bundle);
                context.startActivity(intent);
                */

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

        TextView menuOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.music_title);
            artistName = itemView.findViewById(R.id.music_artist);
            menuOptions =itemView.findViewById(R.id.menu_options);
        }

    }
}
