package com.example.yujhaochen.ims;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.volokh.danylo.video_player_manager.PlayerMessageState;
import com.volokh.danylo.video_player_manager.SetNewViewForPlayback;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.player_messages.ClearPlayerInstance;
import com.volokh.danylo.video_player_manager.player_messages.CreateNewPlayerInstance;
import com.volokh.danylo.video_player_manager.player_messages.PlayerMessage;
import com.volokh.danylo.video_player_manager.player_messages.Prepare;
import com.volokh.danylo.video_player_manager.player_messages.Release;
import com.volokh.danylo.video_player_manager.player_messages.Reset;
import com.volokh.danylo.video_player_manager.player_messages.SetAssetsDataSourceMessage;
import com.volokh.danylo.video_player_manager.player_messages.Start;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.Arrays;

public class VideoView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
    }
    //... some code
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });
}
