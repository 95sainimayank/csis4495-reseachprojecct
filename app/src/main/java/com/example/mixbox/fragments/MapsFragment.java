package com.example.mixbox.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mixbox.R;
import com.example.mixbox.connection.RetroConnection;
import com.example.mixbox.databinding.FragmentMapsBinding;
import com.example.mixbox.model.MusicEvent;
import com.example.mixbox.model.OuterClass;
import com.example.mixbox.model.Venue;
import com.example.mixbox.model._MainEmbedded;
import com.example.mixbox.service.AppService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsFragment extends Fragment {
   FragmentMapsBinding binding;
   String API_KEY = "WoxMvvr1Q3d6mpyCG3n67BwAD55NNUcZ";
   HashMap<String, MusicEvent> allEvents;

   private OnMapReadyCallback callback = new OnMapReadyCallback() {

      /**
       * Manipulates the map once available.
       * This callback is triggered when the map is ready to be used.
       * This is where we can add markers or lines, add listeners or move the camera.
       * In this case, we just add a marker near Sydney, Australia.
       * If Google Play services is not installed on the device, the user will be prompted to
       * install it inside the SupportMapFragment. This method will only be triggered once the
       * user has installed Google Play services and returned to the app.
       */
      @Override
      public void onMapReady(GoogleMap googleMap) {
         allEvents = new HashMap<>();

         binding.btnMapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String city = binding.placeView.getText().toString().trim();
               googleMap.clear();
               if (city.length() < 2) {
                  Toast.makeText(getActivity(), "Please enter valid city name", Toast.LENGTH_SHORT).show();
               } else {
                  allEvents.clear();

                  RetroConnection connection = new RetroConnection();
                  Retrofit retrofit = connection.retrofit;
                  AppService appService = retrofit.create(AppService.class);

                  Toast.makeText(getActivity(), city, Toast.LENGTH_SHORT).show();

                  appService.getByCity(API_KEY, "*", city).enqueue(new Callback<OuterClass>() {
                     @Override
                     public void onResponse(Call<OuterClass> call, Response<OuterClass> response) {
                        if (response.isSuccessful()) {
                           Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                           if (response.body() != null) {
                              _MainEmbedded embedded = response.body().get_embedded();

                              if (embedded != null) {
                                 List<MusicEvent> events = embedded.getEvents();

                                 if (events != null || events.size() != 0) {
                                    for (MusicEvent ev : events) {
                                       for (Venue v : ev.get_embedded().getVenues()) {
                                          if (!allEvents.containsKey(v.getName())) {
                                             allEvents.put(v.getName(), ev);
                                          }
                                       }
                                    }
                                 }
                              } else {
                                 Log.e("embedded", "null");
                              }
                           }

                           Object[] objects = allEvents.keySet().toArray();
                           LatLng latlang = null;
                           for (Object obj : objects) {
                              MusicEvent e = allEvents.get(obj.toString());

                              List<Venue> venues = allEvents.get(obj.toString()).get_embedded().getVenues();

                              if(venues != null){
                                 for (Venue v : venues) {
                                    if(v.getLocation() != null){
                                       latlang = new LatLng(Double.parseDouble(v.getLocation().getLatitude())
                                         , Double.parseDouble(v.getLocation().getLongitude()));

                                       googleMap.addMarker(new MarkerOptions()
                                         .position(latlang).title(e.getUrl()));
                                    }

                                 }

                                 if(latlang != null)
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlang));
                              }
                           }

                        } else {
                           Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                        }
                     }

                     @Override
                     public void onFailure(Call<OuterClass> call, Throwable t) {
                        Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                     }
                  });
               }

            }
         });

//         LatLng sydney = new LatLng(-34, 151);
//         googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//         googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
      }
   };

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      binding = FragmentMapsBinding.inflate(inflater, container, false);

      return binding.getRoot();

   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
      if (mapFragment != null) {
         mapFragment.getMapAsync(callback);
      }
   }
}