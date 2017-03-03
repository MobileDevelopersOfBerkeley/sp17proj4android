package com.example.datarsd1.mdbsocials_2;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Signup Activity of MDBSocials app
 * inflates signup activity layout, sets listeners and enables Authorization features
 * Main Sign up page. Not seen if user is already logged in
 * sends intents to: UserFeedActivity
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addProfilePicButton, addUserButton;
    private EditText usernameInput, passwordInput, nameInput;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Uri profileImage;
    private String profilePicLocation;
    private DatabaseReference mDatabase;

    final int GET_PROFILE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        addProfilePicButton = (Button)findViewById(R.id.addProfilePhotoButton);
        addUserButton = (Button)findViewById(R.id.signUpButton);

        usernameInput = (EditText) findViewById(R.id.signUpEmailInput);
        passwordInput = (EditText) findViewById(R.id.signUpPasswordInput);
        nameInput = (EditText) findViewById(R.id.signUpNameInput);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addProfilePicButton.setOnClickListener(this);
        addUserButton.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(SignupActivity.this, UserFeedActivity.class));
                }else
                {

                }
            }
        };
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addProfilePhotoButton:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_PROFILE_IMAGE);
                break;
            case R.id.signUpButton:
                signUp();
                break;
        }
    }

    public void signUp()
    {
        final String userName, userPassword, userEmail;
        userName = nameInput.getText().toString();
        userEmail = usernameInput.getText().toString();
        userPassword = passwordInput.getText().toString();

        UserUtils signUpUtil = new UserUtils(mAuth);
        signUpUtil.signUp(getApplicationContext(), mDatabase, profileImage, userPassword, userEmail, userName);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && requestCode==1)
        {
            profileImage = imageReturnedIntent.getData();
            ((ImageView)findViewById(R.id.profilePicPreview)).setImageURI(profileImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
