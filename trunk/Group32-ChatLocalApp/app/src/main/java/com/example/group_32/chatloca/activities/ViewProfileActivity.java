package com.example.group_32.chatloca.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewProfileActivity extends AppCompatActivity {
    private final String AVATAR = "avatar";
    private final String USER = "User";
    private final String NAME_OF_USER = "nameOfUser";
    private final String DATE_OF_BIRTH = "dateofbirth";
    private final String GENDER = "gender";

    private ImageView imgAvatar;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvBirthday;
    private String userID;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mapping();
        setData();
    }

    private void mapping(){
        imgAvatar = findViewById(R.id.ImageView_Avatar_ViewProfile);
        tvName = findViewById(R.id.TextView_Name_ViewProfile);
        tvGender = findViewById(R.id.TextView_Gender_ViewProfile);
        tvBirthday = findViewById(R.id.TextView_BirthdayProfile);
        btnBack = findViewById(R.id.Button_Back_ViewProfile);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.containsKey("userID")? bundle.getString("userID"):"qUC8XTxjUAeKoMYt0b3HEd5IfUl2";
    }
    private void setData(){
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        FirebaseStorage.getInstance().getReference()
                .child(AVATAR + "/" + userID)
                .getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        imgAvatar.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        // ...
                    }
                });

        FirebaseDatabase.getInstance()
                .getReference(USER)
                .child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tvName.setText(dataSnapshot.child(NAME_OF_USER).getValue(String.class));
                        tvGender.setText(dataSnapshot.child(GENDER).getValue(String.class));
                        tvBirthday.setText(dataSnapshot.child(DATE_OF_BIRTH).getValue(String.class));
                        btnBack.setText(dataSnapshot.child(NAME_OF_USER).getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public void back(View view){
        this.onBackPressed();
    }
}
