package com.example.yujhaochen.ims;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class VideoRecord extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 1;

    private VideoPreview mVideoPreview = null;
    private Button buttonOperation = null;
    private Button buttonSwitchCamera = null;
    private Button buttonCapture = null;
    private Button Btn_Finish = null;
    private String TAG = "TakePicture";

    private String FilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_record);


        Bundle bundleAccount = this.getIntent().getExtras();

        FilePath = bundleAccount.getString("FilePath");


        mVideoPreview = (VideoPreview) findViewById(R.id.videorecordview);
        mVideoPreview.switchCamera();
        buttonOperation = (Button) findViewById(R.id.operation);
        buttonOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoPreview.isRecording()) {
                    buttonOperation.setText("Start");

                    mVideoPreview.setFilePath(FilePath);
                    mVideoPreview.stopRecord();
                } else {
                    buttonOperation.setText("Stop");
                    mVideoPreview.setFilePath(FilePath);
                    mVideoPreview.startRecord();
                }
            }
        });

        buttonSwitchCamera = (Button)findViewById(R.id.switch_camera);
        buttonSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOperation.setText("Start");
                if(!mVideoPreview.switchCamera()){
                    Toast.makeText(VideoRecord.this, "switch failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonCapture = (Button)findViewById(R.id.TakePicture);
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoPreview.setFilePath(FilePath);
                mVideoPreview.mTakePicture(mPicture);
                mVideoPreview.StartPrevCamera();
            }
        });

        Btn_Finish = (Button)findViewById(R.id.Btn_Finish);
        Btn_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        Btn_Finish.setEnabled(false);

    }

    private void CameraCaptureFinish()
    {
        Intent mIntent = new Intent();
        mIntent.putExtra("FilePath", FilePath);
        // 设置结果，并进行传送
        setResult(MEDIA_TYPE_IMAGE, mIntent);

        finish();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = new File(FilePath);

            if (pictureFile == null){
//                Log.d(TAG, "Error creating media file, check storage permissions: " +
//                        e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                CameraCaptureFinish();
                //Btn_Finish.setEnabled(true);

                 /*假若按下開始 - 結束 之間 未超過1秒, 會crash, 原因應該是因為 MediaRecord Server 尚未收到資料. 加入下列兩行可解此問題*/

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }



    @Override
    protected void onPause() {
        super.onPause();
        mVideoPreview.releaseMediaRecorder();
        mVideoPreview.releaseCamera();
    }
}