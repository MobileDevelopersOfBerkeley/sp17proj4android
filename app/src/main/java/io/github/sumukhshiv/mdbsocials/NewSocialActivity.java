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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NewSocialActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int REQUEST_CAMERA = 1;
    public static final int GET_FROM_GALLERY = 3;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    Intent data;
    private StorageReference mStorageRef;

    EditText editTextEventName;
    EditText editTextDate;
    EditText editTextDescription;
    ImageView imageViewUploadPhoto;
    Button buttonPost;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);

        //defining all variables used
        editTextEventName = (EditText) findViewById(R.id.editTextEventName);
        Button buttonUploadPicture = (Button) findViewById(R.id.buttonUploadPicture);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        imageViewUploadPhoto = (ImageView) findViewById(R.id.imageViewUploadPhoto);
        buttonPost = (Button) findViewById(R.id.buttonPost);
        progressDialog = new ProgressDialog(this);

        buttonUploadPicture.setOnClickListener(this);
        buttonPost.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        //Retrieves image onActivityResult and sets it to the imageView
        if((requestCode==GET_FROM_GALLERY || requestCode == REQUEST_CAMERA) && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                ((ImageView) findViewById(R.id.imageViewUploadPhoto)).setImageBitmap(bitmap);
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
            //Adding a photo case
            case (R.id.buttonUploadPicture):
                AlertDialog alertDialog = new AlertDialog.Builder(NewSocialActivity.this).create();
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

            //Posting the Social Firebase and Newsfeed
            case (R.id.buttonPost):

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("/users").child(firebaseUser.getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object user = dataSnapshot.child("email").getValue();

                        if (user == null) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Please Update Profile!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference myRef = database.getReference("/socials");

                            //Used if-else because only 3 options and not all check for same type data as required by switch statement
                            //Check all text is filled out
                            if (editTextEventName.getText().toString().isEmpty() || editTextDate.getText().toString().isEmpty() ||
                                    editTextDescription.getText().toString().isEmpty()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                            }
                            //Check if image is not yet added
                            else if (data == null) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                            }

                            //Else actually post the social with all the above fields
                            else {
                                progressDialog.setMessage("Uploading New Social...");
                                progressDialog.show();

                                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com");
                                final String imageKey = myRef.child("socials").push().getKey();
                                StorageReference imageRef = mStorageRef.child(imageKey + ".png");

                                //Adding the photo to Storage
                                //Intentionally did not add to Utils class, too many dependencies, created more confusion
                                //than modularity
                                imageRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        //image was successfully added to Storage
                                        ArrayList<String> memberArrayList = new ArrayList<String>();
                                        memberArrayList.add(firebaseUser.getUid());

                                        //Create new Social Object to add to Database and save Social
                                        Social socialToPost = new Social(editTextEventName.getText().toString(), editTextDate.getText().toString(), editTextDescription.getText().toString(),
                                                imageKey + ".png", firebaseUser.getEmail(), 1, memberArrayList);
                                        myRef.child(imageKey).setValue(socialToPost);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Posted new Social!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), UserArea.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                break;
        }
    }
}
