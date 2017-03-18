package com.example.radhika.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * TO DO :
 * 1. add profile image
 * 2. add take picture function
 * 3. fix recycler view problem - when you cmark interested, adds new row - click on that row and CRASH
 * 4. improve UI
 * 5. Perform checks for all input taken!!!!!!! especially in signup
 *
 */

public class InterestedFeedActivity extends AppCompatActivity {
    InterestedFeedAdapter adapter;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_feed);
        init();
    }
    public void init () {
        RecyclerView recyclerView = (RecyclerView) (findViewById(R.id.rv));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final List<User> interested = new ArrayList<>();

        // Get the ID of the event from the Intent used to start the InterestedActivity
        String key = getIntent().getStringExtra("Social Key");

        db = FirebaseDatabase.getInstance().getReference();
        db = db.child("Socials").child(key);
        adapter = new InterestedFeedAdapter(getApplicationContext(), interested);
        recyclerView.setAdapter(adapter);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Social s = snapshot.getValue(Social.class);
                adapter.interested = s.getInterested();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

    }
}
