package io.github.sumukhshiv.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Created by sumukhshivakumar on 2/24/17.
 */

public class SocialsAdapter extends RecyclerView.Adapter<SocialsAdapter.CustomViewHolder> {

    Context context;
    ArrayList<Social> socials;

    public SocialsAdapter(Context context, ArrayList<Social> socials) {
        this.context = context;
        this.socials = socials;
    }

    public void setSocialsList(ArrayList<Social> list) {
        socials = list;
    }

    @Override
    public SocialsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SocialsAdapter.CustomViewHolder holder, int position) {
        //used to view the most recent socials at the top
        final Social social = socials.get(socials.size() - position - 1);

        //Adds image from Storage into the imageView
        //Uses AsyncTask and Firebase Download from URL
        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {return Glide.
                        with(context).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();}
                catch (Exception e) {return null;}
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                holder.imageViewEventImage.setImageBitmap(result);
            }
        }
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-e2598.appspot.com").child(social.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                (new DownloadFilesTask()).execute(uri.toString());
                Log.d("AddingPhoto", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Failed to add image", exception.toString());
            }
        });


        holder.textViewEventName.setText(social.nameOfEvent);
        holder.textViewHostEmail.setText(social.emailOfHost);
        holder.textViewNumberInterested.setText(Integer.toString(social.numberIntersted));


        //Click listener for each card. Kept as setOnCLickListener because used only once in the code
        //Uses the social object specific to the one found in each instance of the bindViewHolder
        holder.cardViewFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailScreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SOCIAL", social);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return socials.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //Set up the variables
        ImageView imageViewEventImage;
        TextView textViewEventName;
        TextView textViewHostEmail;
        TextView textViewNumberInterested;
        CardView cardViewFeed;

        public CustomViewHolder(View view) {
            super(view);

            imageViewEventImage = (ImageView) view.findViewById(R.id.imageViewSocialImage);
            textViewEventName = (TextView) view.findViewById(R.id.textViewEventName);
            textViewHostEmail = (TextView) view.findViewById(R.id.textViewHostEmail);
            textViewNumberInterested = (TextView) view.findViewById(R.id.textViewNumberInterested);
            cardViewFeed = (CardView) view.findViewById(R.id.cardViewFeed);

        }
    }

}
