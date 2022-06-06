package com.example.mixbox.fragments;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private FragmentLoginBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(binding.getRoot());
                navController.navigate(R.id.fromLoginToSignup);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitForm();

            }
        });





        return binding.getRoot();
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void submitForm(){
        String email = binding.inputEmailLogin.getText().toString().trim();
        String password = binding.inputPwdLogin.getText().toString().trim();




        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginFragment.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){

                    NavController navController = Navigation.findNavController(binding.getRoot());
                    navController.navigate(R.id.action_loginFragment_to_homeFragment);
                }
                else{
                   Toast.makeText(LoginFragment.this,getString(R.string.auth_failed),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
