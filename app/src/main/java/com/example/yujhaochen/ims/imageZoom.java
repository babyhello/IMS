package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jsibbold.zoomage.ZoomageView;


public class imageZoom extends Activity {


    //public static Bitmap ImageSource = null;
    private ProgressDialog pDialog;
    public static String ImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(imageZoom.this);
        setContentView(R.layout.activity_image_zoom);

        final ZoomageView Img_WorkNote = (ZoomageView) findViewById(R.id.ImageViewZoom);

//        pDialog.setTitle("Loading...");
//
//        pDialog.show();
        //Log.w("ImagePath:",ImagePath);
        Glide
                .with(imageZoom.this)
                .load(ImagePath)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(1280, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        Img_WorkNote.setImageBitmap(resource);

                        //pDialog.hide();
                    }
                });

    }
}
