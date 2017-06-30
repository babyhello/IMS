package com.apps.ims;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

public class VideoView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.apps.ims.R.layout.activity_video_view);
    }

    //... some code
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });
}
