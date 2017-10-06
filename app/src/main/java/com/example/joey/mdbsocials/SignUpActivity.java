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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.createAccountButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createAccountButton:
                String email = ((EditText) findViewById(R.id.signUpEmailText)).getText().toString();
                String pass1 = ((EditText) findViewById(R.id.signUpPasswordText)).getText().toString();
                String pass2 = ((EditText) findViewById(R.id.confirmPasswordText)).getText().toString();
                FirebaseUtils.attemptSignup(this, email, pass1, pass2);
                break;
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
