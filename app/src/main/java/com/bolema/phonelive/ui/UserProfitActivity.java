package com.bolema.phonelive.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.ProfitBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 收益
 */
public class UserProfitActivity extends ToolBarBaseActivity {
    private TextView mTvTitle;
    @InjectView(R.id.iv_back)
    ImageView mIvBack;
    @InjectView(R.id.tv_text)
    TextView mSaveInfo;
    @InjectView(R.id.tv_votes)
    TextView mVotes;
    @InjectView(R.id.tv_profit_canwithdraw)
    TextView mCanwithDraw;
    @InjectView(R.id.tv_profit_withdraw)
    TextView mWithDraw;
    private ProfitBean mProfitBean;
    private UserBean mUser;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_profit;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        setActionBarTitle(getString(R.string.myprofit));
//        setToolbarColor(R.color.pink_text);
        mSaveInfo.setVisibility(View.GONE);//待开发功能提现记录
        mSaveInfo.setText("提现记录");

        Bundle bundle = getIntent().getBundleExtra("USERINFO");
        mVotes.setText(bundle.getString("votes"));
        requestData();
    }

    private void  requestData()
    {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                if(null != res){
                    mProfitBean = new Gson().fromJson(res,ProfitBean.class);
                    fillUI();
                }
            }
        };
        PhoneLiveApi.getWithdraw(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);
    }

    private void fillUI() {
        mCanwithDraw.setText(mProfitBean.getCanwithdraw());
        mWithDraw.setText(mProfitBean.getWithdraw());
        mVotes.setText(mProfitBean.getVotes());
    }



    @OnClick({R.id.ll_profit_cash,R.id.TextView})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_profit_cash:
                /*
                PhoneLiveApi.requestCash(mUser.getId(),mUser.getToken(),
                        new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                String res = ApiUtils.checkIsSuccess(response);
                                if(null != res){
                                    AppContext.showToastAppMsg(UserProfitActivity.this,res);
                                    initData();
                                }
                            }
                });*/
                UIHelper.showRequestCashActivity(UserProfitActivity.this);
                break;

            case R.id.TextView:  //添加  zxy 2016-04-19
                UIHelper.showWebView(this, AppConfig.MAIN_URL2 + "/index.php?g=portal&m=page&a=newslist", "");
                break;
        }


    }
    @Override
    protected void initActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_actionbar_title);
        mTvTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_actionBarTitle);

    }

    @Override
    public void onResume() {
        requestData();
        super.onResume();
        MobclickAgent.onPageStart("我的收益"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的收益"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getWithdraw");
    }
}
