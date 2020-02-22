package com.example.group_32.chatloca.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.group_32.chatloca.Conversation;
import com.example.group_32.chatloca.Database;
import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.User;
import com.example.group_32.chatloca.dialogs.CreateGroupDialog;
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
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageActivity extends BaseActivity {

    private static final String TAG = "MessageActivity";
    private LinearLayout linearlayoutChat;
    private LinearLayout linearlayoutAddfriend;
    private TextView tvAddFriend;
    private TextView tvAddGroup;
    private android.support.v7.widget.SearchView searchvSearch;
    private ScrollView scrollViewMess;
    private ScrollView scrollViewAddFriend;
    private TextView tvNameMessage;
    private CircleImageView circleImageAvatar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //public boolean isTesting = false;
    int id = 0;
    int idAddUser = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.mapping();

        setUserNameToLayout();
        setAvatar(mAuth.getCurrentUser().getUid(), circleImageAvatar);

        ImageView imgvCallActivityProfile = findViewById(R.id.image_avatar);
        imgvCallActivityProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        id = 0;
        idAddUser = 0;

        tvAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollViewAddFriend.setVisibility(View.VISIBLE);
                scrollViewMess.setVisibility(View.GONE);
                searchvSearch.setIconified(false);
            }
        });

        tvAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < id; i++) {
                    final Button cancel = findViewById(R.id.cancel + i);
                    final Button delete = findViewById(R.id.delete + i);
                    DisappearDeleteAndCancel(cancel, 0, 1);
                    DisappearDeleteAndCancel(delete, 1, 0);
                }
                CreateGroupDialog dialog = new CreateGroupDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });

