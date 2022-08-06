package com.example.mixbox.fragments;

import com.example.mixbox.model.SongListModel;

public interface OnSongClickListener {
    void onSongClick(SongListModel songListModel);

    void onPlayerStop();

}
