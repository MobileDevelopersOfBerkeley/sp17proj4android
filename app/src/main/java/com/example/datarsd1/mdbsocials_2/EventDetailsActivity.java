package com.example.datarsd1.mdbsocials_2;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    TextView name, host, date, desc;
    ImageView wallpaper, hostimg;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;
    Button interestButton;
    FloatingActionButton eventadd;

    ArrayList<String> interestedNames, interestedEmails, interestedIds;

    String user;

    int initialNumInterested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        interestedNames = new ArrayList<>();
        interestedEmails = new ArrayList<>();
        interestedIds = new ArrayList<>();

        wallpaper = (ImageView)findViewById(R.id.profilewallpaper);
        hostimg = (ImageView)findViewById(R.id.profileimage);

        name = (TextView)findViewById(R.id.eventNameDisplay);
        name.setText(getIntent().getExtras().getString("title"));

        host = (TextView)findViewById(R.id.eventHostDisplay);
        host.setText("By: " + getIntent().getExtras().getString("host"));

        date = (TextView)findViewById(R.id.eventDateDisplay);
        date.setText("Date: " + getIntent().getExtras().getString("date"));

        desc = (TextView)findViewById(R.id.eventDescDisplay);
        desc.setText(getIntent().getExtras().getString("desc"));

        initialNumInterested = getIntent().getExtras().getInt("numInterested");

        StorageReference evtImgRef = mStorageRef.child(getIntent().getExtras().getString("firebasePath"));
        evtImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                new DownloadImageTask(EventDetailsActivity.this, getApplicationContext(), wallpaper).execute(uri.toString());
            }
        });

        final StorageReference hostImgRef = mStorageRef.child("profiles/"+getIntent().getExtras().getString("hostuid")+".jpg");
        hostImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                new DownloadImageTask(EventDetailsActivity.this, getApplicationContext(), hostimg).execute(uri.toString());
            }
        });

        interestButton = (Button)findViewById(R.id.eventInterestedButton);
        interestButton.setText("Interested: "+getIntent().getExtras().getInt("numInterested"));
        interestButton.setOnClickListener(this);

        eventadd = (FloatingActionButton) findViewById(R.id.addInterestButton);
        eventadd.setOnClickListener(this);


    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.eventInterestedButton:
                Intent intent = new Intent(getApplicationContext(), InterestedActivity.class);
                if (initialNumInterested == 0)
                {
                    Toast.makeText(getApplicationContext(),"Nobody is interested in this",Toast.LENGTH_LONG).show();

                }else {
                    intent.putExtra("key", getIntent().getExtras().getString("id"));
                    startActivity(intent);
                }
                break;
            case R.id.addInterestButton:
                addInterest();
                eventadd.setEnabled(false);
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials/"+getIntent().getExtras().getString("id"));
                break;
        }
    }

    public void addInterest()
    {
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials/"+getIntent().getExtras().getString("id"));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.child("interested").child("" + dataSnapshot.child("numInterested").getValue(Integer.class)).setValue(email);
                ref.child("interestedUIDs").child("" + dataSnapshot.child("numInterested").getValue(Integer.class)).setValue(mAuth.getCurrentUser().getUid());
                interestButton.setText("Interested: "+((int)(dataSnapshot.child("numInterested").getValue(Integer.class) + 1)));
                ref.child("numInterested").setValue((int)(dataSnapshot.child("numInterested").getValue(Integer.class) + 1));

                initialNumInterested+=1;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

    @Override
    protected void onStart() {
        super.onStart();
        toggleInterestButton();
    }

    protected void onResume()
    {
        super.onResume();
        toggleInterestButton();
    }

    public void toggleInterestButton()
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Socials/"+getIntent().getExtras().getString("id"));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> interestedPeopleIDs = (ArrayList<String>) dataSnapshot.child("interestedUIDs").getValue();
                if (interestedPeopleIDs != null && interestedPeopleIDs.contains(mAuth.getCurrentUser().getUid()))
                {
                    eventadd.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
