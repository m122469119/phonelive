package com.bolema.phonelive.fragment;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.interf.ListenMessage;
import com.bolema.phonelive.ui.other.PhoneLivePrivateChat;
import com.bolema.phonelive.utils.BlurUtil;
import com.bolema.phonelive.utils.StringUtils;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.CustomShadowView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 登录用户中心页面
 */
public class UserInformationFragment extends BaseFragment implements ListenMessage {

    //头像
    @InjectView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    //昵称
    @InjectView(R.id.tv_name)
    TextView mTvName;
    //
    @InjectView(R.id.iv_gender)
    ImageView mIvGender;

    //签名
    @InjectView(R.id.tv_signature)
    TextView mTvSignature;
    //修改信息
    @InjectView(R.id.iv_editInfo)
    ImageView mIvEditInfo;

    @InjectView(R.id.ll_user_container)
    View mUserContainer;
    //退出登陆
    @InjectView(R.id.rl_user_unlogin)
    View mUserUnLogin;

//    @InjectView(R.id.ll_loginout)
//    LinearLayout mLoginOut;

    //直播记录
    @InjectView(R.id.tv_info_u_live_num)
    TextView mLiveNum;

    //关注
    @InjectView(R.id.tv_info_u_follow_num)
    TextView mFollowNum;

    //粉丝
    @InjectView(R.id.tv_info_u_fans_num)
    TextView mFansNum;

    //发送
    @InjectView(R.id.tv_send)
    TextView mSendNum;

    //id
    @InjectView(R.id.tv_id)
    TextView mUId;

    //私信
    @InjectView(R.id.iv_info_private_core)
    ImageView mPrivateCore;

    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;
    @InjectView(R.id.rl_user_center)
    AutoRelativeLayout rlUserCenter;
    @InjectView(R.id.bg_icon_linearlayout)
    AutoLinearLayout bgIconLinearlayout;

    AutoLinearLayout editLinearLayout;

    private boolean mIsWatingLogin;

    private UserBean mInfo;

    private EMMessageListener mMsgListener;
    private FrameLayout frameContainer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bitmap resource = msg.getData().getParcelable("resource");
                    Bitmap bitmap = msg.getData().getParcelable("bitmap");
                    mIvAvatar.setImageBitmap(resource);
                    bgIconLinearlayout.setBackground(new BitmapDrawable(getResources(),
                            bitmap));
                    break;
            }
        }
    };


    private void steupUser() {
        if (mIsWatingLogin) {
            mUserContainer.setVisibility(View.GONE);
            mUserUnLogin.setVisibility(View.VISIBLE);
        } else {
            mUserContainer.setVisibility(View.VISIBLE);
            mUserUnLogin.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        unListen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_information,
                container, false);

        ButterKnife.inject(this, view);
        initView(view);

        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();
        listenMessage();
    }

    @Override
    public void initView(View view) {
        editLinearLayout = (AutoLinearLayout) view.findViewById(R.id.edit_linearLayout);
        editLinearLayout.setOnClickListener(this);
        view.findViewById(R.id.ll_live).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);
        view.findViewById(R.id.ll_fans).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_level).setOnClickListener(this);
        view.findViewById(R.id.ll_diamonds).setOnClickListener(this);
        view.findViewById(R.id.ll_vip).setOnClickListener(this); //HHH 2016-09-13
        view.findViewById(R.id.ll_balance_detail).setOnClickListener(this);
        view.findViewById(R.id.ll_exchange_vote).setOnClickListener(this);
        view.findViewById(R.id.ll_authenticate).setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
        frameContainer = (FrameLayout) view.findViewById(R.id.frame_container);
        mUserUnLogin.setOnClickListener(this);
