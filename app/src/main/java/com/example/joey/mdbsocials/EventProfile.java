package com.example.joey.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EventProfile extends AppCompatActivity {

    private int interested;
    private Button interestedButton;
    private ArrayList<String> people;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_profile);

        final String id = getIntent().getStringExtra("id");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials/" + id);
        interested = getIntent().getIntExtra("interested", 0);
        people = new ArrayList<>();
        interestedButton = (Button) findViewById(R.id.profileInterestButton);

        ((TextView) findViewById(R.id.profileTitle)).setText(getIntent().getStringExtra("name"));
        ((TextView) findViewById(R.id.profileDescription)).setText(getIntent().getStringExtra("description"));
        ((TextView) findViewById(R.id.profileDate)).setText(getIntent().getStringExtra("date"));
        ((TextView) findViewById(R.id.profileInterested)).setText(""+interested);

        FirebaseUtils.loadImageFromFirebase(id, (ImageView) findViewById(R.id.profileImage), (ProgressBar) findViewById(R.id.progressBarProfile));

        //use transaction to update the number

        //TODO: Could not determine how to get this listener in FirebaseUtils.class because the onclicklistener can ONLY be set after valueEventListener executed
        ref.child("people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot person : dataSnapshot.getChildren()){
                    people.add(person.getValue(String.class));
                }
                if(people.contains(user.getEmail())){
                    interestedButton.setText("Already Interested!");
                }
                interestedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!people.contains(user.getEmail())){
                            FirebaseUtils.addInterestedTransaction(id);
                            interested++;
                            ((TextView) findViewById(R.id.profileInterested)).setText(""+interested);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventProfile.this,"Oh no! Database error!", Toast.LENGTH_SHORT).show();
            }
        });


    }



}
