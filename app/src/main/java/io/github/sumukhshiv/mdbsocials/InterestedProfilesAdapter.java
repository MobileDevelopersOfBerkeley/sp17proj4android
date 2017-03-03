package io.github.sumukhshiv.mdbsocials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * Created by sumukhshivakumar on 2/25/17.
 */

public class InterestedProfilesAdapter extends RecyclerView.Adapter<InterestedProfilesAdapter.CustomViewHolder> {
    Context context;
    ArrayList<String> profiles;

    public InterestedProfilesAdapter(Context context, ArrayList<String> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public InterestedProfilesAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InterestedProfilesAdapter.CustomViewHolder holder, int position) {
        final String profile = profiles.get(position);
        /**
         * Arraylist is a list of IDs so we retrieve each user and go into Database to find corresponding
         * email address
         */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users").child(profile);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue().toString();
                holder.MemberName.setText(email);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //Only using email of user so no need to grab the profile image of each user
        TextView MemberName;

        public CustomViewHolder(View view) {
            super(view);
            MemberName = (TextView) view.findViewById(R.id.textViewInterestedMember);

        }
    }
}
