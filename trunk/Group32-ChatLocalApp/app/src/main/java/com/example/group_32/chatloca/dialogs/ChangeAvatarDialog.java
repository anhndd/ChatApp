package com.example.group_32.chatloca.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;

public class ChangeAvatarDialog extends AppCompatDialogFragment {
    private Button btnConfirm, btnCancel, btnChoose;
    private static final int PICK_IMAGE = 100;
    private ImageView imgAvatar;
    private ChangeAvatarDialogListener listener;

    Uri imgUri;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_changeavatar, null);
        mapping(view);

        builder.setView(view);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalerry();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("data", ((BitmapDrawable)imgAvatar.getDrawable()).getBitmap());
                intent.putExtra("uri", imgUri);

                listener.applyBitmapAvatar(intent);

                Toast.makeText(getContext(), "Change avatar successfully", Toast.LENGTH_SHORT).show();
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
        btnConfirm = view.findViewById(R.id.button_ConfirmAvatarProfile);
        btnCancel = view.findViewById(R.id.button_CancelAvatarProfile);
        btnChoose = view.findViewById(R.id.button_ChooseAnotherPicture);

        imgAvatar = view.findViewById(R.id.ImageView_AvatarProfile);
    }
    private void openGalerry(){
        Intent galerry = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galerry, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null && resultCode == Activity.RESULT_OK){
            imgUri = data.getData();
            imgAvatar.setImageURI(imgUri);

            Bitmap oriBitmap = ((BitmapDrawable)imgAvatar.getDrawable()).getBitmap();

            Bitmap newBitmap;
            int width, height, gap, size;
            width = oriBitmap.getWidth();
            height = oriBitmap.getHeight();

            if(width < height){
                gap = (height - width) / 2;
                size = width;
                newBitmap = Bitmap.createBitmap(oriBitmap, 0, gap, size, size);
            }
            else{
                gap = (width - height) / 2;
                size = height;
                newBitmap = Bitmap.createBitmap(oriBitmap, gap, 0, size, size);
            }

            imgAvatar.setImageBitmap(newBitmap);
        }
    }

    public interface ChangeAvatarDialogListener{
        void applyBitmapAvatar(Intent intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (ChangeAvatarDialogListener)context;
    }
}