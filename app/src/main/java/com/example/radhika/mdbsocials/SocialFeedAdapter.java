package com.example.radhika.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.w3c.dom.Text;

/**
 * Created by Radhika on 2/24/17.
 * copied from pokedexadapter
 */

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<Social> socials;

    public SocialFeedAdapter(Context context, ArrayList<Social> socials) {
        this.context = context;
        this.socials = socials;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Social s = socials.get(position);
        holder.hostEmailText.setText(s.getEmailAddress());
        holder.socialNameText.setText(s.getName());
        holder.numInterested.setText(s.getNumInterested() + " Interested");

        Glide.with(context)
                .load(s.getImageUrl())
                .into(holder.socialImageView);
    }

    @Override
    public int getItemCount() {
        return socials.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView socialNameText, hostEmailText, numInterested;
        ImageView socialImageView;

        public CustomViewHolder(View view) {
            super(view);

            this.socialNameText = (TextView) view.findViewById(R.id.socialName);
            this.hostEmailText = (TextView) view.findViewById(R.id.hostEmail);
            this.numInterested = (TextView) view.findViewById(R.id.interested);
            this.socialImageView = (ImageView) view.findViewById(R.id.socialImageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SocialDetailsActivity.class);
                    i.putExtra("Social Key", SocialFeedActivity.keys.get(getAdapterPosition()));
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    public void updateList(ArrayList<Social> newList)
    {
        this.socials = newList;
    }

}