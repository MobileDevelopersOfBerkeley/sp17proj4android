package com.example.datarsd1.mdbsocials_2;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Adhiraj Datar on 3/2/2017.
 */

public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.CustomViewHolder>
{
    Context context;
    public static ArrayList<User> mdbros;
    Activity activity;

    public InterestedAdapter(Activity activity, Context context, ArrayList<User> interestedpeople)
    {
        this.context = context;
        this.activity = activity;
        this.mdbros = interestedpeople;
    }

    public InterestedAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_user, parent, false);
        return new CustomViewHolder(view);
    }

    public void onBindViewHolder(final InterestedAdapter.CustomViewHolder holder, int position)
    {
        User curr = mdbros.get(position);
        holder.interestedPersonEmail.setText(curr.email);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-2.appspot.com");
        StorageReference interestedImageRef = mStorageRef.child(curr.img);
        interestedImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                new DownloadImageTask(activity, context, holder.interestedPersonImage).execute(uri.toString());
            }
        });
    }

    public int getItemCount()
    {
        return mdbros.size();
    }

    public void updateList(ArrayList<User> i)
    {
        mdbros = i;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {
        TextView interestedPersonEmail;
        ImageView interestedPersonImage;

        public CustomViewHolder(View view) {
            super(view);

            interestedPersonEmail = (TextView) view.findViewById(R.id.userProfileEmail);
            interestedPersonImage = (ImageView) view.findViewById(R.id.userProfileImage);
        }
    }

}