/*        createChatView("sfs", "sfdf", "sf", "friend");
        SettingFriendChat();*/
        SettingSearch();
        LoadFriendList();
        LoadRequestedFriend();
    }

    @Override
    public void onBackPressed() {
        if (scrollViewAddFriend.getVisibility() == View.VISIBLE) {
            scrollViewAddFriend.setVisibility(View.GONE);
            scrollViewMess.setVisibility(View.VISIBLE);
            searchvSearch.setQuery("", false);
            searchvSearch.setIconified(true);

        } else if (!searchvSearch.isIconified()) {
            searchvSearch.setIconified(true);
        }
        for (int i = 0; i < id; i++) {
            final Button cancel = findViewById(R.id.cancel + i);
            final Button delete = findViewById(R.id.delete + i);
            DisappearDeleteAndCancel(cancel, 0, 1);
            DisappearDeleteAndCancel(delete, 1, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    public void createChatView(String userName, String ID_User, String NameOfUser, String type) {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setTag(userName);
        relativeLayout.setId(R.id.ChatFriend + id);

        // khởi tạo parameter cho relativeLayout (chat)
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.topMargin = 20;
        textparams.rightMargin = 20;
        textparams.leftMargin = 20;
        // tạo ra TextView
        TextView textviewUserName = new TextView(this);
        textviewUserName.setBackground(getDrawable(R.drawable.input_text_chat));
        textviewUserName.setId(id);
        textviewUserName.setTag(type);
        textviewUserName.setHeight(150);
        textviewUserName.setGravity(Gravity.CENTER);
        textviewUserName.setTextSize(20);
        textviewUserName.setText(NameOfUser);
        // Cho TextView vào parameter vừa tạo ra
        relativeLayout.addView(textviewUserName, textparams);

        // khởi tạo parameter cho relativeLayout (avatar)
        RelativeLayout.LayoutParams imageparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageparams.topMargin = 20;
        imageparams.leftMargin = 20;
        imageparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // tạo ra ImageView
        CircleImageView image = new CircleImageView(MessageActivity.this);
        relativeLayout.addView(image, imageparams);
        if (type.equals("Group Chat")) image.setImageResource(R.drawable.ic_request_friend);
        else setAvatar(ID_User, image);
        image.setTag(textviewUserName.getText() + "Avatar");
        image.getLayoutParams().height = 150;
        image.getLayoutParams().width = 150;
        image.setBorderColor(Color.parseColor("#FFFFFFFF"));
        image.setBorderWidth(3);
        // Cho ImageView vào parameter vừa tạo ra

        // khởi tạo parameter cho relativeLayout (location)
        RelativeLayout.LayoutParams locationparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        locationparams.topMargin = 50;
        locationparams.rightMargin = 30;
        locationparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // tạo ra TextView
        TextView textviewLocation = new TextView(this);
        textviewLocation.setId(R.id.location + id);
        textviewLocation.setTag(textviewUserName.getText() + "Location");
        textviewLocation.setBackgroundResource(R.drawable.ic_locate);
        textviewLocation.setHeight(100);
        textviewLocation.setWidth(100);
        // Cho TextView vào parameter vừa tạo ra
        relativeLayout.addView(textviewLocation, locationparams);

        // khởi tạo parameter cho relativeLayout (Delete)
        RelativeLayout.LayoutParams deleteparams = new RelativeLayout.LayoutParams(
                250, 120);
        deleteparams.topMargin = 40;
        deleteparams.rightMargin = 150;
        deleteparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // tạo ra TextView
        Button btnDelete = new Button(this);
        btnDelete.setTag(textviewUserName.getText() + "Delete");
        btnDelete.setId(R.id.delete + id);
        btnDelete.setText("Delete");
        btnDelete.setGravity(Gravity.CENTER);
        btnDelete.setTextSize(15);
        btnDelete.setBackgroundResource(R.drawable.button_v3);
        btnDelete.setVisibility(View.GONE);
        // Cho Button vào parameter vừa tạo ra
        relativeLayout.addView(btnDelete, deleteparams);

        // khởi tạo parameter cho relativeLayout (Cancel)
        RelativeLayout.LayoutParams cancelparams = new RelativeLayout.LayoutParams(
                250, 120);
        cancelparams.topMargin = 40;
        cancelparams.leftMargin = 170;

        // tạo ra TextView
        Button btnCancel = new Button(this);
        btnCancel.setTag(textviewUserName.getText() + "Cancel");
        btnCancel.setId(R.id.cancel + id);
        btnCancel.setText("Cancel");
        btnCancel.setGravity(Gravity.CENTER);
        btnCancel.setTextSize(15);
        btnCancel.setBackgroundResource(R.drawable.button_v2);
        btnCancel.setVisibility(View.GONE);
        // Cho Button vào parameter vừa tạo ra
        relativeLayout.addView(btnCancel, cancelparams);

        // thêm thanh chat mới vào message
        linearlayoutChat.addView(relativeLayout);
        id++;
    }

    @SuppressLint("SetTextI18n")
    public void createAddUserView(String userName, String ID_User, String NameOfUser) {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setTag(userName);
        relativeLayout.setId(R.id.RelativeLAddFriend + idAddUser);
        relativeLayout.setVisibility(View.GONE);

        // khởi tạo parameter cho relativeLayout (user)
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.topMargin = 20;
        textparams.rightMargin = 20;
        textparams.leftMargin = 20;
        // tạo ra TextView
        TextView textviewUserName = new TextView(this);
        textviewUserName.setBackgroundColor(Color.parseColor("#FFFFFF"));
        textviewUserName.setBackground(getDrawable(R.drawable.line));
        textviewUserName.setHeight(150);
        textviewUserName.setGravity(Gravity.CENTER);
        textviewUserName.setTextSize(20);
        textviewUserName.setText(NameOfUser);
        textviewUserName.setId(R.id.txtAddFriend + idAddUser);
        // Cho TextView vào parameter vừa tạo ra
        relativeLayout.addView(textviewUserName, textparams);

        // khởi tạo parameter cho relativeLayout (avatar)
        RelativeLayout.LayoutParams imageparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageparams.topMargin = 20;
        imageparams.leftMargin = 20;
        imageparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // tạo ra ImageView

        CircleImageView image = new CircleImageView(MessageActivity.this);
        relativeLayout.addView(image, imageparams);
        setAvatar(ID_User, image);
        image.setTag(textviewUserName.getText() + "Avatar");
        image.setBorderWidth(3);
        image.setBorderColor(Color.parseColor("#FFB3B2B2"));
        image.getLayoutParams().height = 150;
        image.getLayoutParams().width = 150;
        // Cho ImageView vào parameter vừa tạo ra

        // khởi tạo parameter cho relativeLayout (add)
        RelativeLayout.LayoutParams addparams = new RelativeLayout.LayoutParams(
                210, 120);
        addparams.topMargin = 40;
        addparams.rightMargin = 30;
        addparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // tạo ra TextView
        Button btnAdd = new Button(this);
        btnAdd.setId(R.id.addButton + idAddUser);
        btnAdd.setGravity(Gravity.CENTER);
        btnAdd.setPaddingRelative(10, 10, 0, 0);
        btnAdd.setTag(textviewUserName.getText() + "Add");
        btnAdd.setText("Add");
        btnAdd.setBackgroundResource(R.drawable.button_v2);
        btnAdd.setTextSize(15);
        // Cho ImageView vào parameter vừa tạo ra
        relativeLayout.addView(btnAdd, addparams);

        // thêm thanh chat mới vào message
        linearlayoutAddfriend.addView(relativeLayout);
        idAddUser++;
    }

    public void scaleView(View v, float startScaleX, float endScaleX, float pivotX, float pivotY) {
        Animation anim = new ScaleAnimation(
                startScaleX, endScaleX, // Start and end values for the X axis scaling
                (float) 1, (float) 1, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, pivotX, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, pivotY); // Pivot point of Y scaling
        anim.setFillAfter(false); // Needed to keep the result of the animation
        anim.setDuration(300);
        v.startAnimation(anim);
    }

    public void AnimationforDeleteAndCancel(View v, float pivotX, float pivotY) {
        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
            scaleView(v, 0, 1, pivotX, pivotY);
        } else if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
            scaleView(v, 1, 0, pivotX, pivotY);
        }
    }

    public void DisappearDeleteAndCancel(View v, float pivotX, float pivotY) {
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
            scaleView(v, 1, 0, pivotX, pivotY);
        }
    }

    public void SettingSearch() {
        // Search friend's list
        searchvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchvSearch.setIconified(false);
                for (int i = 0; i < id; i++) {
                    final Button cancel = findViewById(R.id.cancel + i);
                    final Button delete = findViewById(R.id.delete + i);
                    DisappearDeleteAndCancel(cancel, 0, 1);
                    DisappearDeleteAndCancel(delete, 1, 0);
                }
            }
        });
        // Search friend's list
        searchvSearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchvSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    callSearch(newText);
                } else {
                    if (scrollViewMess.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < id; i++) {
                            RelativeLayout relativeLayout = findViewById(R.id.ChatFriend + i);
                            if (relativeLayout.getTag() != "@Delete")
                                relativeLayout.setVisibility(View.VISIBLE);
                        }
                    } else { // scrollViewAddfriend visible
                        for (int i = 0; i < idAddUser; i++) {
                            RelativeLayout relativeLayout = findViewById(R.id.RelativeLAddFriend + i);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return true;
            }

            void callSearch(final String query) {
                //Do searching
                showProgressDialog();
                if (scrollViewMess.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < id; i++) {
                        RelativeLayout relativeLayout = findViewById(R.id.ChatFriend + i);
                        TextView textViewUsername = findViewById(i);
                        if (!textViewUsername.getText().toString().contains(query) && !relativeLayout.getTag().toString().contains(query))
                            relativeLayout.setVisibility(View.GONE);
                        else relativeLayout.setVisibility(View.VISIBLE);
                    }
                    hideProgressDialog();
                } else { // scrollViewAddfriend visible
                    /**
                     *  get User by query in database and createAddUserView
                     * */

                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    final String username = mAuth.getCurrentUser().getDisplayName();
                    DatabaseReference ref = database.getReference("User");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot db : snapshot.getChildren()) {
                                final User user = db.getValue(User.class);
                                final String ID_user = db.getKey();
                                if (user != null && !user.getUserName().equals(username)) {
                                    if (user.getUserName().contains(query) || user.getNameOfUser().contains(query)) {
                                        boolean isExist = false;
                                        for (int i = 0; i < idAddUser; i++) {
                                            RelativeLayout relativeLayout = findViewById(R.id.RelativeLAddFriend + i);
                                            if (user.getUserName().equals(relativeLayout.getTag().toString())) {
                                                isExist = true;
                                                break;
                                            }
                                        }
                                        if (!isExist) {
                                            createAddUserView(user.getUserName(), ID_user, user.getNameOfUser());
                                            SettingAddFriend();
                                            for (int i = 0; i < id; i++) {
                                                RelativeLayout relativeLayout = findViewById(R.id.ChatFriend + i);
                                                if (!relativeLayout.getTag().toString().equals("@Delete")) {
                                                    if (relativeLayout.getTag().toString().equals(user.getUserName())) {
                                                        final Button btnAdd = findViewById(R.id.addButton + idAddUser - 1);
                                                        btnAdd.setText("Friend");
                                                        btnAdd.setBackgroundResource(R.drawable.button_v2);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        DatabaseReference refRequestFriend = database.getReference("requestFriendList");
                                        refRequestFriend.child(mAuth.getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        if (!dataSnapshot.exists()) return;
                                                        if (dataSnapshot.hasChild(ID_user)) {
                                                            String userName_User = dataSnapshot.child(ID_user).getValue(String.class);
                                                            for (int i = 0; i < idAddUser; i++) {
                                                                RelativeLayout relativeLayout = findViewById(R.id.RelativeLAddFriend + i);
                                                                if (relativeLayout.getTag().toString().equals(userName_User)) {
                                                                    final Button btnAdd = findViewById(R.id.addButton + i);
                                                                    btnAdd.setText("Request");
                                                                    btnAdd.setBackgroundResource(R.drawable.button);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                    }
                                }
                            }
                            for (int i = 0; i < idAddUser; i++) {
                                RelativeLayout relativeLayout = findViewById(R.id.RelativeLAddFriend + i);
                                TextView textViewAddFriend = findViewById(R.id.txtAddFriend + i);
                                if (!relativeLayout.getTag().toString().contains(query) && !textViewAddFriend.getText().toString().contains(query))
                                    relativeLayout.setVisibility(View.GONE);
                                else relativeLayout.setVisibility(View.VISIBLE);
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            hideProgressDialog();
                        }
                    });
                }
            }
        });
    }

    public void SettingFriendChat() {
        // chat view
        final String ID_User = mAuth.getCurrentUser().getUid();
        int i = id - 1;
        final RelativeLayout relativeLayout = findViewById(R.id.ChatFriend + i);
        final Button cancel = findViewById(R.id.cancel + i);
        final Button delete = findViewById(R.id.delete + i);
        final TextView location = findViewById(R.id.location + i);
        final TextView chatView = findViewById(i);

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AnimationforDeleteAndCancel(cancel, 0, 1);
                AnimationforDeleteAndCancel(delete, 1, 0);
                return true;
            }
        });

        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
                if (chatView.getTag().toString().equals("Friend Chat")) {
                    DatabaseReference refUserKey = database.getReference("userKey").child(relativeLayout.getTag().toString());
                    refUserKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String ID_Friend = dataSnapshot.getValue(String.class);
                            //----------------------------------------------------------------------------------------
                            //  Truyền dữ liệu cho ChatActivity Friend chat
                            intent.putExtra("visit_user_id", ID_Friend);
                            intent.putExtra("user_name", chatView.getText().toString());
                            intent.putExtra("type_chat", chatView.getTag().toString());
                            startActivity(intent);
                            //----------------------------------------------------------------------------------------
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    //----------------------------------------------------------------------------------------
                    //  Truyền dữ liệu cho ChatActivity Group chat
                    intent.putExtra("visit_user_id", relativeLayout.getTag().toString());
                    intent.putExtra("user_name", chatView.getText().toString());
                    intent.putExtra("type_chat", chatView.getTag().toString());
                    startActivity(intent);
                    //----------------------------------------------------------------------------------------
                }
            }
        });
        chatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AnimationforDeleteAndCancel(cancel, 0, 1);
                AnimationforDeleteAndCancel(delete, 1, 1);
                return true;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaleView(cancel, 1, 0, 0, 1);
                scaleView(delete, 1, 0, 1, 1);
                view.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = new Database();
                if (chatView.getTag().toString().equals("Friend Chat"))
                    db.deleteFriend(relativeLayout.getTag().toString());
                else db.deleteGroup(relativeLayout.getTag().toString());

                relativeLayout.setVisibility(View.GONE);
                relativeLayout.setTag("@Delete");
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,  "Click on Location");
                final Intent intent = new Intent(MessageActivity.this, MapsActivity.class);
                // is Friend Chat
                if (chatView.getTag().toString().equals("Friend Chat")) {
                    DatabaseReference refUserKey = database.getReference("userKey").child(relativeLayout.getTag().toString());
                    refUserKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String ID_Friend = dataSnapshot.getValue(String.class);
                            final DatabaseReference refMeeting = database.getReference("meeting").child(ID_User + ID_Friend);
                            refMeeting.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                                        intent.putExtra("mConverId", ID_User + ID_Friend);
                                        intent.putExtra("mIsMessageActi", "True");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MessageActivity.this, "Do not have the meeting", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                // is Group Chat
                else {
                    final DatabaseReference refMeeting = database.getReference("meeting").child(relativeLayout.getTag().toString());
                    refMeeting.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                                intent.putExtra("mConverId", relativeLayout.getTag().toString());
                                intent.putExtra("mIsMessageActi", "True");
                                startActivity(intent);
                            } else {
                                Toast.makeText(MessageActivity.this, "Do not have the meeting", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

        });

        location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AnimationforDeleteAndCancel(cancel, 0, 1);
                AnimationforDeleteAndCancel(delete, 1, 1);
                return true;
            }
        });
    }

    public void SettingAddFriend() {
        Log.d(TAG, "SETTTINGFRIEND: BEGIN");
        int i = idAddUser - 1;
        final RelativeLayout relativeLayout = findViewById(R.id.RelativeLAddFriend + i);
        final TextView textViewAddFriend = findViewById(R.id.txtAddFriend + i);
        final Button btnAdd = findViewById(R.id.addButton + i);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnAdd.getText().equals("Add")) {
                    Database mydb = new Database();
                    Log.d(TAG, "SENDREQUESTFRIEND: IN");
                    mydb.sendRequestFriend(relativeLayout.getTag().toString());

                    btnAdd.setText("Sent");
                    btnAdd.setBackgroundResource(R.drawable.button_v4);
                } else if (btnAdd.getText().equals("Sent")) {
                    Database mydb = new Database();
                    mydb.cancelRequestFriend(relativeLayout.getTag().toString());
                    btnAdd.setText("Add");
                    btnAdd.setBackgroundResource(R.drawable.button_v2);
                }
            }
        });

        textViewAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MessageActivity.this, ViewProfileActivity.class);
                final String userName_other = relativeLayout.getTag().toString();
                DatabaseReference refUserKey = database.getReference("userKey").child(userName_other);
                refUserKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ID_other = dataSnapshot.getValue(String.class);
                        intent.putExtra("userID", ID_other);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    public void setUserNameToLayout() {
        database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("nameOfUser").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNameMessage.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void LoadRequestedFriend() {
        showProgressDialog();
        String id_user = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = database.getReference("senderRequestFriend").child(id_user);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot db : snapshot.getChildren()) {
                    DatabaseReference refUser = database.getReference("User").child(db.getKey());
                    final String ID_Other = db.getKey();
                    refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User otherUser = dataSnapshot.getValue(User.class);
                            createAddUserView(otherUser.getUserName(), ID_Other, otherUser.getNameOfUser());
                            Button btnAdd = findViewById(R.id.addButton + idAddUser - 1);
                            btnAdd.setText("Sent");
                            btnAdd.setBackgroundResource(R.drawable.button_v4);
                            SettingAddFriend();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    public void LoadFriendList() {
        // LoadFriend create friend chat
        showProgressDialog();
        String Username = mAuth.getCurrentUser().getDisplayName();
        final String ID_User = mAuth.getCurrentUser().getUid();
        if (Username == null) return;
        DatabaseReference ref = database.getReference("Chat").child(ID_User);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    final String ID_Other = db.getKey();
                    final Conversation conversation = db.getValue(Conversation.class);
                    final String UserName_Other = conversation.getConversationName();

                    if (conversation.getType().equals("Friend Chat")) {
                        DatabaseReference refUser = database.getReference("User").child(ID_Other);
                        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User FriendInfo = dataSnapshot.getValue(User.class);
                                createChatView(UserName_Other, ID_Other, FriendInfo.getNameOfUser(), conversation.getType());
                                SettingFriendChat();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        // CreateChatView Group conversation
                        createChatView(db.getKey(), db.getKey(), UserName_Other, conversation.getType());
                        SettingFriendChat();
                    }
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    private void mapping() {
        tvAddFriend = findViewById(R.id.textview_add_friend_message);
        tvAddGroup = findViewById(R.id.textview_add_group_message);
        searchvSearch = findViewById(R.id.searchview_Message);
        tvNameMessage = findViewById(R.id.textview_NameMessage);
        circleImageAvatar = findViewById(R.id.image_avatar);
        scrollViewAddFriend = findViewById(R.id.scrollviewAddfriend_Message);
        scrollViewMess = findViewById(R.id.scrollView_Message);
        linearlayoutChat = findViewById(R.id.Linearlayoutchat_Message);
        linearlayoutAddfriend = findViewById(R.id.LinearlayoutAddfriend_Message);
    }

    private void setAvatar(final String UserID, final CircleImageView image) {
//        if (checkImageExist(UserID)) {
//            //Toast.makeText(this, "EXIST", Toast.LENGTH_SHORT).show();
//            Bitmap bitmap = loadImageFromStorage(UserID);
//            image.setImageBitmap(bitmap);
//        } else {
        showProgressDialog();
        final String AVATAR = "avatar";
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        if (localFile != null) {
            FirebaseStorage.getInstance().getReference()
                    .child(AVATAR + "/" + UserID)
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            image.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                            saveToInternalStorage(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), UserID);
                            hideProgressDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            image.setImageResource(R.drawable.ic_user);
                            hideProgressDialog();
                        }
                    });
        }
//        }
    }

    private void saveToInternalStorage(Bitmap bitmap, String userId) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, userId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private boolean checkImageExist(String userId) {
//        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//
//        // path to /data/data/your app/app_data/avatar
//        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);
//
//        // Create imageDir
//        File mypath = new File(directory, userId + ".jpg");
//        return mypath.exists();
//    }

//    private Bitmap loadImageFromStorage(String userId) {
//        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//
//        // path to /data/data/your app/app_data/avatar
//        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);
//
//        // Create imageDir
//        File mypath = new File(directory, userId + ".jpg");
//        try {
//            return BitmapFactory.decodeStream(new FileInputStream(mypath));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
