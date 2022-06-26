package com.example.mixbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mixbox.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
   FirebaseAuth auth;
   Button btnSignup;
   FirebaseDatabase db;

   EditText emailEditTxt;
   EditText pwdEditText;
   TextView txtLogin;
   EditText confirmPwdEditText;
   EditText nameEditText;
   EditText phoneEditText;

   //df
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signup);

      btnSignup = findViewById(R.id.btnSignup);
      emailEditTxt = findViewById(R.id.emailEditText);
      pwdEditText = findViewById(R.id.pwdEditText);
      txtLogin = findViewById(R.id.txtLogin);
      confirmPwdEditText = findViewById(R.id.confirmPwdEditText);
      nameEditText = findViewById(R.id.nameEditText);
      phoneEditText = findViewById(R.id.phoneEditText);

      auth = FirebaseAuth.getInstance();
      db = FirebaseDatabase.getInstance();

      txtLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
         }
      });

      btnSignup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String email = emailEditTxt.getText().toString().trim();
            String pwd = pwdEditText.getText().toString().trim();
            String cpwd = confirmPwdEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(email) || TextUtils.isEmpty(cpwd) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
               Toast.makeText(SignupActivity.this, "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
            } else if (pwd.length() < 8) {
               Toast.makeText(SignupActivity.this, "Password should contain 8 characters at least!!", Toast.LENGTH_SHORT).show();
            } else if (!pwd.equals(cpwd)) {
               Toast.makeText(SignupActivity.this, "Password should be same as confirm password !!", Toast.LENGTH_SHORT).show();
            } else if (validateEmail(email)) {
               auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                  @Override
                  public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                     boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                     if (isNewUser) {
                        auth.createUserWithEmailAndPassword(emailEditTxt.getText().toString().trim(), pwdEditText.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                              if (task.isSuccessful()) {
                                 Toast.makeText(SignupActivity.this, "Signup Successful !!", Toast.LENGTH_SHORT).show();

                                 Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                 startActivity(intent);
                              } else {
                                 Toast.makeText(SignupActivity.this, "Signup was not successful !! Please check internet connection or try again in sometime", Toast.LENGTH_SHORT).show();
                              }
                           }
                        });

                        User user = new User(email, name, phone, null, null, null);
                        addUserToDb(user);

                     } else {
                        Log.e("TAG", "Is Old User!");
                     }
                  }
               });
            } else {
               Toast.makeText(SignupActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            }
         }
      });
   }

   public boolean validateEmail(String email) {
      String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

      return email.matches(expression) && email.length() > 0;
   }

   public void addUserToDb(User user){
      db.getReference().child("allUsers").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
               Log.e("---", "User Added successfully");
            }
            else{
               Log.e("---", "Failed to add user");
            }
         }
      });
   }
}


//
//            else if(!emailExists(email)){ //create user in auth
//
//
//               //adding user to realtime db
//
//
//            }


//db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//@Override
//public void onComplete(@NonNull Task<DataSnapshot> task) {
//  if(task.isSuccessful()) {
//  DataSnapshot snapshot = task.getResult();
//  HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
//  HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");
//
//  for (Object value : allUsers.values()) {
//  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;
//
//  if(eachUser.get("email").equals(email)){
//  exists[0] = true;
//  }
//  }
//  }
//  else{
//  Log.e("---", task.getException().toString());
//  }
//  }
//  });



