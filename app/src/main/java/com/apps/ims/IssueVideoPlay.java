
package com.apps.ims;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

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

        View v = mInflater.inflate(com.apps.ims.R.layout.activity_issue_video_play, this, true);

        txt_Video_Name = (TextView) v.findViewById(com.apps.ims.R.id.txt_Video_Name);

        if (!TextUtils.isEmpty(VideoPath)) {
            txt_Video_Name.setText(VideoPath.substring(VideoPath.lastIndexOf('/') + 1));
        }
    }

    public void init() {

        View v = mInflater.inflate(com.apps.ims.R.layout.activity_issue_video_play, this, true);

    }


}