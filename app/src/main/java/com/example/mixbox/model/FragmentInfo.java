package com.example.mixbox.model;

public class FragmentInfo {
    String type;
    String playlistName;

    public FragmentInfo(String type, String playlistName) {
        this.type = type;
        this.playlistName = playlistName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

}
