package com.example.mixbox.model;

import java.time.LocalDateTime;
import java.util.List;

public class Song {

   String songName;
   int timesPlayed;
   LocalDateTime dateTime;
   List<String> genres;


   public Song() {
   }

   public Song(String songName, int timesPlayed, LocalDateTime dateTime, List<String> genres) {
      this.songName = songName;
      this.timesPlayed = timesPlayed;
      this.dateTime = dateTime;
      this.genres = genres;
   }

   public LocalDateTime getDateTime() {
      return dateTime;
   }

   public void setDateTime(LocalDateTime dateTime) {
      this.dateTime = dateTime;
   }

   public List<String> getGenres() {
      return genres;
   }

   public void setGenres(List<String> genres) {
      this.genres = genres;
   }

   public String getSongName() {
      return songName;
   }

   public void setSongName(String songName) {
      this.songName = songName;
   }

   public int getTimesPlayed() {
      return timesPlayed;
   }

   public void setTimesPlayed(int timesPlayed) {
      this.timesPlayed = timesPlayed;
   }
}
