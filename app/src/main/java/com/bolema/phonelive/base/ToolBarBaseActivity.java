package com.bolema.phonelive.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.interf.BaseViewInterface;
import com.bolema.phonelive.interf.DialogControl;
import com.bolema.phonelive.ui.dialog.CommonToast;
import com.bolema.phonelive.utils.DialogHelp;
import com.bolema.phonelive.utils.LoginUtils;
import com.bolema.phonelive.utils.StringUtils;
import com.bolema.phonelive.utils.TDevice;
import com.githang.statusbar.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.ButterKnife;

/**
 *
 * Created by Administrator on 2016/6/22.
 */
public abstract class ToolBarBaseActivity extends AppCompatActivity implements
        DialogControl, View.OnClickListener, BaseViewInterface {

    private Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    public static final String INTENT_ACTION_EXIT_APP = "INTENT_ACTION_EXIT_APP";

    private boolean _isVisible;
    private ProgressDialog _waitDialog;
    protected LayoutInflater mInflater;
    protected ActionBar mActionBar;
    private TextView mTvActionTitle;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TDevice.hideSoftKeyboard(getCurrentFocus());
        ButterKnife.reset(this);
        //OkHttpUtils.getInstance().cancelTag("phonelive");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        initToolbar();
        if (!hasActionBar()) {
            toolbar.setVisibility(View.GONE);
        }
        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mActionBar = getSupportActionBar();
        mInflater = getLayoutInflater();
        if (hasActionBar()) {
            initActionBar(mActionBar);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.global));
//        }
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.global));
        // 通过注解绑定控件
        init(savedInstanceState);
        ButterKnife.inject(this);
        initView();
        initData();
        _isVisible = true;


    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvActionTitle = (TextView) findViewById(R.id.toolbar_title);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    protected void onBeforeSetContentLayout() {
    }

    protected boolean hasActionBar() {
        return true;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {
    }

    protected void initActionBar(ActionBar actionBar) {
        if (actionBar == null)
            return;
        if (hasBackButton()) {
            //mActionBar.setDisplayHomeAsUpEnabled(true);
            //mActionBar.setHomeButtonEnabled(true);
        } else {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayUseLogoEnabled(false);
            int titleRes = getActionBarTitle();
            if (titleRes != 0) {
                actionBar.setTitle(titleRes);
            }
        }
    }

    public void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    public void setActionBarTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        if (hasActionBar() && mActionBar != null) {
            if (mTvActionTitle != null) {
                mTvActionTitle.setText(title);
            }
            toolbar.setTitle(title);
        }
    }

    public void setToolbarColor(int colorId) {
        if (colorId != 0) {
            mTvActionTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), colorId));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showToast3(String msg, int time) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int msgResid, int icon, int gravity) {
        showToast(getString(msgResid), icon, gravity);
    }

    public void showToast2(String msg) {
        AppContext.showToastAppMsg(this, msg);
    }

    public void showToast(String message, int icon, int gravity) {
        CommonToast toast = new CommonToast(this);
        toast.setMessage(message);
        toast.setMessageIc(icon);
        toast.setLayoutGravity(gravity);
        toast.show();
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public ProgressDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected int getUserID() {
        return AppContext.getInstance().getLoginUid();
    }


    protected String getUserToken() {
        return AppContext.getInstance().getToken();
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        // setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    native int init(Context context);


    static {
        //System.loadLibrary("ksyaac");
    }




}
