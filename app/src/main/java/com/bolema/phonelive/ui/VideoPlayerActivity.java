package com.bolema.phonelive.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.GridViewAdapter;
import com.bolema.phonelive.adapter.ViewPageGridViewAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ShowLiveActivityBase;
import com.bolema.phonelive.bean.ChatBean;
import com.bolema.phonelive.bean.GiftBean;
import com.bolema.phonelive.bean.SendGiftBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.fragment.UserInfoDialogFragment;
import com.bolema.phonelive.interf.ChatServerInterface;
import com.bolema.phonelive.ui.other.ChatServer;
import com.bolema.phonelive.utils.DialogHelp;
import com.bolema.phonelive.utils.QosThread;
import com.bolema.phonelive.utils.ShareUtils;
import com.bolema.phonelive.utils.SharedPreUtil;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.bolema.phonelive.widget.VideoSurfaceView;
import com.google.gson.Gson;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.socks.library.KLog;
import com.tandong.bottomview.view.BottomView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import okhttp3.Call;

/*

* 直播播放页面
* */
public class VideoPlayerActivity extends ShowLiveActivityBase implements View.OnLayoutChangeListener, UserInfoDialogFragment.IsAttentionListener {

    public final static String USER_INFO = "USER_INFO";
    @InjectView(R.id.video_view)
    VideoSurfaceView mVideoSurfaceView;

    @InjectView(R.id.view_live_content)
    RelativeLayout mLiveContent;

    //加载中的背景图
    @InjectView(R.id.iv_live_look_loading_bg)
    LoadUrlImageView mIvLoadingBg;

    @InjectView(R.id.iv_live_look_loading_pre)
    LoadUrlImageView mIvLoadingPre;

    @InjectView(R.id.iv_live_look_loading_next)
    LoadUrlImageView mIvLoadingNext;

    //@InjectView(R.id.iv_attention)
    ImageView mIvAttention;

    private static final String TAG = "VideoPlayerActivity";

    public static final int UPDATE_QOS = 2;
    @InjectView(R.id.live_anchor_name)
    TextView liveAnchorName;
    @InjectView(R.id.btn_i_know)
    Button btnIKnow;
    @InjectView(R.id.layout_first_note)
    AutoLinearLayout layoutFirstNote;

    private KSYMediaPlayer ksyMediaPlayer;

    private QosThread mQosThread;

    private Surface mSurface = null;

    //private boolean mPause = false;

    //视频流宽度
    private int mVideoWidth = 0;

    //视频流高度
    private int mVideoHeight = 0;

    private List<GiftBean> mGiftList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpGiftAdapter;

    //礼物view
    private ViewPager mVpGiftView;

    //礼物服务端返回数据
    private String mGiftResStr;

    //当前选中的礼物
    private GiftBean mSelectedGiftItem;

    //赠送礼物按钮
    private Button mSendGiftBtn;

    private int mShowGiftSendOutTime = 5;

    private RelativeLayout mSendGiftLian;

    private TextView mUserCoin;

    //主播信息
    private UserBean mEmceeInfo;

    private BottomView mGiftSelectView;

    //是否是禁言状态
    private boolean mIsShutUp = false;

    private long mLitLastTime = 0;

    private View mLoadingView;

    private float lastX;

    private int lastY;

    private String mrl;

    SurfaceHolder mHolder;

    int dy; //HHH 2016-09-13
    DisplayMetrics displayMetrics;

    boolean isFirsterScroll = true;

    boolean upOrDown = true;

