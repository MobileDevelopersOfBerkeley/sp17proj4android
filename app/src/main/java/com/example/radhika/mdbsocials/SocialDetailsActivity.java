package com.example.radhika.mdbsocials;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SocialDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    String key;
    private TextView socialName, dateText, descriptionText;
    private ImageView eventImage;
    private int numInterested;
    Button interestedButton;
    ToggleButton interestedToggleButton;
    DatabaseReference db;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_details);
        init();

    }
    public void init () {
        socialName = (TextView) findViewById(R.id.socialTitleView);
        dateText = (TextView) findViewById(R.id.dateTextView);
        eventImage = (ImageView) findViewById(R.id.eventImageView);
        descriptionText = (TextView) findViewById(R.id.descriptionTextView);
        interestedButton = (Button) findViewById(R.id.interestedButton);
        interestedToggleButton = (ToggleButton) findViewById(R.id.interestedToggleButton);
        interestedButton.setOnClickListener(this);
        interestedToggleButton.setOnClickListener(this);

        key = getIntent().getExtras().getString("Social Key");
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db.child("Socials").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUp(dataSnapshot.getValue(Social.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void onClick (View v){
        switch(v.getId()) {
            case R.id.interestedToggleButton:
                Utils.update(db.child("Socials").child(key), Utils.user);
                break;
            case R.id.interestedButton:
                if (numInterested > 0 ) {
                    Intent i = new Intent(SocialDetailsActivity.this, InterestedFeedActivity.class);
                    i.putExtra("Social Key", key);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, no one is interested in this event", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    public void setUp(final Social s) {
        BitmapAsyncTask b = new BitmapAsyncTask(this, getApplicationContext());
        b.execute(s.getImageUrl());
//        Glide.with(getApplicationContext())
//                .load(s.getImageUrl())
//                .into(eventImage);
        socialName.setText(s.getName());
        dateText.setText(s.getDate());
        descriptionText.setText(s.getDescription());
        interestedButton.setText("" + s.getNumInterested() + " Interested");
        numInterested = s.getNumInterested();

    }
    class BitmapAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        private ProgressDialog dialog;
        public BitmapAsyncTask(Activity act, Context context) {
            super();
            dialog = new ProgressDialog(act);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(true);
            dialog.setMessage("Loading...");
            dialog.setProgress(0);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                eventImage.setImageBitmap(bitmap);
                super.onPostExecute(bitmap);
            }
            dialog.dismiss();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.show();
            dialog.setProgress(values[0]);
        }
    }
}
