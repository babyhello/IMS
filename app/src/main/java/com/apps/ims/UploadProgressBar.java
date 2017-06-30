package com.apps.ims;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;


public class UploadProgressBar extends Activity {


    private Context mContext;
    //提示語
    private String updateMsg = "New Version Update!!!";

    private Dialog downloadDialog;
    /* 進度條與通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UploadProgressBar(Context context) {
        this.mContext = context;


    }

    public void GO_UpdateProgress(float count, float length) {
        progress = (int) ((count / length) * 100);

//        if (count == length)
//        {
//
//        }
    }

    public void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Update");

        final LayoutInflater inflater = LayoutInflater.from(mContext);

        View v = inflater.inflate(com.apps.ims.R.layout.progress, null);

        mProgress = (ProgressBar) v.findViewById(com.apps.ims.R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();
    }


}
