package com.example.mixbox.utilities;

import com.example.mixbox.model.SongListModel;

import java.util.Comparator;

public class SortSongByPlayCount implements Comparator<SongListModel> {
   @Override
   public int compare(SongListModel obj1, SongListModel obj2) {
      if(obj1.getSong().getTimesPlayed() > obj2.getSong().getTimesPlayed()){
         return -1;
      }
      else{
         return 1;
      }
   }
}
