package com.example.mixbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mixbox.model.AllUserData;
import com.example.mixbox.model.Playlist;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
   FirebaseAuth auth;
   Button btnLogin;
   EditText emailEditText;
   EditText pwdEditText;
   TextView toSignupTxtView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      btnLogin = findViewById(R.id.btnLogin);
      emailEditText = findViewById(R.id.loginEmailEditText);
      pwdEditText = findViewById(R.id.loginPwdEditText);
      toSignupTxtView = findViewById(R.id.txtSignup);

      auth = FirebaseAuth.getInstance();

      toSignupTxtView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
         }
      });

      btnLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String email = emailEditText.getText().toString().trim();
            String pwd = pwdEditText.getText().toString().trim();

            auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                     Toast.makeText(LoginActivity.this, "Login Successful !!", Toast.LENGTH_SHORT).show();

                     Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                     startActivity(intent);
                  }
               }
            });
         }
      });

      //addUserFunction();
      //dbCounts();
      //addmoredata();
      ///////////////////////////


   }


   public void addUserFunction(){
      FirebaseDatabase db = FirebaseDatabase.getInstance();
      DatabaseReference reference = db.getReference();


      List<String> s = new ArrayList<>();
      s.add("song1");
      s.add("song2");

      Playlist playlist = new Playlist(1, "Playlist1", s);
      List<Playlist> allPlay = new ArrayList<>();
      allPlay.add(playlist);
      Song song1 = new Song("Song 3", 3, LocalDateTime.now(), null);
      Song song2 = new Song("Song 5", 1, LocalDateTime.now(), null);

      List<Song> songs = new ArrayList<>();
      songs.add(song1);
      songs.add(song2);

      User user = new User("email@gmail.com", "saini", "97899897", songs, s, allPlay);

      List<User> u = new ArrayList<>();
      u.add(user);

      //AllUserData all = new AllUserData(1, 1, 2, u);
      Log.e("---", "here");


//      reference.setValue(all).addOnCompleteListener(new OnCompleteListener<Void>() {
//         @Override
//         public void onComplete(@NonNull Task<Void> task) {
//            if(task.isSuccessful()){
//               Log.e("---", "succedd");
//            }
//            else{
//               Log.e("---", "failure");
//            }
//         }
//      });
      Log.e("---", "there");
   }

//   public void dbCounts(){
//      FirebaseDatabase db = FirebaseDatabase.getInstance();

//      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//         @Override
//         public void onComplete(@NonNull Task<DataSnapshot> task) {
//            if(task.isSuccessful()){
//               Log.e("---", "secc");
//               DataSnapshot result = task.getResult();
//
//               List<HashMap<String, Object>> allUsers = (List<HashMap<String, Object>>) result.child("allUsers").getValue();
//               for(HashMap<String, Object> userMap : allUsers){
//                  Log.e("--", userMap.get("email").toString());
//               }
//            }
//            else{
//               Log.e("---", "fail");
//            }
//         }
//      });

//      db.getReference().child("userCount").setValue(3).addOnCompleteListener(new OnCompleteListener<Void>() {
//         @Override
//         public void onComplete(@NonNull Task<Void> task) {
//            if(task.isSuccessful()){
//               Log.e("---", "Scc");
//            }
//            else{
//               Log.e("---", "fauk");
//            }
//         }
//      });
   //}

   public void addmoredata(){
      FirebaseDatabase db = FirebaseDatabase.getInstance();

      List<String> s = new ArrayList<>();
      s.add("song1");
      s.add("song2");

      Playlist playlist = new Playlist(1, "Playlist1", s);
      List<Playlist> allPlay = new ArrayList<>();
      allPlay.add(playlist);
      Song song1 = new Song("Song 3", 3, LocalDateTime.now(), null);
      Song song2 = new Song("Song 5", 1, LocalDateTime.now(), null);


      List<Song> songs = new ArrayList<>();
      songs.add(song1);
      songs.add(song2);

      User user = new User("email@gmail.com", "saini", "97899897", songs, s, allPlay);

      List<User> u = new ArrayList<>();
      u.add(user);

      AllUserData all = new AllUserData(u);
      Log.e("---", "here");

      //adding user
//      reference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//         @Override
//         public void onComplete(@NonNull Task<Void> task) {
//            if(task.isSuccessful()){
//               Log.e("---", "succedd");
//            }
//            else{
//               Log.e("---", "failure");
//            }
//         }
//      });
      db.getReference().child("allUsers").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
               Log.e("---", "succedd");
            }
            else{
               Log.e("---", "failure");
            }
         }
      });
      Log.e("---", "there");
   }
}









































