package com.example.joey.mdbsocials;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //user is signed in
                    Log.d("FIREBASE", "onAuthStateChanged:signed_in" + user.getUid());
                }else{
                    //user is signed out
                    Log.d("FIREABASE", "onAuthStateChanged:signed_out");
                }
            }
        };

        //maybe put the listener attachment in onCreate not onStart?
        mAuth.addAuthStateListener(mAuthListener);

        ((Button) findViewById(R.id.loginButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


        ((Button) findViewById(R.id.signUpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void attemptLogin() {
        String email = ((EditText) findViewById(R.id.emailText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();
        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("FIREBASE", "signInWithEmail:onComplete:"+task.isSuccessful());

                    if(!task.isSuccessful()){
                        Log.w("FIREBASE", "signInWithEmail:failed", task.getException());
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Login SUCCESS!", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.signOut();
        ((EditText) findViewById(R.id.emailText)).setText("");
        ((EditText) findViewById(R.id.passwordText)).setText("");

    }
}
