package com.example.radhika.mdbsocials;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Radhika on 2/25/17.
 */

public class Utils {
    public static User user;
    public static void update (DatabaseReference db, final User curr) {
        db.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mut) {
                Social s = mut.getValue(Social.class);
                if (s == null)
                    return Transaction.success(mut);
                if (s.getInterested() == null) {
                    s.setInterested(new ArrayList<User>());
                    s.getInterested().add(curr);
                    s.setNumInterested(s.getNumInterested()+1);
                }
                else if (s.getInterested().contains(curr)) {
                    s.setNumInterested(s.getNumInterested()-1);
                    s.getInterested().remove(curr);
                } else {
                    s.setNumInterested(s.getNumInterested()+1);
                    s.getInterested().add(curr);
                }
                mut.setValue(s);
                return Transaction.success(mut);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
    public static void getUser(DatabaseReference db, FirebaseUser user) {
        db.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Utils.user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

//    public static void addUserToDB (String name, String email, String imageUrl) {
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//        final String key = db.child("Users").push().getKey();
//        curr = new User (name, email, imageUrl);
//        db.child("Users").child(key).setValue(curr);
//    }
}
