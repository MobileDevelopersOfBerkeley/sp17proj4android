package com.example.radhika.mdbsocials;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialFeedActivity extends AppCompatActivity implements View.OnClickListener {
    SocialFeedAdapter adapter;
    DatabaseReference db;
    FloatingActionButton addSocialButton;
    Button signOut;
    static List<String> keys;
    RecyclerView recyclerView;
    ArrayList<Social> socials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);

        addSocialButton = (FloatingActionButton) findViewById(R.id.addSocialButton);
        addSocialButton.setOnClickListener(this);
        signOut = (Button) (findViewById(R.id.signOutButton));
        signOut.setOnClickListener(this);
        socials = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initDB();

//        recyclerView.setAdapter(adapter);
    }

    private void initDB () {
        db = FirebaseDatabase.getInstance().getReference();
        Utils.getUser(db, FirebaseAuth.getInstance().getCurrentUser());
        adapter = new SocialFeedAdapter(getApplicationContext(), socials);
        db.child("Socials").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                socials = new ArrayList<Social>();
                Map<Social, String> map = new HashMap<>();

                for (DataSnapshot p : snapshot.getChildren()) {
                    Social s = p.getValue(Social.class);
                    map.put(s, p.getKey());
                    if (!socials.contains(s))
                        socials.add(s);
                }

                Collections.sort(socials);

                keys = new ArrayList<>();
                for (Social s : socials)
                    keys.add(map.get(s));

                adapter.socials = socials;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.addSocialButton)
            startActivity(new Intent(getApplicationContext(), NewSocialActivity.class));
        else if (view.getId() == R.id.signOutButton) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        }
    }
}
