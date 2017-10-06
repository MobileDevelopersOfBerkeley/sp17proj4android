package com.example.joey.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by joey on 9/27/17.
 */

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialViewHolder> {
    private Context context;
    private ArrayList<Social> socials;

    public ArrayList<Social> getSocials(){
        return socials;
    }

    public SocialAdapter(Context context, ArrayList<Social> socials){
        this.context = context;
        this.socials = socials;
    }

    @Override
    public SocialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_row, parent, false);
        return new SocialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SocialViewHolder holder, int position) {
        Social social = socials.get(position);
        holder.emailText.setText(social.owner);
        holder.interestedCount.setText("" + social.interested);
        holder.eventName.setText(social.name);

        FirebaseUtils.loadImageFromFirebase(social.id, holder.eventImage, holder.progressBar);
    }

    @Override
    public int getItemCount(){
        return socials.size();
    }

    class SocialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView eventName;
        ImageView eventImage;
        TextView interestedCount;
        TextView emailText;
        ProgressBar progressBar;

        public SocialViewHolder(View v){
            super(v);
            eventName =  v.findViewById(R.id.eventName);
            eventImage = v.findViewById(R.id.eventImage);
            interestedCount =  v.findViewById(R.id.eventInterested);
            emailText =  v.findViewById(R.id.eventEmail);
            progressBar = v.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // implement code to open the detail screen for the current view
            Social s = socials.get(getAdapterPosition());
            Intent intent = new Intent(context, EventProfile.class);
            intent.putExtra("id", s.id);
            intent.putExtra("name", s.name);
            intent.putExtra("date", s.date);
            intent.putExtra("description", s.description);
            intent.putExtra("interested", s.interested);

            context.startActivity(intent);


        }
    }

}
