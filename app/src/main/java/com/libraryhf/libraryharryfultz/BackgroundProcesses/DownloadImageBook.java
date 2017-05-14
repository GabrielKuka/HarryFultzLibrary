package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageBook extends AsyncTask<String, Void, Bitmap> {

    private ImageView bookImage;
    private AppCompatActivity a;

    public DownloadImageBook(ImageView image, AppCompatActivity ac) {
        this.bookImage = image;
        this.a = ac;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        //bookImage.setImageBitmap(result);
        bookImage.setBackgroundDrawable(new BitmapDrawable(a.getResources(), result));
    }
}