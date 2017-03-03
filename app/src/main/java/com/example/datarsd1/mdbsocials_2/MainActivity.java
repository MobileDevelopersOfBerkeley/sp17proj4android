package com.example.datarsd1.mdbsocials_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main Activity of MDBSocials app
 * inflates main activity layout, sets listeners and enables Authorization features
 * Main Sign in Page/ Sign up page. Not seen if user is already logged in
 * sends intents to: UserFeedActivity and SignupActivity
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button loginButton, signUpButton;
    EditText usernameInput, passwordInput;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    /**
     * onCreate method for main activity of MDBSocials app
     * inflates main activity layout, sets listeners and enables Authorization features
     * sends intents to: UserFeedActivity and SignupActivity
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = (EditText)findViewById(R.id.emailInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        loginButton = (Button)findViewById(R.id.loginButton);
        signUpButton = (Button)findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, UserFeedActivity.class));
                }else
                {

                }
            }
        };
    }

    /**
     * onClick method for main activity of MDBSocials app
     * handles click events for sign up and sign in buttons
     * sends intents to: SingupActivity and UserFeedActivity
     */
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.loginButton:
                signIn();
                break;
            case R.id.signUpButton:
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
                break;
        }
    }

    /**
     * signIn method for main activity of MDBSocials app
     * calls on UserUtils helper class to handle user authorization
     * sends intents to: UserFeedActivity and SignupActivity
     */
    public void signIn()
    {
        UserUtils authManager = new UserUtils(mAuth);
        authManager.signIn(this, usernameInput.getText().toString(), passwordInput.getText().toString());
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