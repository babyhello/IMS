
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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.*;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;

public class IssueVideoPlay extends RelativeLayout {

    // Declare variables
    ProgressDialog pDialog;

    TextView txt_Video_Name;

    // Insert your Video URL
    public String VideoURL;

    LayoutInflater mInflater;

    public String fileName;

    public Context mcontext;

    public IssueVideoPlay(Context context) {


        super(context);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        //init();

    }

    public IssueVideoPlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        //init();
    }

    public IssueVideoPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        mInflater = LayoutInflater.from(context);
        //init();
    }

    public void SetVideoPath(String VideoPath) {

        View v = mInflater.inflate(R.layout.activity_issue_video_play, this, true);

        txt_Video_Name = (TextView) v.findViewById(R.id.txt_Video_Name);

        if (!TextUtils.isEmpty(VideoPath)) {
            txt_Video_Name.setText(VideoPath.substring(VideoPath.lastIndexOf('/') + 1));
        }
    }

    public void init() {

        View v = mInflater.inflate(R.layout.activity_issue_video_play, this, true);

    }


}