package com.example.shoplist.shoplist.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseAdd;
    private DatabaseReference mDatabaseRemove;
    private DatabaseReference mDataFiller;
    private DatabaseReference mDatabaseTemp;
    private String mUserId;
    public List<String> userList = new ArrayList<String>();
    public List<String> list = new ArrayList<String>();
    public String nickname;
    public List<Item> listItem;
    public ListAdapter listAdapter;
    public Item itemtemp;
    private Friend friend;
    Map<String, FriendRequest> mapa = new HashMap<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseRemove = mDatabase;
        mDatabaseAdd = mDatabaseRemove;
        mDataFiller = mDatabaseRemove;
        mDatabaseTemp = mDataFiller;
        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            mUserId = mFirebaseUser.getUid();
            AddRequests();


            //mFirebaseUser.
            // Set up ListView
            getSupportActionBar().setSubtitle(mFirebaseUser.getDisplayName());
            final ListView listView = findViewById(R.id.listView);
            listItem = new ArrayList<Item>();
            listAdapter = new ListAdapter(this, listItem);
            //final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            listView.setAdapter(listAdapter);
            // Add items via the Button and EditText at the bottom of the view.
            final EditText textTitle = (EditText) findViewById(R.id.TitleText);
            final EditText textAmount = (EditText) findViewById(R.id.AmountText);
            final Button button = (Button) findViewById(R.id.addButton);


            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    final String title = textTitle.getText().toString();
                    final String amount = textAmount.getText().toString();
                    if (!title.isEmpty() && !amount.isEmpty()) {
                        Item item = new Item(title, amount, mFirebaseUser.getDisplayName());
                        mDatabase.child("items").push().setValue(item);
                        textTitle.setText("");
                        textAmount.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.uncorrecttext), Toast.LENGTH_SHORT).show();
                    }



                }
            });

            userList = userListFiller();

            //authorValue = getAuthorValue();
            // Use Firebase to populate the list.
            mDatabase.child("items").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    if (userList.contains(dataSnapshot.child("author").getValue()) || dataSnapshot.child("author").getValue().equals(mFirebaseUser.getDisplayName())) {
                        //adapter.add((String) dataSnapshot.child("title").getValue() + "\u2063 " + dataSnapshot.child("amount").getValue() + "\u2063 " + dataSnapshot.child("author").getValue());
                        listItem.add(new Item(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("amount").getValue().toString(), dataSnapshot.child("author").getValue().toString()));
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    itemtemp = new Item(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("amount").getValue().toString(), dataSnapshot.child("author").getValue().toString());
                    listItem.removeIf(new Predicate<Item>() {
                        @Override
                        public boolean test(Item item) {
                            return item.getTitle().equals(itemtemp.getTitle()) && item.getAmount().equals(itemtemp.getAmount()) && item.getAuthor().equals(itemtemp.getAuthor());
                        }
                    });
                    listAdapter.notifyDataSetChanged();
                    //adapter.remove((String) dataSnapshot.child("title").getValue() + "\u2063 " + dataSnapshot.child("amount").getValue() + "\u2063 " + dataSnapshot.child("author").getValue());
                    //adapter.remove((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), getString(R.string.databaseerror), Toast.LENGTH_SHORT).show();
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mDatabase.child("items").orderByChild("title").equalTo(((Item) listView.getItemAtPosition(position)).getTitle())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                        firstChild.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.deleteerror), Toast.LENGTH_SHORT).show();
                                }
                            });
                    return true;
                }
            });
        }
    }


    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }else if (id == R.id.action_requests){
            Intent intent = new Intent(MainActivity.this, RequestActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_friends) {
            Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_refresh) {
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }


    public List<String> userListFiller() {

        mDataFiller.child("users").child(mUserId).child("friends").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.add(dataSnapshot.child("nick").getValue().toString());

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
        DeleteRequests();
        return list;
    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    public void AddRequests(){

        mDatabaseRemove.child("add").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshotAdd, String s) {
                if(dataSnapshotAdd.hasChildren()){
                    if(dataSnapshotAdd.child("SenderRequest").getValue().toString().equals(mFirebaseUser.getDisplayName()) && dataSnapshotAdd.child("StatusRequest").getValue().toString().equals("True")){
                        friend = new Friend("false", dataSnapshotAdd.child("ReceiverRequest").getValue().toString(), "false");
                        mDatabaseRemove.child("users").child(mUserId).child("friends").push().setValue(friend);
                        dataSnapshotAdd.getRef().setValue(null);
                        mDatabaseRemove = FirebaseDatabase.getInstance().getReference();
                        finish();
                        startActivity(getIntent());
                    }
                }

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
    }

    public void DeleteRequests(){

        mDatabaseAdd.child("remove").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshotRemove, String s) {
                if(dataSnapshotRemove.child("ReceiverDelete").getValue().toString().equals(mFirebaseUser.getDisplayName())){
                    mDatabaseAdd.child("users").child(mUserId).child("friends").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(final DataSnapshot dataSnapshot2, String s) {
                            if(dataSnapshotRemove.child("SendDelete").getValue().toString().equals(dataSnapshot2.child("nick").getValue().toString())){

                                dataSnapshot2.getRef().setValue(null);
                                dataSnapshotRemove.getRef().setValue(null);
                                finish();
                                startActivity(getIntent());
                            }
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
                }
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
    }

    class ListAdapter extends BaseAdapter {

        List<Item> arrayList;
        Context context;

        public ListAdapter(Context context, List<Item> arrayList) {
            this.arrayList=arrayList;
            this.context=context;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            CompleteListViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.list_adapter, null);
                viewHolder = new CompleteListViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (CompleteListViewHolder) v.getTag();
            }
            viewHolder.productView.setText(arrayList.get(position).getTitle());
            viewHolder.authorView.setText(arrayList.get(position).getAuthor());
            viewHolder.amountView.setText(arrayList.get(position).getAmount());
            return v;
        }

    }

    public class CompleteListViewHolder {
        public TextView productView;
        public TextView authorView;
        public TextView amountView;
        public CompleteListViewHolder(View base) {
            productView = (TextView) base.findViewById(R.id.productAdapter);
            authorView = (TextView)base.findViewById(R.id.authorAdapter);
            amountView = (TextView)base.findViewById(R.id.amountAdapter);
        }
    }

}
