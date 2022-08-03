package com.example.mixbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mixbox.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
   private View mWindow;
   private Context context;

   public CustomInfoWindowAdapter(Context ctx){
      context = ctx;
      mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
   }

   public void renderWindowText(Marker marker, View view){
      String title = marker.getTitle();
      TextView tvTitle = view.findViewById(R.id.mapTitle);

      if(!title.equals("")){
         tvTitle.setText(title);
      }

      String snippet = marker.getSnippet();
      TextView tvSnippet = view.findViewById(R.id.mapSnippet);

      if(!snippet.equals("")){
         tvSnippet.setText(snippet);
      }

   }

   @Nullable
   @Override
   public View getInfoContents(@NonNull Marker marker) {
      renderWindowText(marker, mWindow);
      return mWindow;
   }

   @Nullable
   @Override
   public View getInfoWindow(@NonNull Marker marker) {
      renderWindowText(marker, mWindow);
      return mWindow;
   }
}

























