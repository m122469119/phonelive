package com.bolema.phonelive.ui;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.bolema.phonelive.interf.BaseViewInterface;
import com.bolema.phonelive.utils.ExampleUtil;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.utils.UpdateManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.em.MainTab;
import com.bolema.phonelive.utils.LoginUtils;
import com.bolema.phonelive.widget.MyFragmentTabHost;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Set;

import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;


//主页面
public class MainActivity extends ToolBarBaseActivity implements
        TabHost.OnTabChangeListener, BaseViewInterface,
        View.OnTouchListener {
    @InjectView(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

//    public static boolean isForeground = false;
//    //for receive customer msg from jpush server
//    private MessageReceiver mMessageReceiver;
//    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
//    public static final String KEY_TITLE = "title";
//    public static final String KEY_MESSAGE = "message";
//    public static final String KEY_EXTRAS = "extras";



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public boolean isStartingLive = true;

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        getSupportActionBar().hide();
        initTabs();
//        checkLocation();

        mTabHost.setCurrentTab(100);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setNoTabChangedTag("1");
//        registerMessageReceiver();  // used for receive msg
    }

    private void initTabs() {
        final MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
//        String[] title = new String[]{"首页","","我"};

        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];

            TabHost.TabSpec tab = mTabHost.newTabSpec(String.valueOf(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            ImageView tabImg = (ImageView) indicator.findViewById(R.id.tab_img);
//            TextView tabTv = (TextView) indicator.findViewById(R.id.tv_wenzi);
            Drawable drawable = ContextCompat.getDrawable(this,
                    mainTab.getResIcon());

//            tabTv.setText(title[i]);

            if (i == 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 17, 2, 0);
                tabImg.setLayoutParams(params);
            }
            tabImg.setImageDrawable(drawable);
            tab.setIndicator(indicator);

            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }

//        mTabHost.getTabWidget().getChildAt(1).setLayoutParams();
        mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLive();
            }
        });
    }

    @Override
    public void initData() {
        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //检查是否有最新版本
        checkNewVersion();
        mTabHost.setCurrentTab(0);

        Bundle bundle = getIntent().getBundleExtra("USER_INFO");

        if (bundle != null) {
            UIHelper.showLookLiveActivity(this, bundle);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5:
                // 判断权限请求是否通过
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                        requestStartLive();
                    } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        AppContext.showToast("您已拒绝使用摄像头权限,将无法正常直播,请去设置中修改");
                    } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                        AppContext.showToast("您已拒绝使用录音权限,将无法正常直播,请去设置中修改");
                    } else if (grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                        AppContext.showToast("您没有同意使用读写文件权限,无法正常直播,请去设置中修改", 0);
                    } else if (grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                        AppContext.showToast("定位权限未打开", 0);
                    } else if (grantResults.length > 0 && grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                        AppContext.showToast("您没有同意使用定位权限,无法正常直播,请去设置中修改", 0);
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


//    public void registerMessageReceiver() {
//        mMessageReceiver = new MessageReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(MESSAGE_RECEIVED_ACTION);
//        registerReceiver(mMessageReceiver, filter);
//    }
    //请求服务端开始直播
    private void requestStartLive() {
        if (isStartingLive) {
            isStartingLive = false;
            PhoneLiveApi.getLevelLimit(AppContext.getInstance().getLoginUid(), new StringCallback() {

                @Override
                public void onError(Call call, Exception e) {
                    AppContext.showToastAppMsg(MainActivity.this, "开始直播失败");
                    isStartingLive = true;
                }

                @Override
                public void onResponse(String response) {
                    String res = ApiUtils.checkIsSuccess(response);
                    if (null != res) {
                        UIHelper.showStartLiveActivity(MainActivity.this);
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStartingLive = true;
    }

    //登录环信即时聊天
    private void loginIM() {
        String uid = String.valueOf(AppContext.getInstance().getLoginUid());

        EMClient.getInstance().login(uid,
                "fmscms" + uid, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        Log.d("loginHy", "登录成功");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("loginHy", "登录失败");
                        if (204 == code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.showToastAppMsg(MainActivity.this, "聊天服务器登录和失败,请重新登录");
                                }
                            });

                        }
                        TLog.log("环信[主页登录聊天服务器失败" + "code:" + code + "MESSAGE:" + message + "]");
                    }
                });


    }

    /**
     * @dw 注册极光推送
     */
    private void registerJpush() {
        JPushInterface.setAlias(this, AppContext.getInstance().getLoginUid() + "PUSH",
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        TLog.log("极光推送注册[" + i + "I" + "S:-----" + s + "]");
                    }
                });

    }

    /**
     * @dw 检查token是否过期
     */
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                if (null == res) return;
                if (res.equals(ApiUtils.TOKEN_TIMEOUT)) {
                    AppContext.showToastAppMsg(MainActivity.this, "登陆过期,请重新登陆");
                    UIHelper.showLoginSelectActivity(MainActivity.this);
                }
            }
        });
    }

    /**
     * @dw 检查是否有最新版本
     */
    private void checkNewVersion() {
        UpdateManager manager = new UpdateManager(this, false);
        manager.checkUpdate();

    }

    @Override
    protected void onResume() {
//        isForeground = true;
        super.onResume();
        MobclickAgent.onPageStart("主页"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长

    }

    public void onPause() {
//        isForeground = false;
        super.onPause();
        MobclickAgent.onPageEnd("主页"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    //开始直播初始化
    public void startLive() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        5);

            } else {
                requestStartLive();
            }
        } else {
            requestStartLive();
        }

    }
    //初始化检查是否获取位置权限
//    public void checkLocation() {
//        if(android.os.Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
//            }
//        }
//    }


    @Override
    public void onTabChanged(String tabId) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return false;

    }


    @Override
    public void onClick(View view) {

    }
//
//    public class MessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
//                String messge = intent.getStringExtra(KEY_MESSAGE);
//                String extras = intent.getStringExtra(KEY_EXTRAS);
//                StringBuilder showMsg = new StringBuilder();
//                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//                if (!ExampleUtil.isEmpty(extras)) {
//                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//                }
////                setCostomMsg(showMsg.toString());
//                AppContext.showToastShort(showMsg.toString());
//            }
//        }
//    }

}
