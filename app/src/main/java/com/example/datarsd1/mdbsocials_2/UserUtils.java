package com.example.datarsd1.mdbsocials_2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by datarsd1 on 3/1/17.
 */

public class UserUtils
{

    private FirebaseAuth mAuth;

    public UserUtils(FirebaseAuth mAuth)
    {
        this.mAuth = mAuth;
    }

    public void signIn(final Context context, String userEmail, String userPassword)
    {
        if (userPassword == null || userEmail == null || userEmail.length() == 0 || userPassword.length() == 0)
        {
            Toast.makeText(context, "You didn't Enter Anything", Toast.LENGTH_SHORT).show();
        }else
        {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Sign in Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void signUp(final Context context, final DatabaseReference mDatabase, final Uri profileImage, final String userPassword, final String userEmail, final String userName)
    {
        if (profileImage == null || userPassword == null || userEmail == null || userEmail.length() == 0 || userPassword.length() == 0 || userName.length()==0)
        {
            Toast.makeText(context, "You didn't Enter Anything", Toast.LENGTH_SHORT).show();
        }else
        {
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        FirebaseUtils.writeUserToDB(mAuth, mDatabase, profileImage, userName, userEmail, userPassword);
                    }else
                    {
                        Toast.makeText(context, "Sign Up Failed. Make sure you entered a valid email and a STRONG password (uppercase, symbols, 10+ chars)",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void signOut()
    {
        mAuth.signOut();
    }

}
