package com.example.group_32.chatloca;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dath on 4/8/18.
 */

public class Database {
    private static final String USERS = "Users";
    private static final String FRIENDS = "Friends";

    private static final String PASSWORD_FIELD = "password";
    private static final String USERNAME_FIELD = "username";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_FIELD = "phone";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "firstName";
    private static final String GENDER_FIELD = "gender";
    private static final String DAY_OF_BIRTH_FIELD = "gender";
    private static final String ADDRESS_FIELD = "gender";

    private static final String TIME_CREATED_FIELD = "time";
    private static final String RELATIONSHIP_FIELD = "relationship";
    private static final String FRIEND_RELATIONSHIP = "friend";
    private static final String FRIEND_ID_FIELD = "friendId";

    private FirebaseFirestore db;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference messengerRef = database.getReference("messenger");
    private DatabaseReference infoUserRef = database.getReference("userInfo");
    private DatabaseReference UserRefKey = database.getReference("userKey"); // Anh modify
    private DatabaseReference localMessengerRef = database.getReference("localMessenger");
    private DatabaseReference conversationRef = database.getReference("conversation");
    private DatabaseReference meetingRef = database.getReference("meeting");
    private DatabaseReference requestFriendList = database.getReference("requestFriendList"); // Anh modify
    private DatabaseReference requestFriendSender = database.getReference("senderRequestFriend"); // Anh modify
    private DatabaseReference MemberInGroupID = database.getReference("MemberInGroupID"); // Anh modify
    private DatabaseReference chat = database.getReference("Chat");
    private DatabaseReference friendChat = database.getReference("friendChat");
    private DatabaseReference groupChat = database.getReference("groupChat");
    private DatabaseReference user = database.getReference("User");

    private static final String TAG = "Database";

    public Database() {
        db = FirebaseFirestore.getInstance();
    }

    public DatabaseReference getMessengerRefRef() {
        return messengerRef;
    }

    public DatabaseReference getInfoUserRefKey() {
        return UserRefKey;
    }

    public DatabaseReference getInfoUserRef() {
        return infoUserRef;
    }

    public void sendRequestFriend(final String userName_receiver) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final String ID_sender = mAuth.getCurrentUser().getUid();

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    User Request = db.getValue(User.class);
                    if (Request.getUserName().equals(userName_receiver)) {
                        String ID_receiver = db.getKey();
                        Request.setUserName(mAuth.getCurrentUser().getDisplayName());
                        requestFriendList.child(ID_receiver).child(ID_sender).setValue(Request.getUserName());
                        Request.setUserName(userName_receiver);
                        requestFriendSender.child(ID_sender).child(ID_receiver).setValue(Request.getUserName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Log.d(TAG, "SENDREQUESTFRIEND: RUNNING IN DB CLASS");
    }

    public void cancelRequestFriend(final String userName_receiver) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final String ID_sender = mAuth.getCurrentUser().getUid();

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    User Request = db.getValue(User.class);
                    if (Request.getUserName().equals(userName_receiver)) {
                        String ID_receiver = db.getKey();
                        requestFriendSender.child(ID_sender).child(ID_receiver).removeValue();
                        requestFriendList.child(ID_receiver).child(ID_sender).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void deleteFriend(final String username_friend) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final String ID = mAuth.getCurrentUser().getUid();
        UserRefKey.child(username_friend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ID_receiver = dataSnapshot.getValue(String.class);
                chat.child(ID).child(ID_receiver).removeValue();
                friendChat.child(ID).child(ID_receiver).removeValue();
                chat.child(ID_receiver).child(ID).removeValue();
                friendChat.child(ID_receiver).child(ID).removeValue();
                meetingRef.child((ID + ID_receiver)).removeValue();
                meetingRef.child((ID_receiver + ID)).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        chat.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot db : dataSnapshot.getChildren()) {
//                    Conversation friend = db.getValue(Conversation.class);
//                    if (friend.getConversationName().equals(username_friend)) {
//                        String ID_receiver = db.getKey();
//                        chat.child(ID).child(ID_receiver).removeValue();
//                        friendChat.child(ID).child(ID_receiver).removeValue();
//                        chat.child(ID_receiver).child(ID).removeValue();
//                        friendChat.child(ID_receiver).child(ID).removeValue();
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }

    public String CreateGroup(final String[] userName_Member, String NameOfGroup) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final String ID_UserCreate = mAuth.getCurrentUser().getUid();
        final Conversation conver = new Conversation(NameOfGroup, "Group Chat");
        final DatabaseReference group = chat.child(ID_UserCreate).push();
        group.setValue(conver);
        MemberInGroupID.child(group.getKey()).child(ID_UserCreate).setValue(mAuth.getCurrentUser().getDisplayName());
        meetingRef.child(group.getKey()).setValue(new Conversation(ID_UserCreate, "", "", ""));
        for (int i = 0; i < userName_Member.length; i++) {
            final int finalI = i;
            UserRefKey.child(userName_Member[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String friendkey = dataSnapshot.getValue(String.class);
                    chat.child(friendkey).child(group.getKey()).setValue(conver);
                    MemberInGroupID.child(group.getKey()).child(friendkey).setValue(userName_Member[finalI]);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return group.getKey();
    }

    public void deleteGroup(final String ID_group) {
        String ID_User = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MemberInGroupID.child(ID_group).child(ID_User).removeValue();
        chat.child(ID_User).child(ID_group).removeValue();
        groupChat.child(ID_User).child(ID_group).removeValue();

        MemberInGroupID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(ID_group))
                    meetingRef.child(ID_group).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void saveConversation(Conversation conver) {
        DatabaseReference newConverId = conversationRef.push();
//        localMessengerRef.setValue(newConverId.);

        newConverId.setValue(conver);
    }

    // TinNguyen
    public void addUser(User user) {
        CollectionReference users = db.collection(USERS);

        Map<String, Object> data = new HashMap<>();
        data.put(USERNAME_FIELD, user.getUserName());
        data.put(PASSWORD_FIELD, user.getPassword());
        data.put(FIRST_NAME_FIELD, user.getFirstName());
        data.put(LAST_NAME_FIELD, user.getLastName());
        data.put(EMAIL_FIELD, user.getEmail());
        data.put(PHONE_FIELD, user.getPhone());
        data.put(GENDER_FIELD, user.getGender());
        data.put(DAY_OF_BIRTH_FIELD, user.getDateofbirth());
        data.put(ADDRESS_FIELD, user.getAddress());

        users.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void changePassword(String username, final String newPass) {
        db.collection(USERS)
                .whereEqualTo(USERNAME_FIELD, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection(USERS).document(document.getId()).update(PASSWORD_FIELD, newPass);
                            }
                        }
                    }
                });
    }

    public void changePhone(String username, final String newPhone) {
        db.collection(USERS)
                .whereEqualTo(USERNAME_FIELD, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection(USERS).document(document.getId()).update(PHONE_FIELD, newPhone);
                            }
                        }
                    }
                });
    }

    public void addFriend(String username, final String usernameOfFriend) {
        db.collection(USERS)
                .whereEqualTo(USERNAME_FIELD, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                final Map<String, Object> map = new HashMap<>();

                                map.put(RELATIONSHIP_FIELD, FRIEND_RELATIONSHIP);
                                map.put(FRIEND_ID_FIELD, usernameOfFriend);
                                map.put(TIME_CREATED_FIELD, Timestamp.now());

                                db.collection(USERS).document(document.getId()).collection(FRIENDS).add(map);
                            }
                        }
                    }
                });
    }

}
