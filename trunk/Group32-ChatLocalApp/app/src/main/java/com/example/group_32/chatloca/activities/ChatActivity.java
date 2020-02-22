package com.example.group_32.chatloca.activities;

import android.app.Activity;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.group_32.chatloca.adapters.Message_Adapter;
import com.example.group_32.chatloca.Messages;
import com.example.group_32.chatloca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView list_message;
    private final List<Messages> messageList = new ArrayList<>();
    private String messageReceiverId;
    private String messageReceiverName;
    private String messageSenderId;
    private String typeChat;
    private String address;
    private Double longitude, latitude;

    public static final int MY_REQUEST_CODE = 100;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    private LinearLayoutManager linearLayoutManager;
    private Message_Adapter messageAdapter;
    private ImageView btnSendMessage;
    private EditText editText;
    private Dialog myDialog;
    private Button btnSetApp, btnSetPos;
    private Button btnDate, btnTime;
    private TextView txtDate, txtTime;
    private EditText edtName;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TextView txt_name, txt_date, txt_time;
    private TextView txtNameLocation;
    private String name, date, time;
    private Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        messageSenderId = mAuth.getCurrentUser().getUid();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();

        messageReceiverName = getIntent().getExtras().get("user_name").toString();

        typeChat = getIntent().getExtras().get("type_chat").toString();

        messageAdapter = new Message_Adapter(messageList);

        list_message = (RecyclerView) findViewById(R.id.recyler_list_message);

        linearLayoutManager = new LinearLayoutManager(this);

        list_message.setHasFixedSize(true);

        list_message.setLayoutManager(linearLayoutManager);

        list_message.setAdapter(messageAdapter);

        txt_name = (TextView) findViewById(R.id.appoint_name);
        txt_date = (TextView) findViewById(R.id.appoint_date);
        txt_time = (TextView) findViewById(R.id.appoint_time);

        fetchMessage();

        fetchAppointment();

        editText = (EditText) findViewById(R.id.edtxt_Chat);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (typeChat.equals("Friend Chat")) {
                        rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long count = dataSnapshot.getChildrenCount();
                                if (count != 0) {
                                    list_message.smoothScrollToPosition((int) count - 1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                            rootRef.child("groupChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long count = dataSnapshot.getChildrenCount();
                                    if (count != 0) {
                                        list_message.smoothScrollToPosition((int) count - 1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    }
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeChat.equals("Friend Chat")) {
                    rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getChildrenCount();
                            if (count != 0) {
                                list_message.smoothScrollToPosition((int) count - 1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    rootRef.child("groupChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getChildrenCount();
                            if (count != 0) {
                                list_message.smoothScrollToPosition((int) count - 1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        btnSendMessage = (ImageView) findViewById(R.id.btn_send_message);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageText = editText.getText().toString();
                if(messageText.trim().length() > 0)
                    if (typeChat.equals("Friend Chat")) {
                        String message_sender_ref = "friendChat/" + messageSenderId + "/" + messageReceiverId;
                        String messgae_receiver_ref = "friendChat/" + messageReceiverId + "/" + messageSenderId;

                        DatabaseReference user_message_key = rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId).push();
                        String messgae_push_id = user_message_key.getKey();

                        Map messageBody = new HashMap();
                        messageBody.put("message", messageText);
                        messageBody.put("time", ServerValue.TIMESTAMP);
                        messageBody.put("senderId", messageSenderId);

                        Map messageBodyDetail = new HashMap();
                        messageBodyDetail.put(message_sender_ref + "/" + messgae_push_id, messageBody);
                        messageBodyDetail.put(messgae_receiver_ref + "/" + messgae_push_id, messageBody);

                        rootRef.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("Chat_log", databaseError.getMessage().toString());
                                }
                            }
                        });


                        rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long count = dataSnapshot.getChildrenCount();
                                if (count != 0) {
                                    list_message.smoothScrollToPosition((int) count - 1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else {
                        rootRef.child("MemberInGroupID").child(messageReceiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                    String messageMemGroupId = ds.getKey();
                                    String message_sender_ref = "groupChat/" + messageMemGroupId + "/" + messageReceiverId;

                                    DatabaseReference user_message_key = rootRef.child("groupChat").child(messageMemGroupId).child(messageReceiverId).push();
                                    String messgae_push_id = user_message_key.getKey();

                                    Map messageBody = new HashMap();
                                    messageBody.put("message", messageText);
                                    messageBody.put("time", ServerValue.TIMESTAMP);
                                    messageBody.put("senderId", messageSenderId);

                                    Map messageBodyDetail = new HashMap();
                                    messageBodyDetail.put(message_sender_ref + "/" + messgae_push_id, messageBody);

                                    rootRef.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Log.d("Chat_log", databaseError.getMessage().toString());
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        rootRef.child("groupChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long count = dataSnapshot.getChildrenCount();
                                if (count != 0) {
                                    list_message.smoothScrollToPosition((int) count - 1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                  }

                editText.setText("");
            }
        });

        btnSetApp = (Button) findViewById(R.id.btn_set_appointment);
        btnSetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        goBack = (Button) findViewById(R.id.button_BackChat);
        goBack.setText(messageReceiverName);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ) {
            address = data.getStringExtra("addressName");
            longitude = data.getDoubleExtra("mLongitude", 0);
            latitude = data.getDoubleExtra("mLatitude", 0);

            txtNameLocation.setText(address);
        }
    }

    private void fetchMessage() {
        if (typeChat.equals("Friend Chat")) {
            rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messageList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            rootRef.child("friendChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    if (count != 0) {
                        list_message.smoothScrollToPosition((int) count - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            rootRef.child("groupChat").child(messageSenderId).child(messageReceiverId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messageList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            rootRef.child("groupChat").child(messageSenderId).child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    if (count != 0) {
                        list_message.smoothScrollToPosition((int) count - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

   private void fetchAppointment() {
        if (typeChat.equals("Friend Chat")) {
            rootRef.child("meeting").child(messageSenderId + messageReceiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (ds.getKey().equals("nameOfMeeting")) {
                            if (!ds.getValue().equals(""))
                                txt_name.setText(ds.getValue().toString());
                        }

                        if (ds.getKey().equals("date")) {
                            if (!ds.getValue().equals(""))
                                txt_date.setText(ds.getValue().toString());
                        }

                        if (ds.getKey().equals("time")) {
                            if (!ds.getValue().equals(""))
                                txt_time.setText(ds.getValue().toString());
                        }
                    }

                    /*Conversation conver = dataSnapshot.getValue(Conversation.class);

                    if (!conver.getNameOfMeeting().equals("")) {
                        txt_name.setText(conver.getNameOfMeeting());
                    }

                    if (!conver.getDate().equals("")) {
                        txt_date.setText(conver.getDate());
                    }

                    if (!conver.getTime().equals("")) {
                        txt_time.setText(conver.getTime());
                    }*/

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            rootRef.child("meeting").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (ds.getKey().equals("nameOfMeeting")) {
                            if (!ds.getValue().equals(""))
                                txt_name.setText(ds.getValue().toString());
                        }

                        if (ds.getKey().equals("date")) {
                            if (!ds.getValue().equals(""))
                                txt_date.setText(ds.getValue().toString());
                        }

                        if (ds.getKey().equals("time")) {
                            if (!ds.getValue().equals(""))
                                txt_time.setText(ds.getValue().toString());
                        }
                    }

                    /*Conversation conver = dataSnapshot.getValue(Conversation.class);

                    if (!conver.getNameOfMeeting().equals("")) {
                        txt_name.setText(conver.getNameOfMeeting());
                    }

                    if (!conver.getDate().equals("")) {
                        txt_date.setText(conver.getDate());
                    }

                    if (!conver.getTime().equals("")) {
                        txt_time.setText(conver.getTime());
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
   }

   private void showPopup() {
        Button btnCancel;
        Button btnConfirm;

        myDialog = new Dialog(ChatActivity.this);
        myDialog.setContentView(R.layout.activity_set_appointment);

        txtNameLocation = (TextView) myDialog.findViewById(R.id.nameLocation);

        btnCancel = (Button) myDialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnDate = (Button) myDialog.findViewById(R.id.btn_date);
        txtDate = (TextView) myDialog.findViewById(R.id.txt_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ChatActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        date = dayOfMonth + "-" + (month + 1) + "-" + year;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnTime = (Button) myDialog.findViewById(R.id.btn_time);
        txtTime = (TextView) myDialog.findViewById(R.id.txt_time);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ChatActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);
                        time = hourOfDay + ":" + minute;
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btnSetPos = myDialog.findViewById(R.id.btn_position);
        btnSetPos.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MapsActivity.class);
                if (typeChat.equals("Friend Chat")) {
                    Log.d(TAG,"ID SEND :"+messageSenderId +messageReceiverId);
                    intent.putExtra("mConverId", messageSenderId +messageReceiverId);
//                    intent.putExtra("mFriendId", messageReceiverId);
                }
                else {
                    Log.d(TAG,"ID SEND :"+messageReceiverId);
                    intent.putExtra("mConverId", messageReceiverId);
                }
                ChatActivity.this.startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });

        edtName = (EditText) myDialog.findViewById(R.id.name_of_appointment);

        btnConfirm = (Button) myDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtName.getText().toString();
                if (!name.equals("")) {
                    if (typeChat.equals("Friend Chat")) {
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("nameOfMeeting").setValue(name);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("nameOfMeeting").setValue(name);
                    }
                    else {
                        rootRef.child("meeting").child(messageReceiverId).child("nameOfMeeting").setValue(name);
                    }
                    txt_name.setText(name);
                }
                if (date != null) {
                    if (typeChat.equals("Friend Chat")) {
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("date").setValue(date);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("date").setValue(date);
                    }
                    else {
                        rootRef.child("meeting").child(messageReceiverId).child("date").setValue(date);
                    }
                    txt_date.setText(date);
                }
                if (time != null) {
                    if (typeChat.equals("Friend Chat")) {
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("time").setValue(time);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("time").setValue(time);
                    }
                    else {
                        rootRef.child("meeting").child(messageReceiverId).child("time").setValue(time);
                    }
                    txt_time.setText(time);
                }
                if (address != null && longitude != null && latitude != null) {
                    if (typeChat.equals("Friend Chat")) {
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("latitude").setValue(latitude);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("latitude").setValue(latitude);
                        //rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("address").setValue(address);
                        //rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("address").setValue(address);
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("latLng").child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageSenderId + messageReceiverId).child("latLng").child("latitude").setValue(latitude);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("latLng").child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageReceiverId + messageSenderId).child("latLng").child("latitude").setValue(latitude);
                    } else {
                        rootRef.child("meeting").child(messageReceiverId).child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageReceiverId).child("latitude").setValue(latitude);
                        rootRef.child("meeting").child(messageReceiverId).child("latLng").child("longitude").setValue(longitude);
                        rootRef.child("meeting").child(messageReceiverId).child("latLng").child("latitude").setValue(latitude);
                        //rootRef.child("meeting").child(messageReceiverId).child("address").setValue(address);
                    }
                }
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}
