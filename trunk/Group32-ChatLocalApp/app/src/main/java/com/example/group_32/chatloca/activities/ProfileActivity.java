package com.example.group_32.chatloca.activities;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.dialogs.ChangeAvatarDialog;
import com.example.group_32.chatloca.dialogs.EditPasswordDialog;
import com.example.group_32.chatloca.dialogs.EditPhoneDialog;

import com.example.group_32.chatloca.dialogs.FriendRequestDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.example.group_32.chatloca.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements ChangeAvatarDialog.ChangeAvatarDialogListener, EditPhoneDialog.EditPhoneDialogListener, EditPasswordDialog.EditPasswordDialogListener{
    private final String USER = "User";
    private final String NAME_OF_USER = "nameOfUser";
    private final String FIRST_NAME = "firstName";
    private final String LAST_NAME = "lastName";
    private final String DATE_OF_BIRTH = "dateofbirth";
    private final String EMAIL = "email";
    private final String PHONE = "phone";
    private final String GENDER = "gender";
    private final String PASSWORD = "password";
    private final String ADDRESS = "address";

    private final String AVATAR = "avatar";

    private Button btnCallActivityBack, btnLogOut;
    private TextView txtName, txtGender, txtBirthday, txtEmail, txtPhone, txtAddress, txtChangePassword, txtEditPhone;

    private ImageView imgAvatar, imgFriendRequest;

    private FirebaseAuth mAuth;
    public static boolean hasChange; // Anh modified
    // TN
    private String password;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mapping();

        hasChange = false;
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        setData();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnCallActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasChange)
                    finish();
                else {
                    Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                finish(); // Anh modified
                Intent intent = new Intent(ProfileActivity.this,Sign_inActivity.class);
                startActivity(intent);
            }
        });
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditPasswordDialog dialog = new EditPasswordDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        txtEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditPhoneDialog dialog = new EditPhoneDialog();

                Bundle bundle = new Bundle();
                bundle.putString(PHONE, txtPhone.getText().toString());
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeAvatarDialog dialog = new ChangeAvatarDialog();

                dialog.show(getSupportFragmentManager(), null);
            }
        });
        imgFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendRequestDialog dialog = new FriendRequestDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override // Anh modified
    public void onBackPressed() {
        super.onBackPressed();
        if(!hasChange)
            finish();
        else {
            Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
            startActivity(intent);
        }
    }

    private void signOut(){
        mAuth.signOut();
        // Google sign out
        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //
                        }
                    });
        }
    }

    private void setData() {
        if(checkImageExist(mAuth.getUid())){
            Bitmap bitmap = loadImageFromStorage(mAuth.getUid());
            imgAvatar.setImageBitmap(bitmap);
        }
        else{
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            FirebaseStorage.getInstance().getReference()
                    .child(AVATAR + "/" + mAuth.getCurrentUser().getUid())
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            imgAvatar.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                            saveToInternalStorage(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), mAuth.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                        }
                    });
        }


        FirebaseDatabase.getInstance()
                .getReference(USER)
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String displayName = dataSnapshot.child(FIRST_NAME).getValue(String.class) + " " + dataSnapshot.child(LAST_NAME).getValue(String.class);
                        txtName.setText(displayName);
                        txtGender.setText(dataSnapshot.child(GENDER).getValue(String.class));
                        txtBirthday.setText(dataSnapshot.child(DATE_OF_BIRTH).getValue(String.class));

                        txtEmail.setText(dataSnapshot.child(EMAIL).getValue(String.class));
                        txtPhone.setText(dataSnapshot.child(PHONE).getValue(String.class));
                        txtAddress.setText(dataSnapshot.child(ADDRESS).getValue(String.class));
                        password = dataSnapshot.child(PASSWORD).getValue(String.class);
                        btnCallActivityBack.setText(dataSnapshot.child(NAME_OF_USER).getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void applyBitmapAvatar(Intent intent) {
        Bitmap bitmap = intent.getParcelableExtra("data");

//        Uri uri = intent.getParcelableExtra("uri");

        imgAvatar.setImageBitmap(bitmap);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos); //Anh modify quality 100 downto 25
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef
                .child(AVATAR + "/" + mAuth.getCurrentUser().getUid())
                .putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
        saveToInternalStorage(bitmap, mAuth.getUid());
        hasChange = true;
    }

    @Override
    public void applyPhonenumbers(String strPhonenumber) {
        txtPhone.setText(strPhonenumber);
        FirebaseDatabase.getInstance()
                .getReference(USER)
                .child(mAuth.getCurrentUser().getUid())
                .child(PHONE)
                .setValue(strPhonenumber);
    }

    public String getPhonenumber(){
        TextView textView = findViewById(R.id.TextView_PhoneProfile);
        return textView.getText().toString();
    }

    public void mapping(){
        btnCallActivityBack = findViewById(R.id.button_BackProfile);
        btnLogOut = findViewById(R.id.button_Logout);
        txtName = findViewById(R.id.TextView_NameProfile);
        txtGender = findViewById(R.id.TextView_GenderProfile);
        txtBirthday = findViewById(R.id.TextView_BirthdayProfile);
        txtEmail = findViewById(R.id.TextView_EmailProfile);
        txtPhone = findViewById(R.id.TextView_PhoneProfile);
        txtAddress = findViewById(R.id.TextView_AddressProfile);
        imgAvatar = findViewById(R.id.image_avatar);
        txtChangePassword = findViewById(R.id.TextView_ChangePasswordProfile);
        txtEditPhone = findViewById(R.id.TextView_EditPhoneProfile);
        imgFriendRequest = findViewById(R.id.ImageView_FriendRequestProfile);
    }

    public String getPassCurrent(){
        return password;
    }

    @Override
    public void applyPassword(boolean isSuccessful) {
        if(isSuccessful){
            Toast.makeText(this, "Change password successful!!!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Change password fail!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToInternalStorage(Bitmap bitmap, String userId){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Bitmap loadImageFromStorage(String userId) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");
        try {
            return BitmapFactory.decodeStream(new FileInputStream(mypath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean checkImageExist(String userId){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");
        return mypath.exists();
    }
}
