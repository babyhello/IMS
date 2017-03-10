package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class imageZoom extends Activity {


    //public static Bitmap ImageSource = null;

    public static String ImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_zoom);

        final MyZoomImageView Img_WorkNote = (MyZoomImageView)findViewById(R.id.ImageViewZoom);

        Glide
                .with(imageZoom.this)
                .load(ImagePath)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(300,300) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        Img_WorkNote.setImageBitmap(resource);

                    }
                });

    }
}
