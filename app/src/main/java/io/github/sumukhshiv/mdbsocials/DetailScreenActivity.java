package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Social social;
    TextView textViewEventNameDetail;
    ImageView imageViewDetail;
    TextView emailDetail;
    Button buttonInterestedDetail;
    TextView textViewDescription;
    Button buttonAreYouInterested;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        textViewEventNameDetail = (TextView) findViewById(R.id.textViewEventNameDetail);
        imageViewDetail = (ImageView) findViewById(R.id.imageViewDetail);
        emailDetail = (TextView) findViewById(R.id.textViewEmailDetail);
        buttonInterestedDetail = (Button) findViewById(R.id.buttonInterstedDetail);
        buttonAreYouInterested = (Button) findViewById(R.id.buttonAreYouInterested);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        buttonAreYouInterested.setOnClickListener(this);
        buttonInterestedDetail.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        urlString = "";

    }

    @Override
    protected void onResume() {
        super.onResume();

        //grabbing the social, which was just clicked on from UserArea
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");

        //Setting different aspects of the Detail Screen View
        textViewEventNameDetail.setText(social.nameOfEvent);
        emailDetail.setText(social.emailOfHost);
        buttonInterestedDetail.setText(Integer.toString(social.numberIntersted));
        textViewDescription.setText(social.description);
        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                //Adding image to imageView on the Detail Screen using AsyncTask (w/o Glide)
                try {
                    urlString = strings[0];
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    return null;
                }
        }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                imageViewDetail.setImageBitmap(result);
            }
        }

        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com").child(social.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                (new DownloadFilesTask()).execute(uri.toString());
                Log.d("Adding to ImageView", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Failed adding Image", exception.toString());
            }
        });

    }

    /**
     * Helper Method used to add current user to the Interested Array List of Social
     * Takes in no parameters and returns nothing
     * Series of tasks to add the current user after checking various cases in Switch statement
     */
    private void addInterested() {
        //NOTE, also add self email to people interested arraylist
        final String socialKey = social.image.substring(0, social.image.length() - 4);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials").child(socialKey);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberIntersted = dataSnapshot.child("numberIntersted").getValue(Integer.class);
                numberIntersted++;
                buttonInterestedDetail.setText(Integer.toString(numberIntersted));
                addInterestedToDatabase(numberIntersted, socialKey);
                ArrayList<String> a = social.peopleInterested;
                if (a != null && !a.contains(firebaseUser.getUid())) {
                    a.add(firebaseUser.getUid());
                    social.peopleInterested = a;
                    ref.child("peopleInterested").setValue(a);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Helper method to actually write updated Arraylist of interested users to Firebase Database
     * @param interested
     * @param firekey
     */
    public static void addInterestedToDatabase(int interested, String firekey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("socials");
        ref.child(firekey).child("numberIntersted").setValue(interested);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.buttonAreYouInterested):
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/users").child(firebaseUser.getUid());

                //SingleValueEventListener because we want to only retrieve the information once.
                //Traditional ValueEventListener causes problems because it is triggered multiple times
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object user = dataSnapshot.child("email").getValue();

                        //Checking if user inputed all the various fields
                        //Used if/else if/else because only 3 cases
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Please Update Profile!",
                                    Toast.LENGTH_LONG).show();
                        } else if (social.peopleInterested.contains(firebaseUser.getUid())) {
                            Toast.makeText(getApplicationContext(), "You're already Interested!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //call on helper method to actually add user to arraylist in social
                            addInterested();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

            case (R.id.buttonInterstedDetail):
                FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                DatabaseReference myRef2 = database2.getReference("/users").child(firebaseUser.getUid());

                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object user = dataSnapshot.child("email").getValue();

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Please Update Profile!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //Pass social object to populate the recycler view using its arrayList of interested members
                            Intent intent = new Intent(getApplicationContext(), InterestedMembersActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("SOCIAL", social);
                            intent.putExtras(bundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
