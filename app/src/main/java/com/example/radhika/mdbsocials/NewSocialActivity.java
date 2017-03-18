package com.example.radhika.mdbsocials;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Things to do
 * add a date picker - check for valid dates
 * USE TIMESTAMP
  */
public class NewSocialActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText eventName, eventDescription, eventDate;
    private Button doneButton;
    private ImageView eventPicture;
    private Bitmap eventPictureBitmap;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private Uri uri;
    private boolean uploadedPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);
        init();
    }

    private void init () {
        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance(); // need to access curr user!
        storage = FirebaseStorage.getInstance();
        uploadedPicture = false;
        eventName = (EditText) (findViewById(R.id.eventName));
        eventDescription = (EditText) (findViewById(R.id.eventDescription));
        eventDate = (EditText) (findViewById(R.id.eventDate));
        doneButton = (Button) (findViewById(R.id.doneButton));
        eventPicture = (ImageView) (findViewById(R.id.eventPicture));
        eventPicture.setOnClickListener(this);
        doneButton.setOnClickListener(this);
    }

    private void getImage() {
        // http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }
    private void addNewSocial() {
        if (mAuth.getCurrentUser() == null)
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        final String eName = eventName.getText().toString();
        final String eDesc = eventDescription.getText().toString();
        final String eDate = eventDate.getText().toString();
        if (eName.length() == 0 || eDesc.length() == 0
                || eDate.length() == 0 || !uploadedPicture) {
            Toast.makeText(getApplicationContext(), "Not enough information to create a social", Toast.LENGTH_LONG).show();
            return;
        }
        final String email = mAuth.getCurrentUser().getEmail();
        final String key = db.child("Socials").push().getKey();
        // refer to youtube SimplifiedCoding tutorial
        StorageReference sref = storage.getReference().child("images/" + key + ".jpg");
        sref.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot Create New Social", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri img = taskSnapshot.getDownloadUrl();
                Social s = new Social (eName, email, 0, img.toString(), System.currentTimeMillis(), eDesc, eDate, new ArrayList<User>());
                db.child("Socials").child(key).setValue(s);
                startActivity(new Intent(NewSocialActivity.this, SocialFeedActivity.class));
            }
        });
    }

    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.eventPicture:
                getImage();
                break;
            case R.id.doneButton:
                addNewSocial();
                break;
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // refer to http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
//        if (requestCode == 1 && resultCode == RESULT_OK &&
//                data != null && data.getData() != null) {
//            uri = data.getData();
//            uploadedPicture = true;
//        }
//    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // refer to http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
        if (requestCode == 1 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            try {
                eventPictureBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                eventPicture.setImageBitmap(eventPictureBitmap);
                uploadedPicture = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
