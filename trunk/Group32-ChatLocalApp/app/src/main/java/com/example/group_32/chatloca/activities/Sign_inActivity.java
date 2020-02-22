package com.example.group_32.chatloca.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sign_inActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "Sign_inActivity";
    private static final int RC_SIGN_IN = 9001;
    private TextView textvSignUp;
    private EditText edUserName;
    private EditText edPassWord;

    // gg button
    private Button btnGg;

    private FirebaseAuth mAuth;
    // gg login
    private GoogleSignInClient mGoogleSignInClient;
    // fb login
    private CallbackManager mCallbackManager;

    //db
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        textvSignUp = findViewById(R.id.TextView_SignUp);
        edUserName = findViewById(R.id.editText_SignInUsername);
        edPassWord = findViewById(R.id.editText_SignInPassword);

        findViewById(R.id.button_mail_signIn).setOnClickListener(this);
        findViewById(R.id.button_google_signIn).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



    }
    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        textvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_inActivity.this, Sign_upActivity.class);
                startActivity(intent);
            }
        });
    }
    public void updateUI(FirebaseUser user){
        hideProgressDialog();
        if (user != null) {
            finish(); // Anh modified
            Intent intent = new Intent(Sign_inActivity.this, MessageActivity.class);
            startActivity(intent);
        }
    }

    public boolean isEmpty(EditText editText){
        return editText.getText().toString().equals("");
    }
    public boolean validateForm(){
        boolean valid = true;
        if (isEmpty(edUserName)) {
            edUserName.setError("Invalid username");
            edUserName.requestFocus();
            valid =false;
        }else
            edUserName.setError(null);

        if (isEmpty(edPassWord)) {
            edPassWord.setError("Invalid password");
            edPassWord.requestFocus();
            valid =false;
        } else
            edPassWord.setError(null);
        return valid;
    }
    public void singInEmail(final String email, String password){
        Log.d(TAG, "signIn: "+email);
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Sign_inActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Sign_inActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                hideProgressDialog();
            }
        });

    }
    public void singInUsername(String username, final String password){

        DatabaseReference mapUser =  db.getReference("MapUsername:Mail");

        Log.d(TAG, "signInWithUsername: "+username);
        showProgressDialog();

        // If sign in email fail, continue with as username.
        mapUser.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
//                    MapUsername newMap =  dataSnapshot.getValue(MapUsername.class);
                    String email = dataSnapshot.getValue(String.class);
                    if(email != null ) {
                        Log.d(TAG, "RETURN EMAIL: " + email);
                        singInEmail(email, password);
                        //String userId = newMap.getUserId();
                    }
                    else{
                        Log.w(TAG, "signInWithUser:failure");
                        Toast.makeText(Sign_inActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
                else{
                    Log.w(TAG, "signInWithUser:failure");
                    Toast.makeText(Sign_inActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                    updateUI(null);

                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Sign_inActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    public void signInGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_mail_signIn) {
            if(validateForm()){
                final String emailOrUsername = edUserName.getText().toString();
                final String pass = edPassWord.getText().toString();
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches())
                    singInEmail(emailOrUsername,pass);
                else
                    singInUsername(emailOrUsername, pass);
            }
        } else if (i == R.id.button_google_signIn) {
            signInGoogle();
        }
    }
}

