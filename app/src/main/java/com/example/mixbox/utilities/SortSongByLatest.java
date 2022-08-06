package com.example.mixbox.utilities;

import android.util.Log;

import com.example.mixbox.model.SongListModel;

import java.util.Comparator;

public class SortSongByLatest implements Comparator<SongListModel> {
   @Override
   public int compare(SongListModel obj1, SongListModel obj2) {
//      Log.e("", obj1.getSong().getSongName() +" obj1 - " + obj1.getSong().getDateTime().toString() +
    //     obj2.getSong().getSongName() + " obj2 - " + obj2.getSong().getDateTime().toString());
      if((obj1.getSong().getDateTime()).isAfter(obj2.getSong().getDateTime())){
         return -1;
      }
      else if((obj1.getSong().getDateTime()).isBefore(obj2.getSong().getDateTime())){
         return 1;
      }
      else{
         return 0;
      }
   }
}
