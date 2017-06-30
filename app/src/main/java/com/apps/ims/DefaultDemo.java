package com.apps.ims;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DefaultDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.apps.ims.R.layout.activity_default_demo);


        IssueVideoPlay IssueVideoPlay = (IssueVideoPlay) findViewById(com.apps.ims.R.id.IssueVideoPlayGo);

        //IssueVideoPlay.VideoURL = GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/VSS/Code/apps/P20170223154210.mp4";


    }
}
