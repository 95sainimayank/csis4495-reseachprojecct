package com.example.mixbox.model;

import java.util.List;

public class Playlist {
   int playlistId;
   String playlistName;
   List<String> songNames;

   public Playlist() {
   }

   public Playlist(int playlistId, String playlistName, List<String> songNames) {
      this.playlistId = playlistId;
      this.playlistName = playlistName;
      this.songNames = songNames;
   }

   public int getPlaylistId() {
      return playlistId;
   }

   public void setPlaylistId(int playlistId) {
      this.playlistId = playlistId;
   }

   public String getPlaylistName() {
      return playlistName;
   }

   public void setPlaylistName(String playlistName) {
      this.playlistName = playlistName;
   }

   public List<String> getSongNames() {
      return songNames;
   }

   public void setSongNames(List<String> songNames) {
      this.songNames = songNames;
   }
}
