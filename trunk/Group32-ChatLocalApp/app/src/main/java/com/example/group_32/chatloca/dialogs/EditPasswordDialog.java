package com.example.group_32.chatloca.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordDialog extends AppCompatDialogFragment {
    private Button btnConfirm, btnCancel;
    private TextView edtCurrentPass, edtNewPass, edtConfirmPass;
    private EditPasswordDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editpassword, null);
        mapping(view);

        builder.setView(view);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            private boolean isSuccessful = false;
            @Override
            public void onClick(View view) {
                String strCurrentPass = edtCurrentPass.getText().toString();
                final String strNewPass = edtNewPass.getText().toString();
                String strConfirmPass = edtConfirmPass.getText().toString();

                if(strCurrentPass.isEmpty() || strNewPass.isEmpty() || strConfirmPass.isEmpty()){
                    Toast.makeText(getContext(), "The fields must not be blank", Toast.LENGTH_SHORT).show();
                }
                else if(strNewPass.length() < 6 || strConfirmPass.length() < 6 || strCurrentPass.length() < 6){
                    Toast.makeText(getContext(), "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(strNewPass.equals(strConfirmPass)){
                        // Change password in Authentication
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), strCurrentPass);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseAuth.getInstance()
                                                    .getCurrentUser()
                                                    .updatePassword(strNewPass)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                isSuccessful = true;

                                                            }
                                                            listener.applyPassword(isSuccessful);
                                                        }
                                                    });

                                            dismiss();
                                        }
                                        else{
                                            Toast.makeText(getContext(), "Something is wrong, please try again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    }
                    else{
                        Toast.makeText(getContext(), "Something is wrong, please check and login again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void mapping(View view){
        btnConfirm = view.findViewById(R.id.button_ConfirmPasswordProfile);
        btnCancel = view.findViewById(R.id.button_CancelPasswordProfile);

        edtCurrentPass = view.findViewById(R.id.EditText_CurrentPasswordProfile);
        edtNewPass = view.findViewById(R.id.EditText_NewPasswordProfile);
        edtConfirmPass = view.findViewById(R.id.EditText_ConfirmPasswordProfile);
    }

    public interface EditPasswordDialogListener{
        void applyPassword(boolean isSuccessful);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (EditPasswordDialog.EditPasswordDialogListener)context;
    }
}
