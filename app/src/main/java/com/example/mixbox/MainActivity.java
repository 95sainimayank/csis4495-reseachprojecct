package com.example.mixbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mixbox.fragments.HomeFragment;
import com.example.mixbox.fragments.MapsFragment;
import com.example.mixbox.fragments.PlaylistFragment;
import com.example.mixbox.fragments.SearchSongFragment;
import com.example.mixbox.fragments.SongListFragment;
import com.example.mixbox.fragments.UploadFragment;
import com.example.mixbox.fragments.UserProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
   private DrawerLayout drawer;
   private NavigationView navView;
   private boolean isBackPressedOnce = false;

   public static final String SHARED_PREFS = "sharedPrefs";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      drawer = findViewById(R.id.drawer_layout);
      navView = findViewById(R.id.navigationView);

      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      drawer.addDrawerListener(toggle);
      toggle.syncState();

      navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
         @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            switch (id) {
               case R.id.home:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_search:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SearchSongFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_events:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new MapsFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_upload:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new UploadFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_favorite:
                  Bundle bundle = new Bundle();
                  bundle.putString("type", "favorite");
                  SongListFragment fragment = new SongListFragment();
                  fragment.setArguments(bundle);
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_playlist:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PlaylistFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_logout:
                  clearLoginData();
                  Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                  startActivity(intent);
                  finish();
                  return  true;
               default:
                  return false;
            }

         }
      });


   }

   @Override
   public void onBackPressed() {
      if (drawer.isDrawerOpen(GravityCompat.START)) {
         drawer.closeDrawer(GravityCompat.START);
      }
      else if(!isBackPressedOnce){
         Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
         isBackPressedOnce = true;
      }
      else{
         super.onBackPressed();
         return;
      }

   }
   private void clearLoginData() {
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.clear();
      editor.apply();
   }


   public void navHeaderClick(View view) {
      getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new UserProfileFragment()).commit();
      drawer.closeDrawer(Gravity.LEFT);
   }
}









































