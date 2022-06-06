package com.example.mixbox.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentLoginBinding;
import com.example.mixbox.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    private FirebaseAuth auth;
    private FragmentSignupBinding binding;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentSignupBinding.inflate(inflater, container, false);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(binding.getRoot());
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        return binding.getRoot();
    }

    private void submitForm(){
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPwd.getText().toString().trim();
        String name = binding.inputName.getText().toString().trim();
        String nickName = binding.inputNick.getText().toString().trim();
        String phoneNumber = binding.inputPhone.getText().toString().trim();




        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpFragment.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Log.v(TAG, "Email verified? " + auth.getCurrentUser().isEmailVerified());

                            finish();
                        }
                        else{
                            Log.d(TAG,"Authentication failed" + task.getException());
                        }

                    }
                });

        Toast.makeText(getApplicationContext(), "You are successfully registered!!", Toast.LENGTH_SHORT).show();

    }

}
