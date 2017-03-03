package com.example.datarsd1.mdbsocials_2;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFeedActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    UserFeedAdapter adapter;
    RecyclerView eventsListView;
    DatabaseReference mDatabase;
    FloatingActionButton fab;

    ArrayList<Social> socials = new ArrayList<Social>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        eventsListView = (RecyclerView) findViewById(R.id.eventsFeedRecyclerView);
        eventsListView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserFeedAdapter(UserFeedActivity.this, getApplicationContext(), socials);
        eventsListView.setAdapter(adapter);

        fab = (FloatingActionButton)findViewById(R.id.addEventButton);
        fab.setOnClickListener(this);

        mDatabase.child("Socials").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                socials = new ArrayList<Social>();
                for (DataSnapshot d: dataSnapshot.getChildren())
                {
                    HashMap<String, Object> evt = (HashMap<String, Object>) d.getValue();
                    String id = (String) evt.get("id");
                    String firebasePathLocation = (String) evt.get("firebasepath");
                    String name = (String) evt.get("name");
                    String host = (String) evt.get("host");
                    String date = (String) evt.get("date");
                    String desc = (String) evt.get("desc");
                    String hostuid = (String) evt.get("hostuid");
                    Integer numInterested = ((Long)evt.get("numInterested")).intValue();
                    ArrayList<String> interested = (ArrayList<String>) evt.get("interestedList");
                    ArrayList<String> interestedUIDs = (ArrayList<String>) evt.get("interestedUIDs");
                    if(interested == null)
                    {
                        interested = new ArrayList<String>();
                    }else if(interestedUIDs == null)
                    {
                        interestedUIDs = new ArrayList<String>();
                    }
                    Social social = new Social(name, host, desc, date, firebasePathLocation, id, numInterested, hostuid, interested, interestedUIDs);
                    socials.add(social);
                }
                adapter.setSocialsList(socials);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.addEventButton:
                Intent intent = new Intent(getApplicationContext(),AddSocial.class);
                startActivity(intent);
                break;
        }
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
