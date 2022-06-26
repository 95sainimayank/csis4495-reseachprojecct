package com.example.mixbox.utilities;

import com.example.mixbox.model.SongListModel;

import java.util.Comparator;

public class SortSongByLatest implements Comparator<SongListModel> {
   @Override
   public int compare(SongListModel obj1, SongListModel obj2) {
      if(obj1.getSong().getDateTime().isAfter(obj2.getSong().getDateTime())){
         return -1;
      }
      else{
         return 1;
      }
   }
}
