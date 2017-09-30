package com.example.joey.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        ((Button) findViewById(R.id.createAccountButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });

    }

    private void attemptSignup(){
        String email = ((EditText) findViewById(R.id.signUpEmailText)).getText().toString();
        String pass1 = ((EditText) findViewById(R.id.signUpPasswordText)).getText().toString();
        String pass2 = ((EditText) findViewById(R.id.confirmPasswordText)).getText().toString();
        if(email.equals("")){
            Toast.makeText(SignUpActivity.this, "Enter Valid email!", Toast.LENGTH_SHORT).show();
        }
        else if(pass1.equals("") || pass2.equals("")){
            Toast.makeText(SignUpActivity.this, "Complete both password banks!", Toast.LENGTH_SHORT).show();
        }
        else if(!pass1.equals(pass2)){
            Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Signup failed!", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(SignUpActivity.this, FeedActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.signOut();
        ((EditText) findViewById(R.id.signUpEmailText)).setText("");
        ((EditText) findViewById(R.id.signUpPasswordText)).setText("");
        ((EditText) findViewById(R.id.confirmPasswordText)).setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
