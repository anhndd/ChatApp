package com.example.group_32.chatloca.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_upActivity extends BaseActivity {
    private static final String TAG = "Sign_upActivity";
    private TextView textvSignIn;
    private Button btnSignUp;
    private EditText etUserName;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassWord;
    private EditText etConfirmPass;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private RadioGroup rdgrSex;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private TextView mDisplayDate;
    private ImageView mIcPicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth mAuth;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etFirstName = findViewById(R.id.EditText_SignUpFirstName);
        etLastName = findViewById(R.id.EditText_SignUpLastName);
        etUserName = findViewById(R.id.EditText_SignUpUserName);
        etPassWord = findViewById(R.id.EditText_SignUpPassword);
        rdMale = findViewById(R.id.radioMale_SignUp);
        etEmail = findViewById(R.id.EditText_SignUpEmail);
        etPhone = findViewById(R.id.EditText_SignUpPhone);
        etAddress = findViewById(R.id.EditText_SignUpAddress);
        etConfirmPass = findViewById(R.id.EditText_SignUpConfirmPassword);
        textvSignIn = findViewById(R.id.TextView_SignIn);
        mDisplayDate =findViewById(R.id.DatePicker_SignUp);
        btnSignUp = findViewById(R.id.button_SignUp);
        mIcPicker = findViewById(R.id.ic_day_of_birth);

        // db
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected  void onStart(){
        super.onStart();

        textvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Anh modified
                Intent intent = new Intent(Sign_upActivity.this,Sign_inActivity.class);
                startActivity(intent);
            }
        });

        onDatePickerClicked();
        onRadioButtonClicked();
        onButtonSignInClicked();
    }
    public  void onDatePickerClicked(){
        mIcPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(Sign_upActivity.this,
                        android.R.style.Theme_Holo_Light,
                        mDateSetListener,
                        2000, 0,1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date = day + "/"+ month+"/"+year;
                mDisplayDate.setText(date);
            }
        };
    }
    public void onRadioButtonClicked(){
        rdgrSex = findViewById(R.id.radioGroupSex_SignUp);
        rdMale = findViewById(R.id.radioMale_SignUp);
        rdFemale = findViewById(R.id.radioFemale_SignUp);
        rdgrSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rdgrSex.getCheckedRadioButtonId()==  rdMale.getId()) {
                    rdFemale.setSelected(false);
                    rdMale.setSelected(true);
                }
                else{
                    rdMale.setSelected(false);
                    rdFemale.setSelected(true);
                }
            }
        });
    }
    public void onButtonSignInClicked(){

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // If sign in email fail, continue with as username.
                if(validateForm()) {
                    String username = etUserName.getText().toString();
                    DatabaseReference mapUser =  db.getReference("MapUsername:Mail");
                    Log.d(TAG, "ChECK USERNAME EXIST: "+username);
                    showProgressDialog();
                    mapUser.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.w(TAG, "CHECK USERNAME: exist");
                                etUserName.setError("Username was existed");
                                etUserName.requestFocus();

                            } else {
                                Log.w(TAG, "CHECK USERNAME: not exist");
                                final String password = etPassWord.getText().toString();
                                final String email = etEmail.getText().toString();
                                createAccount(email, password);
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }
    public boolean isEmpty(EditText editText){
        return editText.getText().toString().equals("");
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validateForm(){
        boolean valid =true;
        if (isEmpty(etFirstName)) {
            etFirstName.setError("First Name can't be empty");
            etFirstName.requestFocus();
            valid = false;
        }else {
            etFirstName.setError(null);
        }

        if (isEmpty(etLastName)) {
            etLastName.setError("Last Name can't be empty");
            etLastName.requestFocus();
            valid = false;
        }else {
            etLastName.setError(null);
        }

        if (isEmpty(etUserName)) {
            etUserName.setError("Username can't be empty");
            etUserName.requestFocus();
            valid = false;
        }else {
            etUserName.setError(null);
        }
        if(etPassWord.getText().toString().length() < 6){
            etPassWord.setError("Your password must have at least 6 letters, numbers and symbols");
            etPassWord.requestFocus();
            valid = false;
        }else {
            etPassWord.setError(null);
        }
        if (isEmpty(etPassWord)) {
            etPassWord.setError("PassWord can't be empty");
            etPassWord.requestFocus();
            valid = false;
        }else {
            if(!etPassWord.getText().toString().equals(etConfirmPass.getText().toString())){
                etConfirmPass.setError("Confirm password not match");
                etConfirmPass.requestFocus();
                valid = false;
            }else {
                etPassWord.setError(null);
            }
        }
        if(mDisplayDate.getText().toString().equals("")){
            mDisplayDate.setError("Select Date of birth");
            mDisplayDate.requestFocus();
            valid = false;
        }else {
            Calendar cal = Calendar.getInstance();
            String date = mDisplayDate.getText().toString();
            String[] temp = date.split("/");
            if(Integer.parseInt(temp[2]) > cal.get(Calendar.YEAR) -5){
                mDisplayDate.setError("You too young to register. At least 5 year old");
                mDisplayDate.requestFocus();
            }
            else
                mDisplayDate.setError(null);
        }


        if(!rdMale.isSelected()&& !rdFemale.isSelected()){
            rdMale.setError("Please select a gender");
            rdFemale.requestFocus();
            valid = false;
        }else {
            rdMale.setError(null);
        }
        if (isEmpty(etEmail)) {
            etEmail.setError("Email can't be empty");
            etEmail.requestFocus();
            valid = false;
        }else {
            if (!isEmailValid(etEmail.getText().toString())){
                etEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
                valid = false;
            }else {
                etEmail.setError(null);
            }
        }

        return valid;
    }

    public void createAccount(String email, String password){
        Log.d(TAG, "createAccount"+ email);
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(Sign_upActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if  (task.isSuccessful()){
                    Log.d(TAG, "createWithEmail: Success");
                    addAccountRealTimeDB();
                    hideProgressDialog();
                    Toast.makeText(Sign_upActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();

                    finish(); // Anh modified
                    Intent intent = new Intent(Sign_upActivity.this, Sign_inActivity.class);
                    startActivity(intent);
                }
                else{
                    Log.w(TAG, "createWithEmail: Failure", task.getException());
                    Toast.makeText(Sign_upActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
                hideProgressDialog();
            }
        });

    }
    public void addAccountRealTimeDB(){
        final String firstname = etFirstName.getText().toString();
        final String lastname = etLastName.getText().toString();
        final String username = etUserName.getText().toString();
//        final String password = etPassWord.getText().toString();
        final String gender = rdMale.isChecked() ? "Male" : "Female";
        final String email = etEmail.getText().toString();
        final String phone = etPhone.getText().toString();
        final String birthday = mDisplayDate.getText().toString();
        final String address = etAddress.getText().toString();
        User user = new User(username, firstname, lastname, gender, email, phone, address, birthday);

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(etUserName.getText().toString()).build();
        mAuth.getCurrentUser().updateProfile(profileUpdate);

        String newUserId = saveInfoUser(user);
        saveUsernameEmail(username, email,  newUserId);
    }

    public String saveInfoUser(User new_user) {
        DatabaseReference newUserID = db.getReference("User").child(mAuth.getCurrentUser().getUid());;
        db.getReference("userKey").child(new_user.getUserName()).setValue(mAuth.getCurrentUser().getUid()); // Anh modify

        newUserID.setValue(new_user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!= null)
                    Log.d(TAG, "Write INFOUSER: ERROR "+databaseError.getMessage());
                else
                    Log.d(TAG, "Write INFOUSER: Successs");
            }
        });
        return newUserID.getKey();
    }
    public void saveUsernameEmail(String username,  String email,String newUserId){
        Log.d(TAG, "Write MapUSEr: Begin");
        DatabaseReference mapUser = db.getReference("MapUsername:Mail");
        mapUser.child(username).setValue(email, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!= null)
                    Log.d(TAG, "Write MapUSer: ERROR "+databaseError.getMessage());
                else
                    Log.d(TAG, "Write MapUSer: Successs");
            }
        });
    }
}
