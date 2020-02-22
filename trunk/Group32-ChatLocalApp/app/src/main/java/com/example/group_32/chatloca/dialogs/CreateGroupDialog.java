package com.example.group_32.chatloca.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.Database;
import com.example.group_32.chatloca.R;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class CreateGroupDialog extends AppCompatDialogFragment {
    private Button btnConfirm, btnCancel;
    private EditText edtNameOfGroup;
    private TextView tvMembers;
    @SuppressLint("StaticFieldLeak")
    static public View viewCreateGroup;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewCreateGroup = inflater.inflate(R.layout.dialog_create_group, null);
        mapping(viewCreateGroup);

        builder.setView(viewCreateGroup);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strNameOfGroup = edtNameOfGroup.getText().toString();
                String strMemebers = tvMembers.getText().toString();
                String[] userName_Member = strMemebers.split(" {3}");
                if (strNameOfGroup.isEmpty() || strMemebers.isEmpty()) {
                    Toast.makeText(getContext(), "The fields must not be blank", Toast.LENGTH_SHORT).show();
                } else if (strMemebers.length() > 0) {
                    Database db = new Database();
                    db.CreateGroup(userName_Member, strNameOfGroup);
                    //----------------------------------------------------------------------------------------
                    // Do add view group here
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                    //----------------------------------------------------------------------------------------
                    Toast.makeText(getContext(), "Create group successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Not Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchMemberDialog dialog = new SearchMemberDialog();
                dialog.show(getChildFragmentManager(), null);
            }
        });

        return builder.create();
    }

    private void mapping(View view) {
        btnConfirm = view.findViewById(R.id.button_ConfirmCreateGroup);
        btnCancel = view.findViewById(R.id.button_CancelCreateGroup);
        edtNameOfGroup = view.findViewById(R.id.EditText_NameOfGroup);
        tvMembers = view.findViewById(R.id.TextView_Members);
    }
}
