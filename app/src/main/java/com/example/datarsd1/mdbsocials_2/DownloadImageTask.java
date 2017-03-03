package com.example.datarsd1.mdbsocials_2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.R.attr.bitmap;

/**
 * Created by Adhiraj Datar on 3/2/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
    private ImageView container;
    private ProgressDialog mDialog;
    private Context context;
    private Activity act;
    private Bitmap bitmap;

    public DownloadImageTask(Activity act, Context context, ImageView mActivity) {
        super();
        this.container = mActivity;
        this.act = act;
        this.context = context;
        mDialog = new ProgressDialog(act);
        bitmap = null;
    }

    protected void onPreExecute() {
        container.setImageBitmap(null);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(true);
        mDialog.setMessage("Loading...");
        mDialog.setProgress(0);
        mDialog.show();
    }

    protected Bitmap doInBackground(String... urls) {
        for (String s: urls)
        {
            try{
                bitmap = Glide.with(context)
                        .load(s)
                        .asBitmap()
                        .into(100,100)
                        .get();
            } catch (final ExecutionException e) {
                Log.e("h", e.getMessage());
            } catch (final InterruptedException e) {
                Log.e("a", e.getMessage());
            } catch (final NullPointerException e) {
                //
            }
            if (isCancelled()) break;
        }
        return bitmap;
    }
    protected void onProgressUpdate(Integer... progress) {
        mDialog.show();
        mDialog.setProgress(progress[0]);
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            container.setImageBitmap(result);
        }
        else {

        }

        mDialog.dismiss();
    }

}