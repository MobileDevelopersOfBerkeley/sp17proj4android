package com.example.joey.mdbsocials;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by joey on 10/6/17.
 */

public class FirebaseUtils {


    /**
     * Creates a new user in firebase
     * @param context the context of the signup activity - used to display toasts
     * @param email the email the user wishes to create account with as string
     * @param pass1 password string
     * @param pass2 check to see if two passwords match
     */
    public static void attemptSignup(final Context context, String email, String pass1, String pass2) {
        if (email.equals("")) {
            Toast.makeText(context, "Enter Valid email!", Toast.LENGTH_SHORT).show();
        } else if (pass1.equals("") || pass2.equals("")) {
            Toast.makeText(context, "Complete both password banks!", Toast.LENGTH_SHORT).show();
        } else if (!pass1.equals(pass2)) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass1).addOnCompleteListener((Activity) context,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Signup failed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, FeedActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

    }

    /**
     *Attempts a login based on user input in fields
     * @param context the context of Login Activity
     * @param email a string that is the email the user submits
     * @param password a string that is the password the user submits.
     */
    public static void attemptLogin(final Context context, String email, String password) {

        if (!email.equals("") && !password.equals("")) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("FIREBASE", "signInWithEmail:onComplete:"+task.isSuccessful());

                    if(!task.isSuccessful()){
                        Log.w("FIREBASE", "signInWithEmail:failed", task.getException());
                        Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(context, FeedActivity.class);
                        context.startActivity(intent);
                        Toast.makeText(context, "Login SUCCESS!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    /**
     * Attempts to create a new social based on entered fields.
     * @param context context of CreateSocial Activity to push toasts / start activiites
     * @param name name of new social
     * @param description description of social
     * @param date date of social
     * @param file image file associated with social
     */
    public static void pushNewSocial(final CreateSocial context, final String name, final String description, final String date, final Uri file){
        //check to see if image exists
        if(file == null){
            Toast.makeText(context, "no image selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.equals("") || description.equals("") ||date.equals("")){
            Toast.makeText(context, "Must fill in fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //get Firebase information
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //First generate a key for the image
        final String key = ref.push().getKey();
        StorageReference imageRef = storageReference.child(key + ".png");

        imageRef.putFile(file).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "no image selected!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Social social = new Social(key, user.getEmail(), name, description, date);

                ref.child(key).setValue(social);

                Intent returnIntent = new Intent(context, FeedActivity.class);
                context.startActivity(returnIntent);

            }
        });
    }

    /**
     * Creates the firebase listener for social objects and adds them to the feed Recycler view.
     * @param context context of the feed activity
     * @param adapter the adapter of the recycler view
     */
    public static void initFeed(final FeedActivity context, final SocialAdapter adapter){
        final ArrayList<Social> socials = adapter.getSocials();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Social> newSocials = new ArrayList<>();
                for (DataSnapshot socialObject : dataSnapshot.getChildren()){
                    Social social = socialObject.getValue(Social.class);
                    newSocials.add(social);
                }
                //CODE DOES ORDER BY TIMESTAMP, but in descending order, reverse to chagne to ascending.
                Collections.reverse(newSocials);
                socials.clear();
                socials.addAll(newSocials);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,"Oh no! Database error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Class used for asycronous image downloading.
     */
    private static class DownloadImagesTask extends AsyncTask<Uri, Void, Bitmap> {

        private ImageView imageView;
        private ProgressBar progressBar;

        public DownloadImagesTask(ImageView imageView, ProgressBar progressBar){
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            if(uris.length == 1){
                return downloadImage(uris[0].toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }


        private Bitmap downloadImage(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                Log.d("Exceptions", e.getMessage());
            } catch (IOException e) {
                Log.d("Exceptions", e.getMessage());
            }
            return null;
        }
    }

    /**
     * Instantiates a new Download image task to pull an image from firebase for a given social
     * @param id the id of the social
     * @param imageView view where image is placed
     * @param progressBar progress bar that image replaces.
     */
    public static void loadImageFromFirebase(final String id, final ImageView imageView, final ProgressBar progressBar){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(id+".png");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadImagesTask(imageView, progressBar).execute(uri);
            }
        });

    }

    /**
     * Uses firebase transaction to update interested field
     * @param id id of the social.
     */
    public static void addInterestedTransaction(String id){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials/" + id);
        String key = ref.child("people").push().getKey();
        ref.child("people").child(key).setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ref.child("interested").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int interested = mutableData.getValue(Integer.class);
                interested++;
                mutableData.setValue(interested);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("FIREBASE", "Successful incrementation of interested count");

            }
        });

    }


}
