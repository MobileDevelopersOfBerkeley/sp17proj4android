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

public class FeedActivity extends AppCompatActivity implements View.OnClickListener{

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

        FirebaseUtils.initFeed(FeedActivity.this, adapter);

        findViewById(R.id.newSocialFAB).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newSocialFAB:
                Intent intent = new Intent(getApplicationContext(), CreateSocial.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}