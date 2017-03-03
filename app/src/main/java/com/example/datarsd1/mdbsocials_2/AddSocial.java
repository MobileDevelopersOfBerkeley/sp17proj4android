package com.example.datarsd1.mdbsocials_2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddSocial extends AppCompatActivity implements View.OnClickListener{

    private EditText newTitleText, newDateText, newDescText;
    private FirebaseAuth mAuth;
    private String firebasePathLocation;
    private DatabaseReference mDatabase;
    private ImageView preview;

    private Button addEventButton, uploadImageButton;

    private Uri eventimageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_social);

        newTitleText = (EditText)findViewById(R.id.newEventTitle);
        newDateText = (EditText)findViewById(R.id.newEventDate);
        newDescText = (EditText)findViewById(R.id.newEventDesc);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String id = mDatabase.child("Socials").push().getKey();
        mAuth = FirebaseAuth.getInstance();

        preview = (ImageView)findViewById(R.id.eventImagePreview);

        addEventButton = (Button)findViewById(R.id.newEventCreate);
        uploadImageButton = (Button)findViewById(R.id.newEventImage);
        addEventButton.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.newEventCreate:
                Log.d("new evt clicked", "button onclick working");
                if (eventimageuri==null|| newTitleText.getText()==null || newDateText.getText()==null || newDescText.getText()==null ||
                        newTitleText.getText().toString().length()==0 || newDateText.getText().toString().length()==0 || newDescText.getText().toString().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter All Fields Properly",Toast.LENGTH_LONG).show();
                }else
                {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    final String key = ref.child("Socials").push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-2.appspot.com");
                    StorageReference riversRef = storageRef.child("eventPics/" + key + ".jpg");

                    String name = newTitleText.getText().toString();
                    String date = newDateText.getText().toString();
                    String desc = newDescText.getText().toString();
                    String host = mAuth.getCurrentUser().getEmail();

                    FirebaseUtils.writeEventToDB(mDatabase, riversRef, eventimageuri, key, name, date, desc, host, mAuth);
                    startActivity(new Intent(getApplicationContext(),UserFeedActivity.class));
                }
                break;
            case R.id.newEventImage:
                AlertDialog alertDialog = new AlertDialog.Builder(AddSocial.this).create();
                alertDialog.setTitle("Image Upload Options");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Take a Photo", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "Image");
                        Uri tempuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
                        startActivityForResult(takePictureIntent, 0);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload from Gallery", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);                            }
                });
                alertDialog.show();
                Button glitchy = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                glitchy.setEnabled(false);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && requestCode == 1) {
            eventimageuri = imageReturnedIntent.getData();
            preview.setImageURI(eventimageuri);
        }
    }
}
