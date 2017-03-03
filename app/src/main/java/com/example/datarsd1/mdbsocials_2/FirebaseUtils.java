package com.example.datarsd1.mdbsocials_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adhiraj Datar on 3/2/2017.
 */

public class FirebaseUtils
{

    public static void writeUserToDB(FirebaseAuth mAuth, DatabaseReference mDatabase, Uri profileImage, String name, String email, String password)
    {
        String key = mAuth.getCurrentUser().getUid();
        Map<String, Object> post = new HashMap<>();

        String profilePicLocation = "profiles/" + key + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-2.appspot.com").child(profilePicLocation);
        storageRef.putFile(profileImage);

        post.put("uid", key);
        post.put("name", name);
        post.put("email", email);
        post.put("password",password);

        mDatabase.child("Users").push().setValue(post);
    }

    public static void writeEventToDB(final DatabaseReference mDatabase, StorageReference riversRef, final Uri eventimageuri, final String key, final String name, final String date, final String desc, final String host, final FirebaseAuth mAuth)
    {
        riversRef.putFile(eventimageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ArrayList<String> interested = new ArrayList<String>();
                Integer numInterested = 0;
                Map<String, Object> post = new HashMap<String, Object>();
                post.put("name", name);
                post.put("host", host);
                post.put("date", date);
                post.put("desc", desc);
                post.put("hostuid",mAuth.getCurrentUser().getUid());
                post.put("interestedList", interested);
                post.put("firebasepath", "eventPics/"+key+".jpg");
                post.put("id",key);
                post.put("numInterested", numInterested);
                if (name.length()==0 || host==null || host.length()==0 || date.length()==0 || desc.length()==0 || key==null || key.length()==0)
                {

                }else
                {
                    mDatabase.child("Socials").child(key).setValue(post);
                }
            }
        });
    }

}
