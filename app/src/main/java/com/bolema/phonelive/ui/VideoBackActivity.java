package com.bolema.phonelive.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.tedcoder.wkvideoplayer.dlna.engine.DLNAContainer;
import com.android.tedcoder.wkvideoplayer.dlna.service.DLNAService;
import com.android.tedcoder.wkvideoplayer.model.Video;
import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.bean.LiveRecordBean;
import com.bolema.phonelive.R;
import com.bolema.phonelive.base.ToolBarBaseActivity;

import java.util.ArrayList;

import butterknife.InjectView;


//直播回放
public class VideoBackActivity extends ToolBarBaseActivity {

    @InjectView(R.id.video_player)
    SuperVideoPlayer mSuperVideoPlayer;
    private LiveRecordBean mLiveRecord;
    @InjectView(R.id.play_btn)
    ImageView mPlayBtnView;

    @Override
    protected int getLayoutId() {
        //        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }
        return R.layout.activity_videoback;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mPlayBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlayerLive();
            }
        });
    }

    //开始播放视频
    private void startPlayerLive() {
        mPlayBtnView.setVisibility(View.GONE);
        Video video = new Video();
        VideoUrl videoUrl1 = new VideoUrl();
        videoUrl1.setFormatName("贼清楚");
        videoUrl1.setFormatUrl(mLiveRecord.getVideo_url());
        ArrayList<VideoUrl> arrayList1 = new ArrayList<>();
        arrayList1.add(videoUrl1);
        video.setVideoName("测试视频一");
        video.setVideoUrl(arrayList1);
        ArrayList<Video> videoArrayList = new ArrayList<>();
        videoArrayList.add(video);
        mSuperVideoPlayer.setVisibility(View.VISIBLE);
        mSuperVideoPlayer.loadMultipleVideo(videoArrayList,0,0,0);
    }

    @Override
    public void initData() {
        mLiveRecord = getIntent().getParcelableExtra("video");

        /*PhoneLiveApi.getVideoCode(mLiveRecord.getVideo_url(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                long time = System.currentTimeMillis()/1000 - Long.parseLong(mLiveRecord.getEndtime());

                int needTime = ((int)(time / 60));
                if(needTime <= 5){
                    showToast3("生成视频还需要还大约" + (5 - needTime) + "分钟,如果视频已生成请忽略该信息",0);
                }
            }

            @Override
            public void onResponse(String response) {

            }
        });*/
        //判断视频是否生成
        if(!TextUtils.isEmpty(mLiveRecord.getVideo_url())){
            mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
            startDLNAService();
        }else {
            showToast3("回放视频暂未生成",0);
        }
    }

    @Override
    public void onClick(View view) {

    }



    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            mSuperVideoPlayer.close();
            mPlayBtnView.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.setVisibility(View.GONE);
            resetPageToPortrait();
        }

        @Override
        public void onSwitchPageType() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {
        }
    };
    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
        }
    }

    private void startDLNAService() {
        // Clear the device container.
        DLNAContainer.getInstance().clear();
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        startService(intent);
    }

    private void stopDLNAService() {
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        stopService(intent);
    }
    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == mSuperVideoPlayer) return;
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSuperVideoPlayer.getLayoutParams().height = (int) width;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            mSuperVideoPlayer.getLayoutParams().height = (int) height;
            mSuperVideoPlayer.getLayoutParams().width = (int) width;
        }
    }
    @Override
    protected boolean hasActionBar() {
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDLNAService();
    }
    public static void startVideoBack(Context context, LiveRecordBean video){
        Intent intent = new Intent(context,VideoBackActivity.class);
        intent.putExtra("video",video);
        context.startActivity(intent);
    }
}
