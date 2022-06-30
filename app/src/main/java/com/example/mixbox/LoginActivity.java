package com.example.mixbox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mixbox.model.AllUserData;
import com.example.mixbox.model.Playlist;
import com.example.mixbox.model.Song;
import com.example.mixbox.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

            if (!isValidEmail(email)) {
               Toast.makeText(LoginActivity.this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            } else if (pwd.length() < 8) {
               Toast.makeText(LoginActivity.this, "Password should atleast be 8 characters!", Toast.LENGTH_SHORT).show();
            } else {
               auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful !!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                     } else {
                        Toast.makeText(LoginActivity.this, "Login failed! Please check your internet connection or your credentials.", Toast.LENGTH_SHORT).show();
                     }
                  }
               });
            }


         }
      });


   }


   public boolean isValidEmail(String email) {
      String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

      return email.matches(expression) && email.length() > 0;
   }


}









































