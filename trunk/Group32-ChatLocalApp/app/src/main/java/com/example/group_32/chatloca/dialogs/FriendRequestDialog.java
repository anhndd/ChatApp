package com.example.group_32.chatloca.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.activities.ProfileActivity;
import com.example.group_32.chatloca.activities.ViewProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestDialog extends AppCompatDialogFragment {
    private final String USER = "User";
    private final String CHAT = "Chat";
    private final String FRIEND_CHAT = "Friend Chat";
    private final String TYPE = "type";
    private final String CONVERSATION_NAME = "conversationName";
    private final String NAME_OF_USER = "nameOfUser";
    private final String DATE_OF_BIRTH = "dateofbirth";
    private final String REQUEST_FRIEND_LIST = "requestFriendList";
    private final String SENDER_REQUEST_FRIEND = "senderRequestFriend";
    private final String AVATAR = "avatar";
    private final String USERNAME = "userName";
    private final String MEETING = "meeting";
    private final String USER_ID_CREATED = "userIdCreated";
    private final String TIME = "time";
    private final String NAME_OF_MEETING = "nameOfMeeting";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String DATE = "date";
    private final String LATITUDE_LONGITUDE = "latLng";

    private Button btnClose;
    private ListView lvFriendRequests;
    private ArrayList<String> arrayList = new ArrayList<>();
    private MyListAdapter myListAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_friend_request, null);
        mapping(view);
        loadListFriendRequests();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myListAdapter = new MyListAdapter(getContext(), R.layout.friend_request_row, arrayList);
        lvFriendRequests.setAdapter(myListAdapter);

        builder.setView(view);

        return builder.create();
    }
    public void mapping(View view){
        btnClose = view.findViewById(R.id.Button_Close_FriendRequestDialog);
        lvFriendRequests = view.findViewById(R.id.ListView_FriendRequests);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        lvFriendRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userId = adapterView.getItemAtPosition(i).toString();

                Intent intent = new Intent(getContext(), ViewProfileActivity.class);
                intent.putExtra("userID", userId);
                startActivity(intent);
            }
        });
    }

    public void loadListFriendRequests(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance()
                .getReference(REQUEST_FRIEND_LIST)
                .child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            arrayList.add(ds.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        private List<String> objects;

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);

            layout = resource;
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.imgThumbnail = convertView.findViewById(R.id.ImageView_Avatar_FriendRequestRow);
                viewHolder.tvDisplayName = convertView.findViewById(R.id.TextView_DisplayName_FriendRequestRow);
                viewHolder.tvBirthday = convertView.findViewById(R.id.TextView_Birthday_FriendRequestRow);
                viewHolder.btnConfirm = convertView.findViewById(R.id.Button_Confirm_FriendRequestRow);
                viewHolder.btnDelete = convertView.findViewById(R.id.Button_Delete_FriendRequestRow);

                convertView.setTag(viewHolder);
            }

            mainViewHolder = (ViewHolder)convertView.getTag();

            final String userId = getItem(position);
            final ViewHolder finalMainViewHolder = mainViewHolder;

            FirebaseDatabase.getInstance()
                    .getReference(USER)
                    .child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            finalMainViewHolder.tvDisplayName.setText(dataSnapshot.child(NAME_OF_USER).getValue(String.class));
                            finalMainViewHolder.tvBirthday.setText(dataSnapshot.child(DATE_OF_BIRTH).getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            FirebaseStorage.getInstance().getReference()
                    .child(AVATAR + "/" + userId)
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            finalMainViewHolder.imgThumbnail.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                        }
                    });

            //  Event Accept / Delete
            final ViewHolder finalMainViewHolder1 = mainViewHolder;
            mainViewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String userId = getItem(position);

                    FirebaseDatabase.getInstance()
                            .getReference(USER)
                            .child(userId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child(USERNAME).getValue(String.class);
                                    FirebaseDatabase.getInstance()
                                            .getReference(CHAT)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(userId)
                                            .child(CONVERSATION_NAME)
                                            .setValue(userName);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    FirebaseDatabase.getInstance()
                            .getReference(USER)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child(USERNAME).getValue(String.class);
                                    FirebaseDatabase.getInstance()
                                            .getReference(CHAT)
                                            .child(userId)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(CONVERSATION_NAME)
                                            .setValue(userName);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    FirebaseDatabase.getInstance()
                            .getReference(CHAT)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId)
                            .child(TYPE)
                            .setValue(FRIEND_CHAT);

                    FirebaseDatabase.getInstance()
                            .getReference(CHAT)
                            .child(userId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(TYPE)
                            .setValue(FRIEND_CHAT);

                    addMeeting(userId, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    addMeeting(FirebaseAuth.getInstance().getCurrentUser().getUid(), userId);
                    ProfileActivity.hasChange = true; // Anh modified
                    finalMainViewHolder1.btnDelete.performClick();
                }
            });
            mainViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance()
                            .getReference(REQUEST_FRIEND_LIST)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userId)
                            .removeValue();

                    FirebaseDatabase.getInstance()
                            .getReference(SENDER_REQUEST_FRIEND)
                            .child(userId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();


                    int length = arrayList.size();
                    Toast.makeText(getContext(), String.valueOf(length), Toast.LENGTH_LONG).show();
                    arrayList.remove(position);
                    ArrayList<String> newArrayList = new ArrayList<>();
                    for(String s: arrayList){
                        newArrayList.add(s);
                    }
                    arrayList.clear();
                    myListAdapter.notifyDataSetChanged();

                    for(String s: newArrayList){
                        arrayList.add(s);
                    }
                    myListAdapter.notifyDataSetChanged();
//                    myListAdapter.notify();
                }
            });
            mainViewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewProfileActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
    public class ViewHolder{
        ImageView imgThumbnail;
        TextView tvDisplayName, tvBirthday;
        ImageButton btnConfirm, btnDelete;
    }

    public void addMeeting(String userId1, String userId2){
        Map<String, Object> meetingUser = new HashMap<>();
        meetingUser.put(USER_ID_CREATED, userId1);
        meetingUser.put(TIME, "");
        meetingUser.put(NAME_OF_MEETING, "");/*
        meetingUser.put(LATITUDE, 0.0);
        meetingUser.put(LONGITUDE, 0.0);*/
        meetingUser.put(DATE, "");

/*        Map<String, Object> latLng = new HashMap<>();
        latLng.put(LATITUDE, 0.0);
        latLng.put(LONGITUDE, 0.0);*/

        FirebaseDatabase.getInstance()
                .getReference(MEETING)
                .child(userId1 + userId2)
                .setValue(meetingUser);
/*        FirebaseDatabase.getInstance()
                .getReference(MEETING)
                .child(userId1 + userId2)
                .child(LATITUDE_LONGITUDE)
                .setValue(latLng);*/
    }
}
