package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserArea extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/socials");
    ArrayList<Social> arrayListSocials;
    ValueEventListener mSocialsEventListener;
    SocialsAdapter toSetSocialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //Elements for the side navbar with the series of options.
        final ListView mDrawerList;
        final ArrayAdapter<String> mAdapter;
        String[] optionsArray = {"Update Profile", "Create New Social", "Logout"};
        final Intent grabIntent = getIntent();
        mDrawerList = (ListView) findViewById(R.id.navList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsArray);
        mDrawerList.setAdapter(mAdapter);

        //Default toast to indicate swipe from the left reveals more options.
        Toast.makeText(UserArea.this, "Swipe from the left for more options", Toast.LENGTH_SHORT).show();

        /**
         * Click listener for the items of the navbar. Did not implement for the class because this
         * is not directly a view viewed by default on the screen. This works best according to
         * StackOverFlow.
         */
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray clickedItemPositions = mDrawerList.getCheckedItemPositions();
                mAdapter.notifyDataSetChanged();

                for(int j = 0;j < clickedItemPositions.size(); j ++){
                    boolean checked = clickedItemPositions.valueAt(j);

                    if(checked) {
                        int key = clickedItemPositions.keyAt(j);
                        String item = (String) mDrawerList.getItemAtPosition(key);

                        switch (item) {
                            case ("Update Profile"):
                                Intent updateProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                updateProfileIntent.putExtra("email", grabIntent.getStringExtra("email"));
                                startActivity(updateProfileIntent);
                                break;
                            case ("Create New Social"):
                                Intent newSocialIntent = new Intent(getApplicationContext(), NewSocialActivity.class);
                                newSocialIntent.putExtra("email", grabIntent.getStringExtra("email"));
                                startActivity(newSocialIntent);
                                break;
                            case ("Logout"):
                                FirebaseAuth.getInstance().signOut();
                                Intent logoutIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(logoutIntent);
                                break;

                        }

                    }
                }
            }
        });


        /**
         * Grabbing RecyclerView
         * Setting LayoutManager
         * Creating a new Adapter
         * Attaching it to the recyclerView
         */

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        arrayListSocials = new ArrayList<>();
        toSetSocialsAdapter = new SocialsAdapter(getApplicationContext(), arrayListSocials);
        recyclerView.setAdapter(toSetSocialsAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(UserArea.this, "Swipe from the left for more options", Toast.LENGTH_LONG).show();
        if (mSocialsEventListener == null) {
            mSocialsEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Social> temp = new ArrayList<>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        /**
                         * Create a new Social Object based on what is found within Firebase
                         * Uses the datasnapchat and integrates through all of database to create
                         * social object for each social
                         */

                        String name = postSnapshot.child("nameOfEvent").getValue(String.class);
                        String date = postSnapshot.child("date").getValue(String.class);
                        String description = postSnapshot.child("description").getValue(String.class);
                        String image = postSnapshot.child("image").getValue(String.class);
                        String emailOfHost = postSnapshot.child("emailOfHost").getValue(String.class);
                        int numberInterested = postSnapshot.child("numberIntersted").getValue(Integer.class);
                        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                        ArrayList<String> listOfUsersInSocial = postSnapshot.child("peopleInterested").getValue(t);
                        Social newSocial = new Social(name, date, description, image, emailOfHost, numberInterested, listOfUsersInSocial);
                        temp.add(newSocial);
                        Log.d("DEBUG", arrayListSocials.size() + "");
                    }
                    toSetSocialsAdapter.setSocialsList(temp);
                    toSetSocialsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            myRef.addValueEventListener(mSocialsEventListener);
        }
    }

}
