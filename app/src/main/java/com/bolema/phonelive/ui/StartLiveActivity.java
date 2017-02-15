package com.bolema.phonelive.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ShowLiveActivityBase;
import com.bolema.phonelive.bean.ChatBean;
import com.bolema.phonelive.bean.MusicLrcBean;
import com.bolema.phonelive.bean.SendGiftBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.fragment.MusicPlayerDialogFragment;
import com.bolema.phonelive.fragment.SearchMusicDialogFragment;
import com.bolema.phonelive.fragment.UserInfoDialogFragment;
import com.bolema.phonelive.interf.ChatServerInterface;
import com.bolema.phonelive.interf.DialogInterface;
import com.bolema.phonelive.ui.other.ChatServer;
import com.bolema.phonelive.ui.other.LiveStream;
import com.bolema.phonelive.utils.DialogHelp;
import com.bolema.phonelive.utils.GsonTools;
import com.bolema.phonelive.utils.InputMethodUtils;
import com.bolema.phonelive.utils.LiveUtils;
import com.bolema.phonelive.utils.ShareUtils;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.music.DefaultLrcBuilder;
import com.bolema.phonelive.widget.music.ILrcBuilder;
import com.bolema.phonelive.widget.music.LrcRow;
import com.bolema.phonelive.widget.music.LrcView;
import com.ksy.recordlib.service.core.KSYStreamerConfig;
import com.ksy.recordlib.service.streamer.OnStatusListener;
import com.ksy.recordlib.service.streamer.RecorderConstants;
import com.ksy.recordlib.service.util.audio.KSYBgmPlayer;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 直播页面
 * 本页面包括点歌 分享 直播 聊天 僵尸粉丝 管理 点亮 歌词等功能详细参照每个方法的注释
 * 本页面继承基类和观看直播属于同一父类
 */
public class StartLiveActivity extends ShowLiveActivityBase implements SearchMusicDialogFragment.SearchMusicFragmentInterface, UserInfoDialogFragment.IsAttentionListener {

    //渲染视频
    @InjectView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    //歌词显示控件
    @InjectView(R.id.lcv_live_start)
    LrcView mLrcView;

    @InjectView(R.id.iv_live_camera_control)
    ImageView mIvCameraControl;

    @InjectView(R.id.fl_bottom_menu)
    FrameLayout mFlBottomMenu;


    @InjectView(R.id.live_anchor_name)
    TextView liveAnchorName;

    @InjectView(R.id.rl_live_music)
    AutoLinearLayout mViewShowLiveMusicLrc;


    private String stream;

    //直播结束魅力值数量
    private int mLiveEndYpNum;

    private Timer mTimer;

    private TimerTask mTask;

    //是否开启直播
    private boolean IS_START_LIVE = true;

    public LiveStream mStreamer;

    private final static String TAG = "StartLiveActivity";

    private boolean mBeauty = false;

    private int mPlayTimerDuration = 1000;

    private int pauseTime = 0;

    private PopupWindow popupWindow;

    private MediaPlayer mPlayer;

    private boolean flashingLightOn;
//    private LiveReceiver liveReceiver;

