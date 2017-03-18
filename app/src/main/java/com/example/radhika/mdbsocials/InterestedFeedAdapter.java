package com.example.radhika.mdbsocials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Radhika on 2/25/17.
 */

public class InterestedFeedAdapter extends RecyclerView.Adapter<InterestedFeedAdapter.CustomViewHolder> {
    Context context;
    List<User> interested;

    public InterestedFeedAdapter (Context context, List<User> list){
        this.context = context;
        this.interested = list;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_2, parent, false);
        return new InterestedFeedAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position){
        User u = interested.get(position);
        holder.interestedTextView.setText(u.getEmail());
        holder.interestedName.setText(u.getName());
        Glide.with(context)
                .load(u.getImageUrl())
                .into(holder.interestedPicture);
    }

    public int getItemCount(){
        return interested.size();
    }

    public void updateList (List<User> newList) {
        this.interested = newList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView interestedTextView;
        TextView interestedName;
        ImageView interestedPicture;

        public CustomViewHolder(View view){
            super(view);
            this.interestedTextView = (TextView) (view.findViewById(R.id.interestedEmail));
            this.interestedName = (TextView) (view.findViewById(R.id.interestedName));
            this.interestedPicture = (ImageView) (view.findViewById(R.id.userImage));
        }
    }

}
