package com.example.eric.socials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by eric on 2/24/17.
 */

public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<User> data;

    public InterestedAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public InterestedAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InterestedAdapter.CustomViewHolder holder, int position) {
        User u = data.get(position);
        holder.userName.setText(u.name);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public CustomViewHolder (View view) {
            super(view);
            this.userName = (TextView) view.findViewById(R.id.userName);
        }
    }
}