    public static StartLiveActivity instance;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_show;
    }

    @Override
    public void initView() {
        super.initView();
        AppManager.getAppManager().addActivity(this);
        instance = this;
//        View lrcView = LayoutInflater.from(this).inflate(R.layout.view_live_music_lrc, null, false);
//        mViewShowLiveMusicLrc = (AutoLinearLayout) findViewById(R.id.rl_live_music);

        //防止聊天软键盘挤压屏幕
        mRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > oldBottom && InputMethodUtils.isShowSoft(StartLiveActivity.this)) {
                    hideEditText();
                }
            }
        });
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("pullblack");
//
//        liveReceiver = new LiveReceiver();
//        liveReceiver.setPullBlackListener(new PullBlackListener() {
//            @Override
//            public void pullblack() {
//                videoPlayerEnd();
//                showLiveEndDialog(mUser.getId(), mLiveEndYpNum);
//            }
//        });
//        BroadCastManager.getInstance().registerReceiver(this, liveReceiver, intentFilter);
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static StartLiveActivity getInstance() {
        return instance;
    }

    private boolean isFrontCameraMirro = false;

    @Override
    public void initData() {
        super.initData();

        mUser = AppContext.getInstance().getLoginUser();

        mRoomNum = mUser.getId();
        liveAnchorName.setText(mUser.getUser_nicename());
        mTvLiveNumber.setText("ID号:" + mUser.getId());
        stream = getIntent().getStringExtra("stream"); //HHH 2016-09-13
        isFrontCameraMirro = getIntent().getBooleanExtra("isFrontCameraMirro", false);
        //连接聊天服务器
        initChatConnection();
        initLivePlay();
    }

    /**
     * @dw 初始化连接聊天服务器
     */
    private void initChatConnection() {
        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), this, mUser.getId());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     * @dw 初始化直播播放器
     */
    private void initLivePlay() {

        //直播参数配置start
        KSYStreamerConfig.Builder builder = new KSYStreamerConfig.Builder();
        builder.setmUrl(AppConfig.RTMP_URL + stream + "?vhost=t.wanchuangzhongchou.com");//HHH 2016-09-09
        builder.setFrontCameraMirror(isFrontCameraMirro); //HHH 2016-09-13
        mStreamer = new LiveStream(this);
        mStreamer.setConfig(builder.build());
        mStreamer.setDisplayPreview(mCameraPreview);
        mStreamer.setOnStatusListener(mOnErrorListener);
        //默认美颜关闭
        //mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);
        //直播参数配置end
        mEmceeHead.setAvatarUrl(mUser.getAvatar());

        startAnimation(3);
    }

    //开始直播
    private void startLiveStream() {

        mStreamer.startStream();
        mStreamer.setEnableReverb(true);
        mStreamer.setReverbLevel(4);

        //连接到socket服务端
        mChatServer.connectSocketServer(mUser, mUser.getId());
        mLvChatList.setAdapter(mChatListAdapter);
    }

    //R.id.iv_live_flashing_light,R.id.iv_live_switch_camera,
    @OnClick({R.id.btn_live_sound, R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.ll_live_room_info, R.id.btn_live_end_music, R.id.iv_live_music, R.id.iv_live_meiyan, R.id.iv_live_camera_control, R.id.camera_preview, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.iv_live_chat, R.id.bt_send_chat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //音效
            case R.id.btn_live_sound:
                showSoundEffectsDialog();
                break;
            //展示主播信息弹窗
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(mUser, mUser, mRoomNum, StartLiveActivity.this);
                break;
            //展示主播信息弹窗
            case R.id.ll_live_room_info:
                showUserInfoDialog(mUser, mUser, mUser.getId(), StartLiveActivity.this);
                break;
            //展示点歌菜单
            case R.id.iv_live_music:
                showSearchMusicDialog();
                break;
            //美颜
            case R.id.iv_live_meiyan:
                if (!mBeauty) {
                    mBeauty = true;
                    mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);
                } else {
                    mBeauty = false;
                    mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DISABLE);
                }
                break;
            //摄像头反转
            /*
            case R.id.iv_live_switch_camera:
                mStreamer.switchCamera();
                break;
            */
            case R.id.iv_live_camera_control:
                showCameraControl(v);
                break;
            /*
            case R.id.iv_live_flashing_light:
                flashing_light_on =!flashing_light_on;
                mStreamer.toggleTorch(flashing_light_on);
                break;
             */
            //开启关闭弹幕
            case R.id.tglbtn_danmu_setting:
                mDanMuIsOpen = mDanMuIsOpen ? false : true;
                if (mDanMuIsOpen) {
                    mDanmuControl.show();
                    if (mChatInput.getText().toString().equals("")) {
                        mChatInput.setHint("开启大喇叭，" + barrageFee + "播币/条");
                    }

                } else {
                    mDanmuControl.hide();
                    mChatInput.setHint("");
                }
                mBtnDanMu.setBackgroundResource(mDanMuIsOpen ? R.drawable.tuanmubutton1 : R.drawable.tanmubutton);
                break;
            case R.id.camera_preview:
                hideEditText();
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            //主播点击退出
            case R.id.iv_live_back:
                clickBack();
                break;

            //魅力值排行榜
            case R.id.ll_yp_labe:
                showDedicateOrder();
                break;
            //聊天输入框
            case R.id.iv_live_chat://chat gone or visble
                showEditText();
                break;
            case R.id.bt_send_chat://send chat
                //sendChat();
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    sendChat();
                }
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.btn_live_end_music:
                stopMusic();
                break;
        }
    }


    private void showCameraControl(View v) {
        showPopUp(v);
    }

    private void showPopUp(View v) {
        View popView = getLayoutInflater().inflate(R.layout.pop_view_camera_control, null);
        LinearLayout llLiveCameraControl = (LinearLayout) popView.findViewById(R.id.ll_live_camera_control);
        llLiveCameraControl.measure(0, 0);
        int height = llLiveCameraControl.getMeasuredHeight();
        popView.findViewById(R.id.iv_live_flashing_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingLightOn = !flashingLightOn;
                mStreamer.toggleTorch(flashingLightOn);
            }
        });
        popView.findViewById(R.id.iv_live_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamer.switchCamera();
            }
        });
        popView.findViewById(R.id.iv_live_shar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSharePopWindow(StartLiveActivity.this, mIvCameraControl);
            }
        });
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - height);
    }


    //音效调教菜单
    private void showSoundEffectsDialog() {
        MusicPlayerDialogFragment musicPlayerDialogFragment = new MusicPlayerDialogFragment();
        musicPlayerDialogFragment.show(getSupportFragmentManager(), "MusicPlayerDialogFragment");
    }


    //打开魅力值排行
    private void showDedicateOrder() {
        DialogHelp.showPromptDialog(getLayoutInflater(), StartLiveActivity.this, "正在直播点击排行会影响直播,是否继续", new DialogInterface() {

            @Override
            public void cancelDialog(View v, Dialog d) {
                d.dismiss();
            }

            @Override
            public void determineDialog(View v, Dialog d) {
                d.dismiss();
                UIHelper.showDedicateOrderActivity(StartLiveActivity.this, mUser.getId());
            }
        });
    }

    /**
     * @dw 当每个聊天被点击显示该用户详细信息弹窗
     */
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() == 13) {
            return;
        } else {
            showUserInfoDialog(mUser, chat, mRoomNum, StartLiveActivity.this);
        }
    }

    /**
     * @dw 显示搜索音乐弹窗
     */
    private void showSearchMusicDialog() {

        SearchMusicDialogFragment musicFragment = new SearchMusicDialogFragment();
        musicFragment.setStyle(SearchMusicDialogFragment.STYLE_NO_TITLE, 0);
        musicFragment.show(getSupportFragmentManager(), "SearchMusicDialogFragment");
    }

    /**
     * @param toUser 被@用户
     * @dw @艾特用户
     */
    @Override
    public void dialogReply(UserBean toUser) {
        ACE_TEX_TO_USER = toUser.getId();
        mChatInput.setText("@" + toUser.getUser_nicename() + " ");
        mChatInput.setSelection(mChatInput.getText().length());
        showEditText();
    }

    //当主播选中了某一首歌,开始播放
    @Override
    public void onSelectMusic(Intent data) {
        startMusicStrem(data);
    }

    @Override
    protected void sendBarrageOnResponse(String response) {
        String s = ApiUtils.checkIsSuccess(response);
        if (s != null) {
            try {
                JSONObject tokenJson = new JSONObject(s);
                mChatServer.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                mChatInput.setText("");
                mChatInput.setHint("开启大喇叭，" + barrageFee + "播币/条");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void isAttention() {

    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ButterKnife.inject(this);
//    }

    //socket客户端事件监听处理
    private class ChatListenUIRefresh implements ChatServerInterface {

        @Override
        public void onMessageListen(final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (type == 1) {
                        addDanmu(c);
                    } else if (type == 2) {
                        addChatMessage(c);
                    }
                }
            });
        }

        @Override
        public void onConnect(final boolean res) {
            //连接结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectRes(res);
                }
            });
        }

        //用户列表
        @Override
        public void onUserList(List<UserBean> uList, String votes) {//用户列表
            mUserList = uList;
            if (mRvUserList != null) {
                mLiveNum.setText(ChatServer.LIVE_USER_NUMS + "观众");
                mYpNum.setText(votes);
                sortUserList();
            }
        }

        //用户状态改变
        @Override
        public void onUserStateChange(final UserBean user, final boolean state) {//用户状态改变
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        //系统通知
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {//后台关闭直播
                        DialogHelp.showPromptDialog(getLayoutInflater(), StartLiveActivity.this, "直播内容涉嫌违规", new DialogInterface() {

                            @Override
                            public void cancelDialog(View v, Dialog d) {

                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                d.dismiss();
                            }
                        });
                        videoPlayerEnd();
                        showLiveEndDialog(mUser.getId(), mLiveEndYpNum);
                    }
                }
            });

        }

        //送礼物
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {//送礼物展示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLiveEndYpNum += giftInfo.getTotalcoin();
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        //设置管理员
        @Override
        public void setManage(final JSONObject contentJson, final ChatBean chat) {//设置为管理员
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (contentJson.getInt("touid") == mUser.getId()) {
                            AppContext.showToastAppMsg(StartLiveActivity.this, "您已被设为管理员");
                        }
                        addChatMessage(chat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //特权动作
        @Override
        public void onPrivilegeAction(final ChatBean c, final JSONObject contentJson) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessage(c);
                }
            });

        }

        //点亮
        @Override
        public void onLit() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLit(mRandom.nextInt(4));
                }
            });

        }

        //添加僵尸粉
        @Override
        public void onAddZombieFans(final String ct) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addZombieFans(ct);
                }
            });

        }

        //服务器连接错误
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppContext.showToastAppMsg(StartLiveActivity.this, "服务器连接错误");
                }
            });
        }
    }


    //播放音乐
    private void startMusicStrem(Intent data) {

        //停止影月
        mStreamer.stopMixMusic();

        mViewShowLiveMusicLrc.setVisibility(View.VISIBLE);


        //获取音乐路径
        String musicPath = data.getStringExtra("filepath");

        //获取歌词字符串
        String lrcRes = LiveUtils.getFromFile(musicPath.substring(0, musicPath.length() - 3) + "lrc");
        MusicLrcBean lrcBean = GsonTools.instance(lrcRes, MusicLrcBean.class);
        String lrcStr = lrcBean.getShowapi_res_body().getLyric();
        KSYBgmPlayer mKsyBgmPlayer = KSYBgmPlayer.getInstance();
        mKsyBgmPlayer.setOnBgmPlayerListener(new KSYBgmPlayer.OnBgmPlayerListener() {
            @Override
            public void onCompleted() {
                TLog.log("音乐初始化完毕");
            }

            @Override
            public void onError(int i) {
                TLog.log("音乐初始化错误");
            }
        });

        mKsyBgmPlayer.setVolume(1);
        mStreamer.setBgmPlayer(mKsyBgmPlayer);
        mStreamer.startMixMusic(musicPath, true);
        mStreamer.setHeadsetPlugged(true);

        //插入耳机
//        mStreamer.setHeadsetPlugged(true);
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(musicPath);
            mPlayer.setLooping(true);
            mPlayer.setVolume(0, 0);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new LrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPlayTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }

        ILrcBuilder builder = new DefaultLrcBuilder();

        Spanned lrc;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lrc = Html.fromHtml(lrcStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            lrc = Html.fromHtml(lrcStr);
        }
        String temp = lrc.toString();
        List<LrcRow> rows = builder.getLrcRows(temp);
        //设置歌词
        mLrcView.setLrc(rows);
    }

    //停止歌词滚动
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //停止播放音乐
    private void stopMusic() {
        if (mPlayer != null && null != mStreamer) {
            mStreamer.stopMixMusic();
            mPlayer.stop();
            mViewShowLiveMusicLrc.setVisibility(View.GONE);
        }
    }

    private class LrcTask extends TimerTask {

        long beginTime = -1;

        @Override
        public void run() {
            if (beginTime == -1) {
                beginTime = System.currentTimeMillis();
            }

            if (null != mPlayer) {
                final long timePassed = mPlayer.getCurrentPosition();
                StartLiveActivity.this.runOnUiThread(new Runnable() {

                    public void run() {
                        mLrcView.seekLrcToTime(timePassed);
                    }
                });
            }

        }
    }

    ;

    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!IS_START_LIVE)) {
                return super.onKeyDown(keyCode, event);
            } else {
                clickBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // 判断权限请求是否通过
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    showSoundEffectsDialog();
                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您拒绝写入文件权限,无法保存歌曲,请到设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您拒绝读取文件权限,无法读取歌曲,请到设置中修改", 0);
                }
                break;
            }
        }
    }

    //主播点击退出
    private void clickBack() {

        DialogHelp.showIOSStyleDialog(this, getString(R.string.iscloselive), new DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {
                d.dismiss();
            }

            @Override
            public void determineDialog(View v, Dialog d) {
                videoPlayerEnd();
                showLiveEndDialog(mUser.getId(), mLiveEndYpNum);
                d.dismiss();
            }
        });
