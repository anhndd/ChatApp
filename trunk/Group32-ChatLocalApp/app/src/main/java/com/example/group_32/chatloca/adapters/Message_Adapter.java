package com.example.group_32.chatloca.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.ContextWrapper;
import android.content.Context;

import com.example.group_32.chatloca.Messages;
import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.activities.ChatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ChatActivity context;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference UserDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private List<Messages> userMessageList;

    public Message_Adapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 1:
                context = (ChatActivity) parent.getContext();
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message2, parent, false);
                viewHolder = new MyChatViewHolder(v);
                break;
            case 2:
                context = (ChatActivity) parent.getContext();
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message, parent, false);
                viewHolder = new OtherChatViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String fromUserId = userMessageList.get(position).getSenderId();

        if (userMessageList.get(position).getSenderId().equals(mAuth.getCurrentUser().getUid().toString())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);


        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(final MyChatViewHolder myChatViewHolder, final int position) {
        Messages messages = userMessageList.get(position);
        myChatViewHolder.txtChatMessage.setText(messages.getMessage());

        /*if(checkImageExist(userMessageList.get(position).getSenderId())) {
            //Toast.makeText(this, "EXIST", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = loadImageFromStorage(userMessageList.get(position).getSenderId());
            myChatViewHolder.userProfile.setImageBitmap(bitmap);
        }
        else {
            final String AVATAR = "avatar";
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            FirebaseStorage.getInstance().getReference()
                    .child(AVATAR + "/" + userMessageList.get(position).getSenderId())
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            myChatViewHolder.userProfile.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                            saveToInternalStorage(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), userMessageList.get(position).getSenderId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                            myChatViewHolder.userProfile.setImageResource(R.drawable.ic_user);
                        }
                    });
        }*/
    }

    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, final int position) {
        Messages messages = userMessageList.get(position);
        otherChatViewHolder.txtChatMessage.setText(messages.getMessage());

        UserDatabaseReference.child("User").child(messages.getSenderId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("nameOfUser")) {
                        otherChatViewHolder.userProfile.setText(ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*if(checkImageExist(userMessageList.get(position).getSenderId())) {
            //Toast.makeText(this, "EXIST", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = loadImageFromStorage(userMessageList.get(position).getSenderId());
            otherChatViewHolder.userProfile.setImageBitmap(bitmap);
        }
        else {
            final String AVATAR = "avatar";
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            FirebaseStorage.getInstance().getReference()
                    .child(AVATAR + "/" + userMessageList.get(position).getSenderId())
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            otherChatViewHolder.userProfile.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                            saveToInternalStorage(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), userMessageList.get(position).getSenderId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                            otherChatViewHolder.userProfile.setImageResource(R.drawable.ic_user);
                        }
                    });
        }*/
    }

    @Override
    public int getItemCount() {
        if (userMessageList != null) {
            return userMessageList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (userMessageList.get(position).getSenderId().equals(mAuth.getCurrentUser().getUid().toString())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage;
        private TextView userProfile;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.message_txt);
        }
    }

    private class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage;
        private TextView userProfile;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.message_txt);
            userProfile = (TextView) itemView.findViewById(R.id.message_profile);
        }
    }

    /*private void saveToInternalStorage(Bitmap bitmap, String userId){
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", android.content.Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, fos);
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

    private boolean checkImageExist(String userId){
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());

        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");
        return mypath.exists();
    }

    private Bitmap loadImageFromStorage(String userId) {
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());

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
    }*/
}
