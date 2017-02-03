package com.bolema.phonelive.ui;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;

import butterknife.InjectView;

/**
 * 等级
 */
public class UserLevelActivity extends ToolBarBaseActivity {
    @InjectView(R.id.wv_level)
    WebView mWbView;
    @InjectView(R.id.pb_loading)
    ProgressBar mProgressBar;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_level;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.myleve));
        mProgressBar.setMax(100);
        mWbView.setWebChromeClient(new WebViewClient());
        WebSettings settings = mWbView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWbView.loadUrl(AppConfig.MAIN_URL2 + "/index.php?g=user&m=level&a=index&uid="+getIntent().getStringExtra("USER_ID"));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private class WebViewClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if(newProgress == 100){
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);

        }
    }

    @Override
    protected void onDestroy() {
        mWbView.destroy();
        super.onDestroy();

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的等级"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的等级"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

}
