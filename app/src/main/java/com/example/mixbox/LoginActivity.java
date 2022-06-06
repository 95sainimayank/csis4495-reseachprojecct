package com.example.mixbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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


   }
}









































