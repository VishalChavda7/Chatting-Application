package com.example.vishalswhatsapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vishalswhatsapp.Model.User;
import com.example.vishalswhatsapp.databinding.ActivitySettingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
ActivitySettingBinding binding;
ActivityResultLauncher<String> galleryLuncher;
ProgressDialog progressDialog;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();



        binding.arroBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,MainActivity.class));
                finishAffinity();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = binding.Username.getText().toString();
                String about = binding.etAbout.getText().toString();

                HashMap<String,Object> obj = new HashMap<>();
                obj.put("userName",username);
                obj.put("about",about);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(SettingActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilepic())
                                .placeholder(R.drawable.profile)
                                .into(binding.profilepictuer);

                        binding.Username.setText(user.getUserName());
                        binding.etAbout.setText(user.getAbout());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        galleryLuncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                      binding.profilepictuer.setImageURI(uri);

                        final StorageReference reference = storage.getReference().child("profilePic")
                                .child(FirebaseAuth.getInstance().getUid());
                        ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
                        progressDialog.setMessage("wait");
                        progressDialog.show();
                        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .child("profilepic").setValue(uri.toString());
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingActivity.this, "Profile Updeted", Toast.LENGTH_SHORT).show();
                                                    
                                        }
                                    });
                            }
                        });
                    }
                });
        binding.changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLuncher.launch("image/*");

            }
        });


    }
}