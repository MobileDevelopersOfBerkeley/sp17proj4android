package com.example.joey.mdbsocials;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FeedActivity extends AppCompatActivity {

    ArrayList<Social> socials = new ArrayList<>();
    final SocialAdapter adapter = new SocialAdapter(FeedActivity.this, socials);
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RecyclerView activityList = (RecyclerView) findViewById(R.id.activityList);
        activityList.setLayoutManager(new LinearLayoutManager(this));

        activityList.setAdapter(adapter);

        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Social> newSocials = new ArrayList<>();
                for (DataSnapshot socialObject : dataSnapshot.getChildren()){
                    Log.d("WTF", socialObject.child("interested").getValue().getClass().toString());
                    Log.d("WTF", socialObject.child("interested").getValue().toString());

                    Social social = new Social(socialObject.getKey(),
                            socialObject.child("owner").getValue(String.class),
                            socialObject.child("name").getValue(String.class),
                            socialObject.child("date").getValue(String.class),
                            socialObject.child("description").getValue(String.class),
                            socialObject.child("interested").getValue(Integer.class)
                            );
                    newSocials.add(social);
                }
                //CODE DOES ORDER BY TIMESTAMP, but in descending order, reverse to chagne to ascending.
                Collections.reverse(newSocials);
                socials.clear();
                socials.addAll(newSocials);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FeedActivity.this,"Oh no! Database error!", Toast.LENGTH_SHORT);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newSocialFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateSocial.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}