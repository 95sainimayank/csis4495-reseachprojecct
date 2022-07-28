package com.example.mixbox;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mixbox.fragments.EventFragment;
import com.example.mixbox.fragments.PlaylistFragment;
import com.example.mixbox.fragments.SearchSongFragment;
import com.example.mixbox.fragments.SongListFragment;
import com.example.mixbox.fragments.UploadSongFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
   private DrawerLayout drawer;
   private NavigationView navView;

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
               case R.id.nav_search:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SearchSongFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_events:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new EventFragment()).commit();
                  drawer.closeDrawer(Gravity.LEFT);
                  return true;
               case R.id.nav_upload:
                  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new UploadSongFragment()).commit();
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
      } else {
         super.onBackPressed();

      }
   }
}









































