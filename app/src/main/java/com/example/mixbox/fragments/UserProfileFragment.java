package com.example.mixbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserProfileFragment extends Fragment {
   FragmentUserProfileBinding binding;
   FirebaseAuth auth;
   FirebaseDatabase db;
   String SHARED_PREF = "nightmode";
   SharedPreferences sharedPreferences;

   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentUserProfileBinding.inflate(inflater, container, false);

      auth = FirebaseAuth.getInstance();
      db = FirebaseDatabase.getInstance();

      addEventListeners();
      getAllUserData();

      sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
      int choice = sharedPreferences.getInt("choice", AppCompatDelegate.MODE_NIGHT_NO);

      if(choice == AppCompatDelegate.MODE_NIGHT_NO){
         binding.darkModeSwitch.setChecked(false);
      }
      else {
         binding.darkModeSwitch.setChecked(true);
      }


      return binding.getRoot();
   }

   private void addEventListeners() {

      binding.btnMySongs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("profile", "profile");
            SongListFragment fragment = new SongListFragment();
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
         }
      });

      binding.darkModeSwitch.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(binding.darkModeSwitch.isChecked()){
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            editor.putInt("choice", AppCompatDelegate.getDefaultNightMode());
            editor.apply();

         }
      });

   }

   private void getAllUserData() {
      String email = auth.getCurrentUser().getEmail();

      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  if (eachUser.get("email").equals(email)) {
                     binding.userProfileName.setText(eachUser.get("fullName").toString());

                     String userProfileOtherDetails = "Email: " + eachUser.get("email") + "\n\n" +
                       "Phone Number: " + eachUser.get("phoneNumber");

                     binding.userProfileOtherDetails.setText(userProfileOtherDetails);
                     break;
                  }
               }
            }
         }
      });

   }
}











































