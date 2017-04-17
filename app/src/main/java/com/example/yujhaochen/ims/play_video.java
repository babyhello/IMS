package com.example.yujhaochen.ims;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.vision.text.Text;

public class play_video extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Bundle Bundle = this.getIntent().getExtras();

        String VideoUrl = Bundle.getString("VideoUrl");

        if (!TextUtils.isEmpty(VideoUrl)) {
            Handler mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
            LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
            SimpleExoPlayer player =
                    ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter mbandwidthMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "yourApplicationName"), mbandwidthMeter);
// Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(VideoUrl),
                    dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
            player.prepare(videoSource);

            player.setPlayWhenReady(true);
            SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

            simpleExoPlayerView.setPlayer(player);


        }


    }
}
