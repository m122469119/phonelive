package com.bolema.phonelive.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bolema.phonelive.R;

import java.net.URI;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecordedVideoActivity extends AppCompatActivity {

    @InjectView(R.id.video_view_live)
    VideoView videoViewLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorded_video);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String url = bundle.getString("url");
        Log.d("videoUrl", url);

        Uri uri = Uri.parse(url);
        videoViewLive.setMediaController(new MediaController(this));
        //播放完成回调
        videoViewLive.setOnCompletionListener(new MyPlayerOnCompletionListener());

        //设置视频路径
        videoViewLive.setVideoURI(uri);

        videoViewLive.start();
    }

    private class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(RecordedVideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }
}
