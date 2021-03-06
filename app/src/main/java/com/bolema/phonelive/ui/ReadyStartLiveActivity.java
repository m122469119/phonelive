package com.bolema.phonelive.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.InputMethodUtils;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.utils.ShareUtils;
import com.bolema.phonelive.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import okhttp3.Call;

public class ReadyStartLiveActivity extends ToolBarBaseActivity {
    //填写直播标题
    @InjectView(R.id.et_start_live_title)
    EditText mStartLiveTitle;

    //开始直播遮罩层
    @InjectView(R.id.rl_start_live_bg)
    RelativeLayout mStartLiveBg;

    //开始直播btn
    @InjectView(R.id.btn_start_live)
    Button mStartLive;

    //分享模式 7为不分享任何平台
    private int shareType = 7;

    private UserBean mUser;

    private boolean isFrontCameraMirro=false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    AppContext.showToastShort("分享成功");
                    break;
                case 0:
                    AppContext.showToastShort("分享失败");
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ready_start_live;
    }



    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        //默认新浪微博share
        ImageView mSinnaWeiBoShare = (ImageView) findViewById(R.id.iv_live_share_weibo);
        mSinnaWeiBoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,0);
                shareType = 0 == shareType?7:0;
            }
        });
        findViewById(R.id.iv_live_share_timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,2);
                shareType = 2 == shareType?7:2;
            }
        });
        findViewById(R.id.iv_live_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,1);
                shareType = 1 == shareType?7:1;
            }
        });

        findViewById(R.id.iv_live_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,3);
                shareType = 3 == shareType?7:3;
            }
        });
        findViewById(R.id.iv_live_share_qqzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,4);
                shareType = 4 == shareType?7:4;
            }
        });

        ((CheckBox)findViewById(R.id.chk_front_mirro)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isFrontCameraMirro = isChecked;
            }
        });

    }

    @Override
    public void initData() {
         mUser = AppContext.getInstance().getLoginUser();
    }

    @OnClick({R.id.iv_live_exit,R.id.btn_start_live,R.id.tv_add_topic})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_live://创建房间
                //请求服务端存储记录
                createRoom();
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.tv_add_topic:
                //UIHelper.showTopicTitleListActivity(this);
                startActivityForResult (new Intent(ReadyStartLiveActivity.this, TopicTitleListActivity.class), 1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case 1:
                if(data.getStringExtra("topic")!=null)
                {
                    mStartLiveTitle.setText(mStartLiveTitle.getText().toString()+data.getStringExtra("topic"));
                }
        }
    }

    /**
     * @dw 创建直播房间
     * 请求服务端添加直播记录,分享直播
     * */
    private void createRoom() {
        if(shareType != 7){

            ShareUtils.share(ReadyStartLiveActivity.this, shareType, mUser,
                    new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            readyStart();
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            readyStart();
                            handler.sendEmptyMessage(0);
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            readyStart();
                        }
                    });

        }else {
            readyStart();
        }
        InputMethodUtils.closeSoftKeyboard(this);
        mStartLive.setEnabled(false);
        mStartLive.setTextColor(ContextCompat.getColor(this,R.color.white));
    }
    /**
     * @dw 准备直播
     * */
    private void readyStart() {

        //拼接流地址
        final String stream = mUser.getId() + "_" + System.currentTimeMillis();
        //请求服务端
        PhoneLiveApi.createLive(mUser.getId(),stream, StringUtils.getNewString(mStartLiveTitle.getText().toString()),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        AppContext.showToastAppMsg(ReadyStartLiveActivity.this,"开启直播失败,请退出重试- -!");
                    }

                    @Override
                    public void onResponse(String s) {
                        String res = ApiUtils.checkIsSuccess(s);
                        if(res != null){
                            StartLiveActivity.startLiveActivity(ReadyStartLiveActivity.this,stream,isFrontCameraMirro);
                            finish();
                        }
                    }
                },mUser.getToken());
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    /**
     * @dw 开始直播分享
     * @param v 点击按钮
     * @param type 分享平台
     * */
    private void startLiveShare(View v,int type){
        String titleStr = "";
        if(type == shareType){
            String titlesClose[] = getResources().getStringArray(R.array.live_start_share_close);
            titleStr = titlesClose[type];
        }else {
            String titlesOpen[] = getResources().getStringArray(R.array.live_start_share_open);
            titleStr = titlesOpen[type];
        }

        View popView  =  getLayoutInflater().inflate(R.layout.pop_view_share_start_live,null);
        TextView title = (TextView) popView.findViewById(R.id.tv_pop_share_start_live_prompt);
        title.setText(titleStr);
        PopupWindow pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        pop.setFocusable(false);

        pop.showAtLocation(v, Gravity.NO_GRAVITY,location[0] + v.getWidth()/2 - popView.getMeasuredWidth()/2,location[1]- popView.getMeasuredHeight());

    }


}
