package com.bolema.phonelive.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppManager;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.utils.TLog;

import butterknife.InjectView;


//浏览器打开url
public class WebViewActivity extends ToolBarBaseActivity implements View.OnClickListener {
    @InjectView(R.id.iv_webview_back)
    ImageView mBack;
    @InjectView(R.id.tv_webview_title)
    TextView mTitle;
    private WebView wv = null;
    private String url;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wv.getTitle().equals("提交成功")){
                    finish();
                    return;
                }
                if(null != wv){
                    if(wv.canGoBack()){
                        wv.goBack();
                    }else {
                        finish();
                    }
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != wv){
                    if(wv.canGoBack()){
                        wv.goBack();
                    }else {
                        finish();
                    }
                }
            }
        });

        wv = (WebView) this.findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setBuiltInZoomControls(true);
        WebChromeClient chromeClient = new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitle.setText(title);
                setActionBarTitle(title);
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {

                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }


        };


        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(mTitle != null){
                    mTitle.setText(view.getTitle());
                }

                setActionBarTitle(view.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        wv.clearHistory();
                        finish();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return false;
                }

                if (url.startsWith("close:")) {
                    finish();
                    return false;
                }

                if (url.startsWith("login:")) {
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        wv.clearHistory();
                        finish();
                    }
                    return false;
                }
                view.loadUrl(url);
                return false;
            }
        });
        wv.setWebChromeClient(chromeClient);
    }
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    @Override
    public void initData() {
        Bundle _Extras = getIntent().getBundleExtra("URL_INFO");
        url = _Extras.getString("url");
        wv.loadUrl(url);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK  && wv.getTitle().equals("提交成功")){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 16908332 && wv.getTitle().equals("提交成功")){
            finish();
            return true;

        }
        if(item.getItemId() == 16908332){
            if(wv.canGoBack()){
                wv.goBack();
                return true;
            }else {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("webview"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("webview"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv.destroy();
    }
}
