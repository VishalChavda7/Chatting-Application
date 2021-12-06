package com.example.vishalswhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vishalswhatsapp.databinding.SignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_in extends AppCompatActivity {
     TextView newac;
     SignInBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        ProgressDialog progressDialog = new ProgressDialog(Sign_in.this);
        progressDialog.setTitle("Log in Account");
        progressDialog.setMessage("We are Log in your account");

        if (auth.getCurrentUser()!=null)
        {
            Intent intent = new Intent(Sign_in.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.Email.getText().toString().isEmpty())
                {
                    binding.Email.setError(" Please Enter your Email");
                    return;
                }
                if (binding.Password.getText().toString().isEmpty()) {
                    binding.Password.setError(" Please Enter your Password");
                    return;
                }
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.Email.getText().toString(),binding.Password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful())
                                {
                                    Intent intent = new Intent(Sign_in.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(Sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        binding.newac.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          Intent intent = new Intent(Sign_in.this,Sign_up.class);
          startActivity(intent);
          }
      });


    }
}