    float minimumVelocity;
    private AutoRelativeLayout autoRelativeLayout;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_look;
    }

    @Override
    public void initView() {
        super.initView();
        AppManager.getAppManager().addActivity(this);
        mLiveChat.setVisibility(View.VISIBLE);
        //startLoadingAnimation();
        mVideoSurfaceView.addOnLayoutChangeListener(this);
        mRoot.addOnLayoutChangeListener(this);

        displayMetrics = getResources().getDisplayMetrics(); //HHH 2016-09-13

        mIvLoadingPre.setY(-displayMetrics.heightPixels);

        mIvLoadingNext.setY(displayMetrics.heightPixels);

        SurfaceHolder mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mVideoSurfaceView.setOnTouchListener(mTouchListener);
        mVideoSurfaceView.setKeepScreenOn(true);
        mIvAttention = (ImageView) findViewById(R.id.iv_attention);
        mDanmuControl.show();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (v.getId() == R.id.video_view) {
            if (bottom != 0) {
                //防止聊天软键盘挤压屏幕导致视频变形
                //mVideoSurfaceView.setVideoDimension(mScreenWidth,mScreenHeight);
            }
        } else if (v.getId() == R.id.rl_live_root) {
            if (bottom > oldBottom) {
                //如果聊天窗口开启,收起软键盘时关闭聊天输入框
                hideEditText();
            }
        }

    }

    @Override
    public void initData() {
        super.initData();
        mGson = new Gson();
        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        //获取用户登陆信息
        mUser = AppContext.getInstance().getLoginUser();
        mEmceeInfo = bundle.getParcelable("USER_INFO");

        liveAnchorName.setText(mEmceeInfo.getUser_nicename());

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mUserListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) {

                    try {
                        if (mIvLoadingPre.getY() == 0) {

                            mIvLoadingPre.setY(-displayMetrics.heightPixels);

                        } else if (mIvLoadingNext.getY() == 0) {
                            mIvLoadingNext.setY(displayMetrics.heightPixels);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
                super.handleMessage(msg);
            }
        };


        initRoomInfo();
        ViewConfiguration configuration = ViewConfiguration.get(this);
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }


    private void initRoomInfo() {
        //mDanmuControl.show();
        //设置背景图
        mIvLoadingBg.setVisibility(View.VISIBLE);
        mIvLoadingBg.setNull_drawable(R.drawable.create_room_bg);
        mIvLoadingBg.setImageLoadUrl(mEmceeInfo.getAvatar());

        mRoomNum = mEmceeInfo.getId();
        mTvLiveNumber.setText("ID号:" + mEmceeInfo.getId() + "");
        mEmceeHead.setAvatarUrl(mEmceeInfo.getAvatar());
        //初始化直播播放器参数配置
        //视频流播放地址
        //  mrl = AppConfig.RTMP_URL2 + mEmceeInfo.getStream();
        mrl = AppConfig.RTMP_URL2 + mEmceeInfo.getStream();
        //mDanmuControl.show();
        requestIsFollow();
        initLive();
        getRoomInfo();
    }

    private void initLive() { //HHH 2016-09-13

        //视频播放器init
        ksyMediaPlayer = new KSYMediaPlayer.Builder(this).build();
        if (ksyMediaPlayer != null && mHolder != null) {
            final Surface newSurface = mHolder.getSurface();
            ksyMediaPlayer.setDisplay(mHolder);
            ksyMediaPlayer.setScreenOnWhilePlaying(true);
            //设置视频缩放模式
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (mSurface != newSurface) {
                mSurface = newSurface;
                ksyMediaPlayer.setSurface(mSurface);
            }
        }
        ksyMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);
        ksyMediaPlayer.setBufferTimeMax(5);
        try {
            ksyMediaPlayer.setDataSource(mrl);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @dw 获取房间信息
     */
    private void getRoomInfo() {

        //请求服务端获取房间基本信息
        PhoneLiveApi.initRoomInfo(AppContext.getInstance().getLoginUid()
                , mEmceeInfo.getId()
                , AppContext.getInstance().getToken()
                , AppContext.address
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        AppContext.showToastAppMsg(VideoPlayerActivity.this, getString(R.string.initDataError));
                    }

                    @Override
                    public void onResponse(String s) {
                        String res = ApiUtils.checkIsSuccess(s);

                        KLog.json(res);

                        if (res != null) {
                            UserBean u = mGson.fromJson(res, UserBean.class);
                            mUser.setCoin(u.getCoin());
                            mUser.setLevel(u.getLevel());

                            fillUI(res);
                        }
                    }
                });
        //禁言状态初始化
        PhoneLiveApi.isShutUp(mUser.getId(), mEmceeInfo.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res && res.equals("1")) {
                    mIsShutUp = true;
                }
            }
        });
        //获取礼物列表
        PhoneLiveApi.getGiftList(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(VideoPlayerActivity.this, "获取礼物信息失败");
            }

            @Override
            public void onResponse(String s) {
                mGiftResStr = ApiUtils.checkIsSuccess(s);
            }
        });

