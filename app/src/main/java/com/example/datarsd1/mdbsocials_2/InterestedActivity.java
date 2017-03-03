package com.example.datarsd1.mdbsocials_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InterestedActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    RecyclerView interestedPeopleRVDisplay;
    InterestedAdapter interestedPeopleRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        mAuth = FirebaseAuth.getInstance();

        interestedPeopleRVDisplay = (RecyclerView) findViewById(R.id.usersinterestedrecycler);
        interestedPeopleRVDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent intent = getIntent();
        interestedPeopleRVAdapter = new InterestedAdapter(InterestedActivity.this, getApplicationContext(), new ArrayList<User>());

        interestedPeopleRVDisplay.setAdapter(interestedPeopleRVAdapter);
        final String firebasekey = intent.getStringExtra("key");

        final ArrayList<String> interestedPeople = new ArrayList<>();
        final ArrayList<String> interestedUIDs = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials");

        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(firebasekey)) {
                    ArrayList<String> x = (ArrayList) dataSnapshot.child("interested").getValue();
                    ArrayList<String> uids = (ArrayList<String>) dataSnapshot.child("interestedUIDs").getValue();

                    for (int i = 0; i < x.size(); i++)
                    {
                        interestedPeople.add(x.get(i));
                        interestedUIDs.add(uids.get(i));
                    }

                    ArrayList<User> interestedMDBros = new ArrayList<>();


                    for (int i = 0; i < interestedPeople.size(); i++) {
                        interestedMDBros.add(new User("profiles/"+interestedUIDs.get(i)+".jpg",interestedPeople.get(i)));
                    }

                    interestedPeopleRVAdapter.updateList(interestedMDBros);
                    interestedPeopleRVAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenuButton:
                UserUtils fb = new UserUtils(mAuth);
                fb.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
