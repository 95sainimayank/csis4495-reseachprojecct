package com.example.mixbox.model;

public class SongListModel {
   Song song;
   String artistName;

   public SongListModel() {

   }

   public SongListModel(Song song, String artistName) {
      this.song = song;
      this.artistName = artistName;
   }

   public Song getSong() {
      return song;
   }

   public void setSong(Song song) {
      this.song = song;
   }

   public String getArtistName() {
      return artistName;
   }

   public void setArtistName(String artistName) {
      this.artistName = artistName;
   }
}