//        mChatServer.doSendMsg();


    }


    @OnClick({R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.iv_live_shar, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.ll_live_room_info, R.id.iv_live_chat, R.id.iv_live_look_loading_bg, R.id.bt_send_chat, R.id.iv_live_gift, R.id.iv_attention})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(mUser, mEmceeInfo, mRoomNum, VideoPlayerActivity.this);
                break;
            case R.id.iv_live_shar:
                ShareUtils.showSharePopWindow(this, v);
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            //退出直播间
            case R.id.iv_live_back:
                View mDialogView = View.inflate(this, R.layout.dialog_show_own_info_detail, null);
                final Dialog dialog = new Dialog(this, R.style.dialog);
                dialog.setContentView(mDialogView);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                mDialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                mDialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            //票数排行榜
            case R.id.ll_yp_labe:
                UIHelper.showDedicateOrderActivity(this, mEmceeInfo.getId());
                break;
            //发言框
            case R.id.iv_live_chat:
                if (mIsShutUp) {
                    isShutUp();
                } else {
                    showEditText();
                }
                break;
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
            case R.id.bt_send_chat:
                //弹幕判断 HHH
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    sendChat();
                }
                break;
            case R.id.iv_live_look_loading_bg:
                hideEditText();
                break;
            case R.id.iv_live_gift:
                showGiftList();
                break;
            case R.id.ll_live_room_info://左上角点击主播信息
                showUserInfoDialog(mUser, mEmceeInfo, mRoomNum, VideoPlayerActivity.this);
                break;
            case R.id.iv_attention:
                followEmcee();
                break;
        }
    }

    //分享操作
    public void share(View v) {
        ShareUtils.share(this, v.getId(), mEmceeInfo, new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //分享成功
                mChatServer.doSendShareEvent(mUser, 0);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        },0);

    }


    //连送按钮隐藏
    private void recoverySendGiftBtnLayout() {
        ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText("");
        mSendGiftLian.setVisibility(View.GONE);
        mSendGiftBtn.setVisibility(View.VISIBLE);
        autoRelativeLayout.setVisibility(View.VISIBLE);
        mShowGiftSendOutTime = 5;
    }

    //展示礼物列表
    private void showGiftList() {
        if (mYpNum == null) {
            return;
        }
        mGiftSelectView = new BottomView(this, R.style.BottomViewTheme_Transparent, R.layout.view_show_viewpager);
        mGiftSelectView.setAnimation(R.style.BottomToTopAnim);
        mGiftSelectView.showBottomView(true);
        View mGiftView = mGiftSelectView.getView();
        mUserCoin = (TextView) mGiftView.findViewById(R.id.tv_show_select_user_coin);
        mUserCoin.setText(mUser.getCoin());
        //点击底部跳转充值页面
        mGiftView.findViewById(R.id.rl_show_gift_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("diamonds", mUser.getCoin());
                UIHelper.showMyDiamonds(VideoPlayerActivity.this, bundle);
            }
        });
        mVpGiftView = (ViewPager) mGiftView.findViewById(R.id.vp_gift_page);
        mSendGiftLian = (RelativeLayout) mGiftView.findViewById(R.id.iv_show_send_gift_lian);
        mSendGiftLian.bringToFront();
        mSendGiftLian.setOnClickListener(new View.OnClickListener() {//礼物连送
            @Override
            public void onClick(View v) {
                sendGift("y");//礼物发送
                mShowGiftSendOutTime = 5;
                ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
            }
        });
        mSendGiftBtn = (Button) mGiftView.findViewById(R.id.btn_show_send_gift);
        autoRelativeLayout = (AutoRelativeLayout) mGiftView.findViewById(R.id.layout_show_send_gift);
        autoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendGift(v);
            }
        });
        mSendGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendGift(v);
            }
        });
        //表示已经请求过数据不再向下执行
        if (mGiftViews != null) {
            fillGift();
            return;
        }

    }

    /**
     * @param v btn
     * @dw 点击赠送礼物按钮
     */
    private void onClickSendGift(View v) {//赠送礼物
        if (!mConnectionState) {//没有连接ok
            return;
        }
        if ((mSelectedGiftItem != null) && (mSelectedGiftItem.getType() == 1)) {//连送礼物
            v.setVisibility(View.GONE);
            if (mHandler == null) return;
            mHandler.postDelayed(new Runnable() {//开启连送定时器
                @Override
                public void run() {
                    if (mShowGiftSendOutTime == 1) {
                        recoverySendGiftBtnLayout();
                        mHandler.removeCallbacks(this);
                        return;
                    }
                    mHandler.postDelayed(this, 1000);
                    mShowGiftSendOutTime--;
                    ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
                }
            }, 1000);
            sendGift("y");//礼物发送
        } else {
            sendGift("n");//礼物发送
        }
    }

    //礼物列表填充
    private void fillGift() {
        if (null == mVpGiftAdapter && null != mGiftResStr) {
            if (mGiftList.size() == 0) {
                try {
                    JSONArray giftListJson = new JSONArray(mGiftResStr);
                    for (int i = 0; i < giftListJson.length(); i++) {

                        mGiftList.add(mGson.fromJson(giftListJson.getString(i), GiftBean.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //礼物item填充
            List<View> mGiftViewList = new ArrayList<>();
            int index = 0;

            int giftsPageSize;

            if (mGiftList.size() % 8 == 0) {
                giftsPageSize = mGiftList.size() / 8;
            } else {
                giftsPageSize = (int) (mGiftList.size() / 8) + 1;
            }

            for (int i = 0; i < giftsPageSize; i++) {
                View v = getLayoutInflater().inflate(R.layout.view_show_gifts_gv, null);
                mGiftViewList.add(v);
                List<GiftBean> l = new ArrayList<>();
                for (int j = 0; j < 8; j++) {
                    if (index >= mGiftList.size()) {
                        break;
                    }
                    l.add(mGiftList.get(index));
                    index++;
                }
                mGiftViews.add((GridView) v.findViewById(R.id.gv_gift_list));
                mGiftViews.get(i).setAdapter(new GridViewAdapter(l));
                mGiftViews.get(i).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        giftItemClick(parent, view, position);
                    }
                });
            }
            mVpGiftAdapter = new ViewPageGridViewAdapter(mGiftViewList);
        }
        mVpGiftView.setAdapter(mVpGiftAdapter);

        //mVpGiftView.setCurrentItem(0);
    }

    //赠送礼物单项被选中
    private void giftItemClick(AdapterView<?> parent, View view, int position) {
        if ((GiftBean) parent.getItemAtPosition(position) != mSelectedGiftItem) {
            recoverySendGiftBtnLayout();
            mSelectedGiftItem = (GiftBean) parent.getItemAtPosition(position);
            //点击其他礼物
//            changeSendGiftBtnStatue(true);
            for (int i = 0; i < mGiftViews.size(); i++) {
                for (int j = 0; j < mGiftViews.get(i).getChildCount(); j++) {
                    if (((GiftBean) mGiftViews.get(i).getItemAtPosition(j)).getType() == 1) {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
                    } else {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
                    }
                }
            }
            view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift_chosen);

        } else {
            if (((GiftBean) parent.getItemAtPosition(position)).getType() == 1) {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
            } else {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
            }
            mSelectedGiftItem = null;
//            changeSendGiftBtnStatue(false);
        }
    }

    /**
     * @param statue 开启or关闭
     * @dw 赠送礼物按钮状态修改
     */
    private void changeSendGiftBtnStatue(boolean statue) {
//        if (statue) {
//            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.global));
//            mSendGiftBtn.setEnabled(true);
//        } else {
//            mSendGiftBtn.setBackgroundColor(getResources().getColor(R.color.light_gray2));
//            mSendGiftBtn.setEnabled(false);
//        }
    }

    /**
     * @param isOutTime 是否连送超时(如果是连送礼物的情况下)
     * @dw 赠送礼物, 请求服务端获取数据扣费
     */
    private void sendGift(final String isOutTime) {
        if (mSelectedGiftItem != null) {
            if (mSelectedGiftItem.getType() == 1) {
                mSendGiftLian.setVisibility(View.VISIBLE);
            } else {
//                changeSendGiftBtnStatue(true);
            }
            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    AppContext.showToastAppMsg(VideoPlayerActivity.this, getString(R.string.senderror));
                }

                @Override
                public void onResponse(String response) {
                    String s = ApiUtils.checkIsSuccess(response);
                    if (s != null) {
                        try {
                            ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
                            JSONObject tokenJson = new JSONObject(s);
                            //获取剩余金额,重新赋值
                            mUser.setCoin(tokenJson.getString("coin"));
                            mUserCoin.setText(mUser.getCoin());//重置余额
                            mUser.setLevel(tokenJson.getInt("level"));
                            if (tokenJson.getString("win_type").equals("1")) {
                                Toast.makeText(VideoPlayerActivity.this, tokenJson.getString("win_msg"), Toast.LENGTH_SHORT).show();
                            }
                            mChatServer.doSendGift(tokenJson.getString("gifttoken"), mUser, isOutTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            PhoneLiveApi.sendGift(mUser, mSelectedGiftItem, mEmceeInfo.getId(), callback);
        }
    }

    //弹幕发送
    @Override
    protected void sendBarrageOnResponse(String response) {
        String s = ApiUtils.checkIsSuccess(response);
        if (s != null) {
            try {
                JSONObject tokenJson = new JSONObject(s);
                mUser.setCoin(tokenJson.getString("coin"));
                //mUserCoin.setText(mUser.getCoin());
                mUser.setLevel(tokenJson.getInt("level"));
                mChatServer.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                mChatInput.setText("");
                mChatInput.setHint("开启大喇叭，" + barrageFee + "播币/条");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //更新ui
    private void fillUI(String res) {
        if (mLvChatList != null && mChatListAdapter != null) {
            mLvChatList.setAdapter(mChatListAdapter);
        }

        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), this, mEmceeInfo.getId());
            mChatServer.connectSocketServer(res, AppContext.getInstance().getToken(), mEmceeInfo.getId());//连接到socket服务端
//            mChatServer.doSendSystemMessage("huanying", AppContext.getInstance().getLoginUser());
            //请求僵尸粉丝
            mChatServer.getZombieFans();
//            UserBean userBean = AppContext.getInstance().getLoginUser();
//            int isManage = userBean.getIsmanage();

//            if (!vip_type.equals("1")) {
//                mChatServer.doSendSystemMessage("欢迎"+userBean.getUser_nicename()+"进入房间",userBean);
//            }

//            if (isManage==1) {
//                mChatServer.doSendSystemMessage("15895464124", userBean);
//            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            TLog.log("connect error");
        }
        boolean isFirstInstall = SharedPreUtil.getBoolean(this, "isFirstInstall");
        if (isFirstInstall) {
            layoutFirstNote.setVisibility(View.VISIBLE);
            btnIKnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutFirstNote.setVisibility(View.GONE);
                    SharedPreUtil.put(VideoPlayerActivity.this, "isFirstInstall", false);
                }
            });
        } else {
            try {
//                if (layoutFirstNote == null) {
//                    layoutFirstNote = (AutoLinearLayout) findViewById(R.id.layout_first_note);
//                }
                layoutFirstNote.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void isAttention() {
        mIvAttention.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    //socket客户端事件监听处理start
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
        public void onUserStateChange(final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        //主播关闭直播
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {
                        DialogHelp.getMessageDialog(VideoPlayerActivity.this, "直播内容涉嫌违规").show();
                    }
                    showLiveEndDialog(mUser.getId(), 0);
                    videoPlayerEnd();
                }
            });

        }

        //送礼物展示
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        //设置为管理员
        @Override
        public void setManage(final JSONObject contentJson, final ChatBean chat) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (contentJson.getInt("touid") == mUser.getId()) {
                            //AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被设为管理员");
                        }
                         addChatMessage(chat);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //特权操作
        @Override
        public void onPrivilegeAction(final ChatBean c, final JSONObject contentJson) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (contentJson.has("touid") && contentJson.getInt("touid") == mUser.getId()) {
                            //AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被禁言");
                            mIsShutUp = true;
                            hideEditText();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    showLit(mRandom.nextInt(3));
                }
            });
        }

        //添加僵尸粉丝
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
                    AppContext.showToastAppMsg(VideoPlayerActivity.this, "服务器连接错误");
                }
            });
        }
    }

    //socket客户端事件监听处理end

    public void dialogReply(UserBean toUser) {
        if (mIsShutUp) {
            isShutUp();
        } else {
            ACE_TEX_TO_USER = toUser.getId();
            mChatInput.setText("@" + toUser.getUser_nicename() + " ");
            mChatInput.setSelection(mChatInput.getText().length());
            showEditText();
        }

    }


    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            //直播开始
            if (null != mLoadingView) {
                mRoot.removeView(mLoadingView);
                mLoadingView = null;
            }
            mIvLoadingBg.setVisibility(View.GONE);

            ksyMediaPlayer.start();
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            long duration = ksyMediaPlayer.getDuration();
            long progress = duration * percent / 100;
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if (mVideoSurfaceView != null) {
                        mVideoSurfaceView.setVideoDimension(mVideoWidth, mVideoHeight);
                        mVideoSurfaceView.requestLayout();
                    }
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            //获取主播关注状态
            //showLiveEndDialog(mUser.getId(),0);
            //videoPlayerEnd();
        }
    };
    //错误异常监听
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "OnErrorListener, Error Unknown:" + what + ",extra:" + extra);
                    break;
                default:
                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra);
            }

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "onInfo, what:" + i + ",extra:" + i1);
            return false;
        }
    };

    //直播结束释放资源
    private void videoPlayerEnd() {

        mShowGiftAnimator.removeAllViews();

        if (mGiftSelectView != null) {
            mGiftSelectView.dismissBottomView();
        }

        mButtonMenuFrame.setVisibility(View.GONE);//隐藏菜单栏
        mLvChatList.setVisibility(View.GONE);
        mVideoSurfaceView.setBackgroundResource(R.drawable.create_room_bg);

        if (mChatServer != null) {
            mChatServer.close();
        }

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mQosThread != null) {
            mQosThread.stopThread();
            mQosThread = null;
        }

        mUserListHandler = null;

        mDanmuControl.hide();//关闭弹幕 HHH

    }

    private void switchRoomRelease() {
        mLitLastTime = 0;
        mGiftShowQueue.clear();
        mLuxuryGiftShowQueue.clear();
        mChats.clear();
        mShowGiftAnimator.removeAllViews();
        mDanMuIsOpen = false;
        mBtnDanMu.setBackgroundResource(R.drawable.tanmubutton);
        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }
        if (mGiftSelectView != null) {
            mGiftSelectView.dismissBottomView();
        }
        mDanmuControl.hide();
        if (mChatServer != null) {
            mChatServer.close();
        }
        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mQosThread != null) {
            mQosThread.stopThread();
            mQosThread = null;
        }
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideEditText();
            return false;
        }
    };

    /**
     * 滑动页面切换直播间更改直播间信息
     */
    private Runnable endRunnable = new Runnable() {
        @Override
        public void run() {

            if (mIvLoadingPre.getY() == 0) {

                if (DataSingleton.getInstance().getPostion() > 0) {

                    int position = DataSingleton.getInstance().getPostion() - 1;
                    DataSingleton.getInstance().setPostion(position);
                    mEmceeInfo = DataSingleton.getInstance().getUserList().get(position);
                    String name = mEmceeInfo.getUser_nicename();
                    liveAnchorName.setText(name);
                    //switchRoomRelease();
                    initRoomInfo();

                }


            } else if (mIvLoadingNext.getY() == 0) {

                if (DataSingleton.getInstance().getUserList() != null && DataSingleton.getInstance().getPostion() < DataSingleton.getInstance().getUserList().size() - 1) {
                    int position = DataSingleton.getInstance().getPostion() + 1;
                    DataSingleton.getInstance().setPostion(position);
                    mEmceeInfo = DataSingleton.getInstance().getUserList().get(position);
                    //switchRoomRelease();
                    String name = mEmceeInfo.getUser_nicename();
                    liveAnchorName.setText(name);
                    initRoomInfo();

                }

            }
            isFirsterScroll = true;
            upOrDown = false;
        }
    };

    //获取当前用户是否被禁言
    private void isShutUp() {
        if (mIsShutUp) {
            PhoneLiveApi.isShutUp(mUser.getId(), mEmceeInfo.getId(),
                    new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(String response) {
                            String res = ApiUtils.checkIsSuccess(response);

                            if (res == null) return;
                            if (Integer.parseInt(res) == 0) {
                                mIsShutUp = false;
                                showEditText();
                            } else {
                                AppContext.showToastAppMsg(VideoPlayerActivity.this, "您已被禁言");
                            }
                        }
                    });
        }
    }

    private final Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mHolder = holder;
            if (ksyMediaPlayer != null) {
                final Surface newSurface = holder.getSurface();
                ksyMediaPlayer.setDisplay(holder);
                ksyMediaPlayer.setScreenOnWhilePlaying(true);
                //设置视频缩放模式
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                    ksyMediaPlayer.setSurface(mSurface);
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            if (ksyMediaPlayer != null) {
                mSurface = null;
            }
        }
    };

    /**
     * @dw 当每个聊天被点击显示该用户详细信息弹窗
     */
