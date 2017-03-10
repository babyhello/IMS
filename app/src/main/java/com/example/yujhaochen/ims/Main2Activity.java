package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        launchCamera();
    }

    private static final int CAPTURE_MEDIA = 368;

    // showImagePicker is boolean value: Default is true
    private void launchCamera() {
        new SandriosCamera(this, CAPTURE_MEDIA)
                .setShowPicker(true)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_VIDEO) // default is CameraConfiguration.MEDIA_ACTION_BOTH
                .launchCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_MEDIA && resultCode == RESULT_OK) {
            Log.e("File", "" + data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
            Toast.makeText(this, "Media captured.", Toast.LENGTH_SHORT).show();
        }
    }
}
