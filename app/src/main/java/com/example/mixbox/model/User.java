package com.example.mixbox.model;

import java.util.List;

public class User {
   String email;
   String fullName;
   String phoneNumber;
   List<Song> songs;
   List<String> favoriteSongs;
   List<Playlist> allPlaylists;

   public User() {
   }

   public User(String email, String fullName, String phoneNumber, List<Song> songs, List<String> favoriteSongs, List<Playlist> allPlaylists) {
      this.email = email;
      this.fullName = fullName;
      this.phoneNumber = phoneNumber;
      this.songs = songs;
      this.favoriteSongs = favoriteSongs;
      this.allPlaylists = allPlaylists;
   }

   public List<Playlist> getAllPlaylists() {
      return allPlaylists;
   }

   public void setAllPlaylists(List<Playlist> allPlaylists) {
      this.allPlaylists = allPlaylists;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public List<Song> getSongs() {
      return songs;
   }

   public void setSongs(List<Song> songs) {
      this.songs = songs;
   }

   public List<String> getFavoriteSongs() {
      return favoriteSongs;
   }

   public void setFavoriteSongs(List<String> favoriteSongs) {
      this.favoriteSongs = favoriteSongs;
   }
}
