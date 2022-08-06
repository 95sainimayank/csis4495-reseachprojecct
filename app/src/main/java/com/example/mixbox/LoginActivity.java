package com.example.mixbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
   Switch saveLoginInfo;

   public static final String SHARED_PREFS = "sharedPrefs";
   public static final String USERNAME = "user";
   public static final String PASSWORD = "pass";
   public static final String SWITCH = "switch1";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      btnLogin = findViewById(R.id.btnLogin);
      emailEditText = findViewById(R.id.loginEmailEditText);
      pwdEditText = findViewById(R.id.loginPwdEditText);
      toSignupTxtView = findViewById(R.id.txtSignup);
      saveLoginInfo = findViewById(R.id.saveSwitch);

      loadLoginData();
      
      auth = FirebaseAuth.getInstance();

      toSignupTxtView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
         }
      });

      enableNightModeIfApplicable();

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

                        if(saveLoginInfo.isChecked()){
                           saveData(email, pwd);
                        }
                        else{
                           clearLoginData();
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                     } else {
                        Toast.makeText(LoginActivity.this, "Login failed! Please check your internet connection or your credentials.", Toast.LENGTH_SHORT).show();
                     }
                  }
               });
            }


         }
      });


   }

   private void enableNightModeIfApplicable() {
      SharedPreferences sharedPreferences = this.getSharedPreferences("nightmode", MODE_PRIVATE);
      int choice = sharedPreferences.getInt("choice", AppCompatDelegate.MODE_NIGHT_NO);

      if(choice == AppCompatDelegate.MODE_NIGHT_NO){
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }
      else {
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      }
   }

   private void clearLoginData() {
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.clear();
      editor.apply();
   }

   private void loadLoginData() {
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

      Log.e("Inside load login data", sharedPreferences.getString(USERNAME, "") + "-" + sharedPreferences.getString(PASSWORD, "") + sharedPreferences.getBoolean(SHARED_PREFS, false));

      emailEditText.setText(sharedPreferences.getString(USERNAME, ""));
      pwdEditText.setText(sharedPreferences.getString(PASSWORD, ""));
      saveLoginInfo.setChecked(sharedPreferences.getBoolean(SWITCH, false));
   }

   private void saveData(String email, String pass) {
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();

      editor.putString(USERNAME, email);
      editor.putString(PASSWORD, pass);
      editor.putBoolean(SWITCH, saveLoginInfo.isChecked());

      Log.e("Inside save login data", sharedPreferences.getString(USERNAME, "") + "-" + sharedPreferences.getString(PASSWORD, "") + sharedPreferences.getBoolean(SHARED_PREFS, false));

      editor.apply();
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









































