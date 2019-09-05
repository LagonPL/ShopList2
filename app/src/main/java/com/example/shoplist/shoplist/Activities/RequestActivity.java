package com.example.shoplist.shoplist.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shoplist.shoplist.Models.Friend;
import com.example.shoplist.shoplist.Models.FriendRequest;
import com.example.shoplist.shoplist.Models.Item;
import com.example.shoplist.shoplist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mData;
    private String mUserId;
    private Friend friend;
    private String username;
    String Receiver;
    String Sender;
    Map<String, FriendRequest> mapa = new HashMap<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        mData = mDatabase;

        final ListView listView = (ListView) findViewById(R.id.requestlist);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        mDatabase.child("add").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("ReceiverRequest").getValue().toString().equals(mFirebaseUser.getDisplayName()) && dataSnapshot.child("StatusRequest").getValue().toString().equals("False")) {
                        adapter.add((String) dataSnapshot.child("SenderRequest").getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                        adapter.remove((String) dataSnapshot.child("SenderRequest").getValue());

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
                friend = new Friend("", listView.getItemAtPosition(position).toString(), "false");
                mDatabase.child("users").child(mUserId).child("friends").push().setValue(friend);
               username = listView.getItemAtPosition(position).toString();
                mDatabase.child("add").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.child("ReceiverRequest").getValue().toString().equals(mFirebaseUser.getDisplayName()) && dataSnapshot.child("SenderRequest").getValue().toString().equals(username) && !dataSnapshot.child("StatusRequest").getValue().toString().equals("True")) {
                            adapter.remove((String) dataSnapshot.child("SenderRequest").getValue());
                            Receiver = dataSnapshot.child("ReceiverRequest").getValue().toString();
                            Sender = dataSnapshot.child("SenderRequest").getValue().toString();
                            dataSnapshot.getRef().setValue(null);
                             mData.child("add").push().setValue(new FriendRequest(Sender, Receiver, "True"));


                        }
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
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                mDatabase.child("add").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.child("ReceiverRequest").getValue().toString().equals(mFirebaseUser.getDisplayName()) && dataSnapshot.child("SenderRequest").getValue().toString().equals(listView.getItemAtPosition(position).toString())) {
                            adapter.remove((String) dataSnapshot.child("SenderRequest").getValue());
                            dataSnapshot.getRef().setValue(null);
                            //finish();
                            //startActivity(getIntent());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        adapter.remove((String) dataSnapshot.child("SenderRequest").getValue());

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }
        });
    }
}
