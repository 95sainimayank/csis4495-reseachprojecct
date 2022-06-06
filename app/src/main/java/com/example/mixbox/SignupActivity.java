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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
   FirebaseAuth auth;
   Button btnSignup;
   EditText emailEditTxt;
   EditText pwdEditText;
   TextView txtLogin;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signup);

      btnSignup = findViewById(R.id.btnSignup);
      emailEditTxt = findViewById(R.id.emailEditText);
      pwdEditText = findViewById(R.id.pwdEditText);
      txtLogin = findViewById(R.id.txtLogin);

      auth = FirebaseAuth.getInstance();

      txtLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
         }
      });

      btnSignup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            auth.createUserWithEmailAndPassword(emailEditTxt.getText().toString(), pwdEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                     Toast.makeText(SignupActivity.this, "Signup Successful !!", Toast.LENGTH_SHORT).show();

                     Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                     startActivity(intent);
                  }
                  else{
                     Toast.makeText(SignupActivity.this, "Signup was not successful !!", Toast.LENGTH_SHORT).show();
                  }
               }
            });
         }
      });


   }
}