//        DialogHelp.getConfirmDialog(this, getString(R.string.iscloselive), new android.content.DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(android.content.DialogInterface dialogInterface, int i) {
//
//            }
//        }).show();
    }

    //关闭直播
    public void videoPlayerEnd() {
        IS_START_LIVE = false;

        mGiftShowQueue.clear();
        mLuxuryGiftShowQueue.clear();
        mChats.clear();

        mDanMuIsOpen = false;
        mBtnDanMu.setBackgroundResource(R.drawable.tanmubutton);

        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }
        mShowGiftAnimator.removeAllViews();

        //请求接口改变直播状态
        PhoneLiveApi.closeLive(mUser.getId(), mUser.getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //showToast3("关闭直播失败" ,0);
            }

            @Override
            public void onResponse(String response) {

            }
        });
        mChatServer.closeLive();
        //停止直播
        mStreamer.stopStream();
        //停止播放音乐
        stopMusic();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mLvChatList.setVisibility(View.GONE);
        mDanmuControl.hide();//关闭弹幕

        mLiveChatEdit.setVisibility(View.VISIBLE);

        mButtonMenuFrame.setVisibility(View.GONE);
        mCameraPreview.setBackgroundResource(R.drawable.create_room_bg);


    }


    //直播错误监听
    public OnStatusListener mOnErrorListener = new OnStatusListener() {
        @Override
        public void onStatus(int what, int arg1, int arg2, String msg) {
            // msg may be null
            switch (what) {
                case RecorderConstants.KSYVIDEO_OPEN_STREAM_SUCC:
                    // 推流成功

                    break;
                case RecorderConstants.KSYVIDEO_ENCODED_FRAMES_FAILED:
                    //编码失败

                    break;
                case RecorderConstants.KSYVIDEO_FRAME_DATA_SEND_SLOW:
                    //网络状况不佳
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        TLog.log("网络状况不佳:非主线程");
                    } else {
                        TLog.log("网络状况不佳:主线程");
                    }
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_DROP:
                    break;
                case RecorderConstants.KSYVIDEO_EST_BW_RAISE:
                    break;
                case RecorderConstants.KSYVIDEO_AUDIO_INIT_FAILED:

                    break;
                //推流失败
                case RecorderConstants.KSYVIDEO_CODEC_OPEN_FAILED:

                    break;
                //推流失败
                case RecorderConstants.KSYVIDEO_CODEC_GUESS_FORMAT_FAILED:

                    break;
                //推流失败
                case RecorderConstants.KSYVIDEO_CONNECT_FAILED:

                    break;
                case RecorderConstants.KSYVIDEO_INIT_DONE:

                    break;
                //推流过程中断开网络
                case RecorderConstants.KSYVIDEO_CONNECT_BREAK:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast3("网络断开连接,请检查网络设置", 0);
                        }
                    });
                    break;
                default:
                    TLog.log("可以在这里处理断网重连的逻辑");
                    if (msg != null) {
                        // 可以在这里处理断网重连的逻辑
                    }
                    if (mHandler != null) {
                        mHandler.obtainMessage(what, msg).sendToTarget();
                    }
            }
        }

    };

    public static void startLiveActivity(Context context, String stream, boolean isFrontCameraMirro) { //HHH 2016-09-13
        Intent intent = new Intent(context, StartLiveActivity.class);
        intent.putExtra("stream", stream);
        intent.putExtra("isFrontCameraMirro", isFrontCameraMirro);
        context.startActivity(intent);

    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("直播"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);

        if (mStreamer != null) {
            mStreamer.onPause();
        }


        if (IS_START_LIVE && mHandler != null) {
            mHandler.postDelayed(pauseRunnable, 1000);
        }

        //提示
        mChatServer.doSendSystemMessage("主播暂时离开一下,马上回来!", mUser);

    }

    //定时10秒钟,如果主播未能如约而来,提示结束主播
    private Runnable pauseRunnable = new Runnable() {
        @Override
        public void run() {
            pauseTime++;
            if (pauseTime >= 10) {
                mHandler.removeCallbacks(this);
                return;
            }
            TLog.log(pauseTime + "定时器");
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("直播"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长

        if (mStreamer != null) {
            mStreamer.onResume();
        }
        //重置时间,如果超过预期则关闭直播

        if (pauseTime >= 10) {
            videoPlayerEnd();
            showLiveEndDialog(mUser.getId(), mLiveEndYpNum);
        } else {
            mHandler.removeCallbacks(pauseRunnable);
        }
        pauseTime = 0;

    }


    @Override
    protected void onStart() {
        super.onStart();

        startDate = new Date();

    }

    @Override
    protected void onDestroy() {
        videoPlayerEnd();
        mChatServer.close();
        if (mStreamer != null) {
            mStreamer.stopStream();
            stopLrcPlay();
            mStreamer.stopMixMusic();
            mStreamer.onDestroy();
        }
        super.onDestroy();
        ButterKnife.reset(this);
        //OkHttpUtils.getInstance().cancelTag("closeLive");
        //endDate= new Date();
    }


    /**
     * @param num 倒数时间
     * @dw 开始直播倒数计时
     */
    private void startAnimation(final int num) {
        final TextView tvNum = new TextView(this);
        tvNum.setTextColor(getResources().getColor(R.color.white));
        tvNum.setText(num + "");
        tvNum.setTextSize(30);
        mRoot.addView(tvNum);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNum.setLayoutParams(params);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvNum, "scaleX", 5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvNum, "scaleY", 5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRoot == null) return;
                mRoot.removeView(tvNum);
                if (num == 1) {
                    startLiveStream();
                    return;
                }
                startAnimation(num == 3 ? 2 : 1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();

    }

    public void share(View v) {
        ShareUtils.share(this, v.getId(), mUser);
    }
//
//    public class LiveReceiver extends BroadcastReceiver {
//
////    private PullBlackListener pullBlackListener;
////
////    public void setPullBlackListener(PullBlackListener pullBlackListener) {
////        this.pullBlackListener = pullBlackListener;
////    }
//
//
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals("pullblack")) {
//
//                videoPlayerEnd();
//
//                Bundle bundle = intent.getExtras();
//                bundle.putBoolean("out", true);
//                setResultExtras(bundle);
//                TLog.log("[MyReceiver]直播间");
//            }
//
//        }
//
//
//    }

}
