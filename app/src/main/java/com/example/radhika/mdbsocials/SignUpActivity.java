package com.example.radhika.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button signUpButton;
    private Button photoButton;
    private String imageUrl ;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init () {
        name = (EditText) (findViewById(R.id.nameText));
        email = (EditText) (findViewById(R.id.emailText));
        password = (EditText) (findViewById(R.id.passwordText));
        signUpButton = (Button) (findViewById(R.id.signUpButton));
        signUpButton.setOnClickListener(this);
        photoButton = (Button) (findViewById(R.id.uploadPhoto));
        photoButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    startActivity(new Intent(SignUpActivity.this, SocialFeedActivity.class));
            }
        };

    }

    private void signUp(final String name, final String email, String password) {
        final String finName = name;
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Sign Up Problem", Toast.LENGTH_SHORT).show();
                    } else  {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(finName)
                            //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                        user.updateProfile(profileUpdates);
                        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        StorageReference sref = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid() + ".jpg");
                        sref.putFile(uri).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Cannot Create New Social", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageUrl= (taskSnapshot.getDownloadUrl().toString());
                                final User u = new User (name, email, imageUrl);
                                db.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(u);
                            }
                        });
                    }
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpButton:
                if (name.getText().toString().length() > 0 && email.getText().toString().length() > 0 && password.getText().toString().length() > 0)
                    signUp(name.getText().toString(), email.getText().toString(), password.getText().toString());
                break;
            case R.id.uploadPhoto:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);
//                // refer to youtube SimplifiedCoding tutorial
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // refer to http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
        if (requestCode == 2 && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
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
