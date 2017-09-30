package com.example.joey.mdbsocials;

import android.content.Intent;
import android.net.Uri;
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

public class CreateSocial extends AppCompatActivity {

    private Uri file;
    private Button createSocial;
    private Button addImage;
    private EditText socialName;
    private EditText socialDate;
    private EditText socialDescription;
    private ImageView previewImage;

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_social);

        createSocial = (Button) findViewById(R.id.createSocial);
        addImage = (Button) findViewById(R.id.addImage);
        socialName = (EditText) findViewById(R.id.socialName);
        socialDate = (EditText) findViewById(R.id.profileDate);
        socialDescription = (EditText) findViewById(R.id.socalDescription);
        previewImage = (ImageView) findViewById(R.id.imagePreview);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        createSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check to see if image exists
                if(file == null){
                    Toast.makeText(CreateSocial.this, "no image selected!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //First generate a key for the image
                final String key = ref.push().getKey();
                StorageReference imageRef = storageReference.child(key + ".png");
                imageRef.putFile(file).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateSocial.this, "no image selected!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name = socialName.getText().toString();
                        if(name.equals("")){
                            name = "No name";
                        }
                        String date = socialDate.getText().toString();
                        if(date.equals("")){
                            date = "N/A";
                        }
                        String description = socialDescription.getText().toString();
                        if(description.equals("")){
                            description = "No description";
                        }
                        String owner = user.getEmail();
                        //add to firebase
                        ref.child(key).child("interested").setValue(0);
                        ref.child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
                        ref.child(key).child("name").setValue(name);
                        ref.child(key).child("date").setValue(date);
                        ref.child(key).child("description").setValue(description);
                        ref.child(key).child("owner").setValue(owner);

                        Intent returnIntent = new Intent(CreateSocial.this, FeedActivity.class);
                        startActivity(returnIntent);

                    }
                });


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 1){
            file = data.getData();
            previewImage.setImageURI(file);


        }
    }

}
