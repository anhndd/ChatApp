package com.example.group_32.chatloca.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.SearchView;
import com.example.group_32.chatloca.Conversation;
import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.User;
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
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.group_32.chatloca.dialogs.CreateGroupDialog.viewCreateGroup;

public class SearchMemberDialog extends AppCompatDialogFragment {

    private Button btnOK, btnCancel;
    private SearchView searchvSearchMember;
    private LinearLayout linearlayoutMember;
    private final int BLANK_SIZE = 3;
    private int id = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_search_member, null);
        mapping(view);
        id = 0;
        builder.setView(view);

        searchvSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchvSearchMember.setIconified(false);
            }
        });
        // Search friend's list
        searchvSearchMember.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchvSearchMember.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    callSearch(newText);
                } else {
                    for (int i = 0; i < id; i++) {
                        RelativeLayout relativeLayout = view.findViewById(R.id.RelativeLMemberGroup + i);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                for (int i = 0; i < id; i++) {
                    RelativeLayout relativeLayout = view.findViewById(R.id.RelativeLMemberGroup + i);
                    TextView textviewUserName = view.findViewById(R.id.TextviewMember + i);
                    if (!relativeLayout.getTag().toString().contains(query) && !textviewUserName.getText().toString().contains(query))
                        relativeLayout.setVisibility(View.GONE);
                    else relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                TextView tvMembers = viewCreateGroup.findViewById(R.id.TextView_Members);
                tvMembers.setText("");
                ArrayList<Integer> ListLengthWordtoSpan = new ArrayList<Integer>();
                String allString = "";
                for (int i = 0; i < id; i++) {
                    RelativeLayout relativeLayout = linearlayoutMember.findViewById(R.id.RelativeLMemberGroup + i);
                    final CheckBox cbChooseMember = linearlayoutMember.findViewById(R.id.addMemberButton + i);
                    if (cbChooseMember.isChecked()) {
                        ListLengthWordtoSpan.add(relativeLayout.getTag().toString().length()+ BLANK_SIZE);
                        allString += relativeLayout.getTag().toString() + "   ";
                    }
                }
                Spannable WordtoSpan = new SpannableString(allString);
                int cusor = 0;
                for(int i =0 ; i<ListLengthWordtoSpan.size();i++) {
                    cusor += ListLengthWordtoSpan.get(i);
                    WordtoSpan.setSpan(new BackgroundColorSpan(Color.GRAY),
                            cusor-ListLengthWordtoSpan.get(i), cusor-BLANK_SIZE, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvMembers.setText(WordtoSpan);

                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        LoadFriend(view);

        return builder.create();
    }

    void createMemberView(String userName, String ID_User, String NameOfUser) {
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setTag(userName);
        relativeLayout.setId(R.id.RelativeLMemberGroup + id);

        // khởi tạo parameter cho relativeLayout (user)
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textparams.topMargin = 20;
        textparams.rightMargin = 20;
        textparams.leftMargin = 20;
        // tạo ra TextView
        TextView textviewUserName = new TextView(getActivity());
        textviewUserName.setId(R.id.TextviewMember + id);
        textviewUserName.setBackground(getActivity().getDrawable(R.drawable.lview_v2));
        textviewUserName.setHeight(100);
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

        CircleImageView image = new CircleImageView(getActivity());
        setAvatar(ID_User,image);
        relativeLayout.addView(image, imageparams);
        image.setTag(textviewUserName.getText() + "Avatar");
        image.setBorderWidth(3);
        image.setBorderColor(Color.parseColor("#FFFFFFFF"));
        image.getLayoutParams().height=100;
        image.getLayoutParams().width=100;
        // Cho ImageView vào parameter vừa tạo ra

        // khởi tạo parameter cho relativeLayout (add)
        RelativeLayout.LayoutParams addparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addparams.topMargin = 20;
        addparams.rightMargin = 30;
        addparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // tạo ra TextView
        CheckBox cbxAddMember = new CheckBox(getActivity());
        cbxAddMember.setId(R.id.addMemberButton + id);
        cbxAddMember.setGravity(Gravity.CENTER);
        cbxAddMember.setPaddingRelative(10, 10, 0, 0);
        cbxAddMember.setTag(textviewUserName.getText() + "AddMember");
        // Cho ImageView vào parameter vừa tạo ra
        relativeLayout.addView(cbxAddMember, addparams);

        // thêm thanh chat mới vào message
        linearlayoutMember.addView(relativeLayout);
        id++;
    }

    private void mapping(View view) {
        btnOK = view.findViewById(R.id.button_OKsearchMember);
        btnCancel = view.findViewById(R.id.button_CancelsearchMember);
        linearlayoutMember = view.findViewById(R.id.LinearlayoutsearchMember_Message);
        searchvSearchMember = view.findViewById(R.id.searchMember_Message);
    }

    private void setAvatar(final String UserID, final CircleImageView image) {
        if(checkImageExist(UserID)){
            Bitmap bitmap = loadImageFromStorage(UserID);
            image.setImageBitmap(bitmap);
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
                    .child(AVATAR + "/" + UserID)
                    .getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            image.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                            saveToInternalStorage(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), UserID);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                            image.setImageResource(R.drawable.ic_user);
                        }
                    });
        }
    }
    private void saveToInternalStorage(Bitmap bitmap, String userId){
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());

        // path to /data/data/your app/app_data/avatar
        File directory = contextWrapper.getDir("avatar", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory,userId + ".jpg");
        return mypath.exists();
    }
    private Bitmap loadImageFromStorage(String userId) {
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());

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
    }
    public void LoadFriend(final View view) {
        // LoadFriend create friend chat
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String Username = mAuth.getCurrentUser().getDisplayName();
        String ID_User = mAuth.getCurrentUser().getUid();

        TextView tvMembers = viewCreateGroup.findViewById(R.id.TextView_Members);
        final String [] Member_username = tvMembers.getText().toString().split(" ");

        if (Username == null) return;
        DatabaseReference ref = database.getReference("Chat").child(ID_User);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    final Conversation friend = db.getValue(Conversation.class);
                    final String NameOfUser_Friend = friend.getConversationName();
                    if (friend.getType().equals("Friend Chat")) {
                        DatabaseReference refUser = database.getReference("User").child(db.getKey());
                        final String ID_User = db.getKey();
                        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User FriendInfo = dataSnapshot.getValue(User.class);
                                createMemberView(NameOfUser_Friend, ID_User, FriendInfo.getNameOfUser());

                                RelativeLayout friend_username = view.findViewById(R.id.RelativeLMemberGroup + id - 1);
                                CheckBox cbxAddMember = view.findViewById(R.id.addMemberButton + id - 1);
                                for (int i = 0; i < Member_username.length; i++) {
                                    if (Member_username[i].equals(friend_username.getTag().toString())) {
                                        cbxAddMember.setChecked(true);
                                        break;
                                    }
                                }
                            }@Override public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}