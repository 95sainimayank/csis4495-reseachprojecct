package com.example.mixbox.model;

public class FragmentInfo {
    String type;
    String playlistName;
    String searchKeyword;

    public FragmentInfo(String type, String playlistName) {
        this.type = type;
        this.playlistName = playlistName;
        this.searchKeyword = "";
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

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyWord) {
        this.searchKeyword = searchKeyWord;
    }
}
