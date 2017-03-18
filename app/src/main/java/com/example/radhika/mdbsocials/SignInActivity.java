package com.example.radhika.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FilterReader;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText email;
    private EditText password;
    private TextView signUpText;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init () {
        email = (EditText) (findViewById(R.id.emailAddressText));
        password = (EditText) (findViewById(R.id.passwordTextView));
        logInButton = ((Button) (findViewById(R.id.signInButton)));
        logInButton.setOnClickListener(this);
        signUpText = ((TextView) (findViewById(R.id.signUpText)));
        signUpText.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(SignInActivity.this, SocialFeedActivity.class));
                }
            }
        };
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(SignInActivity.this, "Sign In Problem", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                if (email.getText().toString().length() > 0 && password.getText().toString().length() > 0) {
                    signIn(email.getText().toString(), password.getText().toString());
                }
                else
                    Toast.makeText(SignInActivity.this, "Sign In Problem", Toast.LENGTH_SHORT).show();
                break;
            case R.id.signUpText:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
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
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}

