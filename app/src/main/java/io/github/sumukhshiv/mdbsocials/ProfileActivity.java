package io.github.sumukhshiv.mdbsocials;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int REQUEST_CAMERA = 1;
    public static final int GET_FROM_GALLERY = 3;
    private Intent data;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("users");
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText editTextUpdateName;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button buttonSaveProfileUpdate = (Button) findViewById(R.id.buttonSaveProfileUpdate);
        ImageView updateProfilePicture = (ImageView) findViewById(R.id.imageViewUpdateProfilePicture);
        editTextUpdateName = (EditText) findViewById(R.id.editTextUpdateName);

        buttonSaveProfileUpdate.setOnClickListener(this);
        updateProfilePicture.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if((requestCode==GET_FROM_GALLERY || requestCode == REQUEST_CAMERA) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageView) findViewById(R.id.imageViewUpdateProfilePicture)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        this.data = data;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //case to add a profile picture
            case (R.id.imageViewUpdateProfilePicture):
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("Set a Photo");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take a Photo",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Open Camera
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                                }
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Upload from Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //launch gallery
                                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;

            //case to save the profile to firebase
            case (R.id.buttonSaveProfileUpdate):
                //getting the reference to the storage
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com");
                final String imageKey = myRef.child("users").push().getKey();
                StorageReference imageRef = mStorageRef.child(imageKey + ".png");

                //Doing various checks for the different fields.
                //Used if else because the checks are on different variables
                if (editTextUpdateName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                } else if (data == null) {
                    Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Updating your Profile...");
                    progressDialog.show();
                    //adding the image to storage
                    //Intentionally did not use Utils class, Too many dependencies and created more confusion than modularity
                    imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //creating a new Profile object to send data to Firebase Database
                            Profile updatedProfile = new Profile(editTextUpdateName.getText().toString(), firebaseUser.getEmail(), imageKey + ".png" , new ArrayList<String>() );
                            myRef.child(firebaseUser.getUid()).setValue(updatedProfile);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Updated your Profile!", Toast.LENGTH_SHORT).show();

                            //Go straight to Social Feed
                            Intent intent = new Intent(getApplicationContext(), UserArea.class);
                            startActivity(intent);
                        }
                    });
                }
                break;
        }
    }
}
