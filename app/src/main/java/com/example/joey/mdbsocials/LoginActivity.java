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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.signUpButton).setOnClickListener(this);


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

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onClick(View view) {
        Log.d("THIS", "SHIT WAS CALLED");
        switch(view.getId()){
            case R.id.loginButton:
                String email = ((EditText) findViewById(R.id.emailText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();
                FirebaseUtils.attemptLogin(this, email, password);
                break;
            case R.id.signUpButton:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            default:
                break;
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
