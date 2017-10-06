package com.example.joey.mdbsocials;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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

public class CreateSocial extends AppCompatActivity implements View.OnClickListener{

    private Uri file;
    private EditText socialName;
    private EditText socialDate;
    private EditText socialDescription;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_social);

        socialName = (EditText) findViewById(R.id.socialName);
        socialDate = (EditText) findViewById(R.id.profileDate);
        socialDescription = (EditText) findViewById(R.id.socalDescription);
        previewImage = (ImageView) findViewById(R.id.imagePreview);

        findViewById(R.id.addImage).setOnClickListener(this);
        findViewById(R.id.createSocial).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createSocial:
                String name = socialName.getText().toString();
                String date = socialDate.getText().toString();
                String description = socialDescription.getText().toString();
                FirebaseUtils.pushNewSocial(this, name, description, date, file);

                break;
            case R.id.addImage:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                break;
        }
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
