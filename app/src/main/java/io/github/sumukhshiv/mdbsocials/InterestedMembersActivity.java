package io.github.sumukhshiv.mdbsocials;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

public class InterestedMembersActivity extends AppCompatActivity {

    Social social;
    InterestedProfilesAdapter toSetProfilesAdapter;
    ArrayList<String> profilesInterested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_members);

        //grab the social object to populate recyclerView
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewInterestedMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Grab social objects people interested arraylist for adapter
        profilesInterested = social.peopleInterested;
        toSetProfilesAdapter = new InterestedProfilesAdapter(getApplicationContext(), profilesInterested);
        recyclerView.setAdapter(toSetProfilesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        social = (Social) bundle.getSerializable("SOCIAL");
    }
}