//        mLoginOut.setOnClickListener(this);
        mIvEditInfo.setOnClickListener(this);
        mPrivateCore.setOnClickListener(this);
    }

    private void fillUI() {
        if (mInfo == null)
            return;

        //头像
//        mIvAvatar.setAvatarUrl(mInfo.getAvatar());

        //虚化背景
//        Core.getKJBitmap().displayCacheOrDefult(frameContainer,mInfo.getAvatar(),R.drawable.null_blacklist);

        Glide.with(getContext())
                .load(mInfo.getAvatar())
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    Bitmap bitmap;
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        int mWidth = resource.getWidth();
                        int mHeight = resource.getHeight();
                        if (mHeight < 400) {
                            bitmap = BlurUtil.doBlur(Bitmap.createBitmap(resource, 0, 0, mWidth, mHeight), 50, true);
                        } else {
                            bitmap = BlurUtil.doBlur(Bitmap.createBitmap(resource, 0, 0, mWidth, 400), 50, true);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("resource", resource);
                        bundle.putParcelable("bitmap", bitmap);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                });

        //昵称
        mTvName.setText(mInfo.getUser_nicename());
        //性别
        mIvGender.setImageResource(
                StringUtils.toInt(mInfo.getSex()) == 1 ? R.drawable.choice_sex_male : R.drawable.choice_sex_femal);
        //签名
        mTvSignature.setText(mInfo.getSignature().equals("") ? getString(R.string.defaultsign) : mInfo.getSignature());
        mUId.setText("ID:" + mInfo.getId());

        mTvSignature.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mUId.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mTvName.setShadowLayer(10F, 1F, 1F, Color.BLACK);
    }

    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            sendRequestData();
        } else {
            mIsWatingLogin = true;
        }
        steupUser();
    }

    private void sendRequestData() {
        int uid = AppContext.getInstance().getLoginUid();
        String token = AppContext.getInstance().getToken();
        PhoneLiveApi.getMyUserInfo(uid, token, stringCallback);
    }

    private String getCacheKey() {
        return "my_information" + AppContext.getInstance().getLoginUid();
    }

    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String s) {
            String res = ApiUtils.checkIsSuccess(s);
            if (res == null) {
                UIHelper.showLoginSelectActivity(getActivity());
                getActivity().finish();
                return;
            }
            mInfo = new Gson().fromJson(res, UserBean.class);
            AppContext.getInstance().updateUserInfo(mInfo);
            try {
                mLiveNum.setText("" + mInfo.getLiverecordnum());
                mFollowNum.setText("" + mInfo.getAttentionnum());
                mFansNum.setText("" + mInfo.getFansnum());
                mSendNum.setText("送出:  " + mInfo.getConsumption());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onClick(View v) {

        final int id = v.getId();
        switch (id) {
            case R.id.ll_exchange_vote: //兑换播币
                UIHelper.showExchangeVoteActivity(getActivity(), mInfo.getId(), mInfo.getVotes());
                break;
            //购买vip
            case R.id.ll_vip:
                UIHelper.showBuyVipActivity(getActivity());
                //if(SharedPreUtil.contains(getActivity().getApplicationContext(),"vip_type")
                //        &&SharedPreUtil.getString(getActivity().getApplicationContext(),"vip_type").equals("1"))
                //{
                //    Toast.makeText(getActivity(),"您已是VIP会员",Toast.LENGTH_SHORT).show();

                //}else{

                //}
                //UIHelper.showWebView(getActivity(),AppConfig.MAIN_URL2 + "/index.php?g=home&m=vip&a=index&uid="+ AppContext.getInstance().getLoginUid()+"&token="+AppContext.getInstance().getToken(),"购买VIP");
                break;
            case R.id.ll_authenticate://申请认证
                UIHelper.showWebView(getActivity(), AppConfig.MAIN_URL2 + "/index.php?g=User&m=Rz&a=auth&uid=" + AppContext.getInstance().getLoginUid(), "申请认证");
                break;
            //私信
            case R.id.iv_info_private_core:
                mIvNewMessage.setVisibility(View.GONE);
                UIHelper.showPrivateChatSimple(getActivity(), mInfo.getId());
                break;

            case R.id.iv_avatar:
                UIHelper.showMyInfoDetailActivity(getActivity());
                break;
            case R.id.ll_live:  //直播记录
                UIHelper.showLiveRecordActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_following:
                UIHelper.showAttentionActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_fans:
                 /*
                 * 关注列表
                  * */
                UIHelper.showFansActivity(getActivity(), mInfo.getId());
                break;
            case R.id.ll_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds://我的播币
                Bundle dBundle = new Bundle();
                dBundle.putString("diamonds", mInfo.getCoin());
                UIHelper.showMyDiamonds(getActivity(), dBundle);
                break;
            case R.id.ll_level://我的等级
                UIHelper.showLevel(getActivity(), AppContext.getInstance().getLoginUid());
                break;

            case R.id.rl_user_unlogin: //登陆选择
                AppManager.getAppManager().finishAllActivity();
                UIHelper.showLoginSelectActivity(getActivity());
                getActivity().finish();
                break;
            case R.id.edit_linearLayout://编辑资料
                UIHelper.showMyInfoDetailActivity(getActivity());
                break;
            case R.id.ll_profit://收益
                Bundle pBundle = new Bundle();
                pBundle.putString("votes", mInfo.getVotes());
                UIHelper.showProfitActivity(getActivity(), pBundle);
                break;
            case R.id.ll_balance_detail:
                UIHelper.showWebView(getActivity(), AppConfig.MAIN_URL2 + "/index.php?g=user&m=List&a=index&uid=" + AppContext.getInstance().getLoginUid() + "&token=" + AppContext.getInstance().getToken(), "收支明细");
                break;
            default:
                break;
        }
    }


    @Override
    public void initData() {
        //获取私信未读数量
        if (PhoneLivePrivateChat.getUnreadMsgsCount() > 0) {
            mIvNewMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), stringCallback);

    }

    public void listenMessage() {

        mMsgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvNewMessage.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMsgListener);

    }

    public void unListen() {
        EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
