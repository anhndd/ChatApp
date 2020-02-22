package com.example.group_32.chatloca.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;

public class EditPhoneDialog extends AppCompatDialogFragment {
    private Button btnConfirm, btnCancel;
    private TextView edtPhone;
    private EditPhoneDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = getArguments();
        //String strPhone = bundle.getString("phone");
        String strPhone = bundle.containsKey("phone") ? bundle.getString("phone") : "";

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editphone, null);
        mapping(view);
        builder.setView(view);

        edtPhone.setText(strPhone);
        edtPhone.requestFocus();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Changed phone number successfully", Toast.LENGTH_SHORT).show();

                listener.applyPhonenumbers(edtPhone.getText().toString());
                dismiss();
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
        btnConfirm = view.findViewById(R.id.button_ConfirmPhoneProfile);
        btnCancel = view.findViewById(R.id.button_CancelPhoneProfile);

        edtPhone = view.findViewById(R.id.EditText_NewPhoneProfile);
    }

    public interface EditPhoneDialogListener{
        void applyPhonenumbers(String strPhonenumber);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (EditPhoneDialogListener)context;
    }
}
