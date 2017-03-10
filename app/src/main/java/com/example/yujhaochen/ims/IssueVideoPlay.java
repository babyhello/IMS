
package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;

public class IssueVideoPlay extends RelativeLayout {

    // Declare variables
    ProgressDialog pDialog;
    android.widget.VideoView IssueVideoView;

    // Insert your Video URL
    public String VideoURL = "http://172.16.111.114/File/VSS/Code/IMS/P20170223154210.mp4";

    LayoutInflater mInflater;

    public String fileName;

    public Context mcontext;

    public IssueVideoPlay(Context context) {


        super(context);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        init();

    }

    public IssueVideoPlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public IssueVideoPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void SetVoicePath(String VoicePath) {

        fileName = VoicePath;


    }

    public void init() {

        View v = mInflater.inflate(R.layout.activity_issue_video_play, this, true);

        IssueVideoView = (android.widget.VideoView) v.findViewById(R.id.IssueVideoView);

        // Create a progressbar
        pDialog = new ProgressDialog(mcontext);
        // Set progressbar title
        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    mcontext);
            mediacontroller.setAnchorView(IssueVideoView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            IssueVideoView.setMediaController(mediacontroller);
            IssueVideoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        IssueVideoView.requestFocus();
        IssueVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                IssueVideoView.start();
            }
        });
    }


}