//    public void chatListItemClick(ChatBean chat) {
//        if (chat.getType()==13) {
//            return;
//        }else{
//            showUserInfoDialog(mUser, chat, mUser.getId(),VideoPlayerActivity.this);
//        }
//    }
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() == 13) {
        } else {
            showUserInfoDialog(mUser, chat, mRoomNum, VideoPlayerActivity.this);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        doSwitchRoom(ev);
        //mChatListAdapter.notifyDataSetChanged();
        return super.dispatchTouchEvent(ev);
    }

    private boolean isLeftOrRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //按下并且当前屏幕不是清屏状态下
        if (event.getAction() == MotionEvent.ACTION_DOWN && !(mLiveContent.getLeft() > 10)) {
            int index = mRandom.nextInt(3);
            if (mLitLastTime == 0 || (System.currentTimeMillis() - mLitLastTime) > 500) {
                if (mLitLastTime == 0) {
                    //第一次点亮请求服务端纪录
                    PhoneLiveApi.showLit(mUser.getId(), mUser.getToken(), mEmceeInfo.getId());
                    mChatServer.doSendLitMsg(mUser, index);
                }
                mLitLastTime = System.currentTimeMillis();
                mChatServer.doSendLit(index);
            } else {
                showLit(mRandom.nextInt(3));
            }

        }
        //屏幕侧滑隐藏页面功能
        float rowX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = rowX;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = rowX - lastX;

                isLeftOrRight = offsetX <= 0;
                TLog.log(minimumVelocity + "");

                if (Math.abs(offsetX) > 10) {
                    mLiveContent.setX(mLiveContent.getX() + offsetX);
                }
                lastX = rowX;
                break;
            case MotionEvent.ACTION_UP:
                if (isLeftOrRight) {
                    if (mLiveContent.getX() < displayMetrics.widthPixels / 2) {//如果left>当前屏幕宽度/2 则将该内容view隐藏
                        mLiveContent.setX(0);
                    } else {
                        mLiveContent.setX(displayMetrics.widthPixels);
                    }
                } else {
                    if (mLiveContent.getX() > displayMetrics.widthPixels / 2) {//如果left>当前屏幕宽度/2 则将该内容view隐藏
                        mLiveContent.setX(displayMetrics.widthPixels);
                    } else {
                        mLiveContent.setX(0);
                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    String avatarUrl;
    private View.OnTouchListener switchRoomListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //doSwitchRoom(event);
            return false;
        }
    };

    private void doSwitchRoom(MotionEvent event) {
        int ex = (int) event.getRawX();
        int ey = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dy = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int my = ey - dy;
                // Log.i("weipeng",my + "");
                if (my > 0 && isFirsterScroll) {
                    upOrDown = true;
                    isFirsterScroll = false;
                } else if (my < 0 && isFirsterScroll) {
                    upOrDown = false;
                    isFirsterScroll = false;
                }

                if (upOrDown) {
                    if (DataSingleton.getInstance().getPostion() > 0) {
                        avatarUrl = DataSingleton.getInstance().getUserList().get(DataSingleton.getInstance().getPostion() - 1).getAvatar();
                        mIvLoadingPre.setNull_drawable(R.drawable.create_room_bg);
                        mIvLoadingPre.setImageLoadUrl(avatarUrl);
                        mIvLoadingPre.setTranslationY(mIvLoadingPre.getY() + my);

                    }

                } else {
                    if (DataSingleton.getInstance().getPostion() < DataSingleton.getInstance().getUserList().size() - 1) {
                        avatarUrl = DataSingleton.getInstance().getUserList().get(DataSingleton.getInstance().getPostion() + 1).getAvatar();
                        mIvLoadingNext.setNull_drawable(R.drawable.create_room_bg);
                        mIvLoadingNext.setImageLoadUrl(avatarUrl);
                        mIvLoadingNext.setTranslationY(mIvLoadingNext.getY() + my);


                    }
                }
                dy = ey;
                break;

            case MotionEvent.ACTION_UP:

                if (Math.abs(mIvLoadingPre.getY()) < (displayMetrics.heightPixels / 2) && upOrDown) {
                    switchRoomRelease();
                    mIvLoadingPre.animate().translationY(0).setDuration(400).withEndAction(endRunnable).start();

                } else if (upOrDown) {

                    mIvLoadingPre.animate().translationY(-displayMetrics.heightPixels).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isFirsterScroll = true;
                            upOrDown = false;
                        }
                    }).start();
                } else if (Math.abs(mIvLoadingNext.getY()) < (displayMetrics.heightPixels / 2)) {
                    switchRoomRelease();
                    mIvLoadingNext.animate().translationY(0).setDuration(400).withEndAction(endRunnable).start();
                } else {
                    mIvLoadingNext.animate().translationY(displayMetrics.heightPixels).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isFirsterScroll = true;
                            upOrDown = false;
                        }
                    }).start();
                }
                break;
        }
    }

    /*
    private void clearView()
    {
        mShowGiftAnimator.removeAllViews();
        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }
        if (mGiftSelectView != null) {
            mGiftSelectView.dismissBottomView();
        }
        mDanmuControl.hide();
    }*/

    private void requestIsFollow() {
        PhoneLiveApi.getIsFollow(mUser.getId(), mEmceeInfo.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res) {

                    if (mIvAttention != null) {
                        if (res.equals("0")) {
                            mIvAttention.setVisibility(View.VISIBLE);
                        } else {
                            mIvAttention.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }

    private void followEmcee() {

        PhoneLiveApi.showFollow(mUser.getId(), mEmceeInfo.getId(), AppContext.getInstance().getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res) {
                    mIvAttention.setVisibility(View.GONE);
                    showToast2("关注成功");
                }
            }
        });
        if (mUser.getVip_type() == null) {
//            mUser.setVip_type();
        }
        mChatServer.doSendMsg(mUser.getUser_nicename() + "关注了主播", mUser, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("直播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.start();
            //mPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("直播观看"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPause(this);          //统计时长

//        if (ksyMediaPlayer != null) {
//            //ksyMediaPlayer.pause();
//            //mPause = true;
//        }
    }

    @Override
    protected void onDestroy() {//释放
        videoPlayerEnd();
        //解除广播
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
