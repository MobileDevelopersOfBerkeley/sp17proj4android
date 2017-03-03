package io.github.sumukhshiv.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonSignIn;
    private TextView textViewRegister;
    private FirebaseAuth firebaseAuth;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Retrieve all views from XML
        editTextEmailLogin = (EditText) findViewById(R.id.editTextEmail);
        editTextPasswordLogin = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = firebaseAuth.getInstance();

        //Adding the click listeners
        buttonSignIn.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

    }

    /**
     * Modular function that Logs users into the app. Takes in no arguments and returns nothing.
     * Series of actions completed to login user using FirebaseAuth call.
     */
    public void userLogin() {
        final String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Logging in User...");
        progressDialog.show();

        FirebaseUtils.login(email, password, progressDialog, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignin:
                userLogin();
                break;
            case R.id.textViewRegister:
                Intent toHome = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(toHome);
        }
    }

}
