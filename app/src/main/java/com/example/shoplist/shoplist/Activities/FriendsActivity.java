package com.example.shoplist.shoplist.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shoplist.shoplist.Models.Friend;
import com.example.shoplist.shoplist.Models.FriendDelete;
import com.example.shoplist.shoplist.Models.FriendRequest;
import com.example.shoplist.shoplist.R;
import com.example.shoplist.shoplist.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private String mUserId;
    public String authorValue;
    public List<String> userList = new ArrayList<String>();
    public List<String> list = new ArrayList<String>();
    public List<String> friendList = new ArrayList<String>();
    public List<String> friends = new ArrayList<String>();
    public List<FriendRequest> friendRequestList = new ArrayList<FriendRequest>();
    public List<FriendRequest> friendRequests = new ArrayList<FriendRequest>();
    public String nickname;
    public FriendRequest friendRequest;
    public FriendDelete friendDelete;

    protected Button addButton;
    protected Button deleteButton;
    protected EditText friendName;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        mDatabaseUsers = mDatabase.child("users");
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        userList = userListFiller();
        friendList = yourFriends();
        deleteButton = (Button) findViewById(R.id.DeleteButton);
        addButton = (Button) findViewById(R.id.AddButton);
        friendName = (EditText) findViewById(R.id.FriendName);

        final ListView listView = (ListView) findViewById(R.id.friendslist);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        mDatabase.child("users").child(mUserId).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!(dataSnapshot.child("isme").getValue().toString() == null)) {
                    if (!dataSnapshot.child("isme").getValue().equals("true")) {
                        adapter.add((String) dataSnapshot.child("nick").getValue());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("isme").getValue().toString() == null)) {
                    if (!dataSnapshot.child("isme").getValue().equals("true")) {
                        adapter.remove((String) dataSnapshot.child("nick").getValue());
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendsActivity.this, friendlistActivity.class);
                Bundle b = new Bundle();
                b.putString("usernameclicked", (String) listView.getItemAtPosition(position)); //Your id
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendsmail = friendName.getText().toString();
                Friend friend = new Friend("false", friendsmail, "false");
                if (!friendsmail.isEmpty() && userList.contains(friendsmail) && !friendList.get(0).equals(friendsmail)) {
                    friendRequest = new FriendRequest(mFirebaseUser.getDisplayName(), friendsmail, "False");
                    mDatabase.child("add").push().setValue(friendRequest);
                    Toast.makeText(getApplicationContext(), getString(R.string.friend_send), Toast.LENGTH_SHORT).show();
                    //mDatabase.child("users").child(mUserId).child("friends").push().setValue(friend);
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.friend_no_send), Toast.LENGTH_SHORT).show();
                }
                friendName.setText("");
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String friendsmail = friendName.getText().toString();
                if (!friendsmail.isEmpty()) {
                    mDatabase.child("users").child(mUserId).child("friends").addListenerForSingleValueEvent(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                                        //postSnapshot.getValue();
                                        Log.d("testujemy1", postSnapshot.getValue().toString());
                                        if(postSnapshot.child("nick").getValue().toString().equals(friendsmail) && !friendList.get(0).equals(friendsmail)){
                                            postSnapshot.getRef().setValue(null);
                                            friendDelete = new FriendDelete(mFirebaseUser.getDisplayName(), friendsmail);
                                            mDatabase.child("remove").push().setValue(friendDelete);
                                        }
                                        else{
                                            Util.ToastMaker(getApplicationContext(), getString(R.string.deleteerror));
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError dataSnapshot) {
                                    Util.ToastMaker(getApplicationContext(), getString(R.string.deleteerror));

                                }
                            });
                }
                else {
                    Util.ToastMaker(getApplicationContext(), getString(R.string.uncorrecttext));
                }
                friendName.setText("");
            }
        });

    }
    public List<String> userListFiller(){

        Log.d("userlist", "nick");
        mDatabase.child("userlist").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("userlist", dataSnapshot.child("nickname").getValue().toString());
                list.add(dataSnapshot.child("nickname").getValue().toString());


            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //adapter.remove((String) dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return list;
    }

    public List<String> yourFriends(){

        mDatabase.child("users").child(mUserId).child("friends").addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        friends.add(dataSnapshot.child("nick").getValue().toString());
                        friends = friends.stream().distinct().collect(Collectors.toList());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return friends;
    }



}
