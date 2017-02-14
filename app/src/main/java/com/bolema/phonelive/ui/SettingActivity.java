package com.bolema.phonelive.ui;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.utils.LoginUtils;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.utils.UpdateManager;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;

import org.kymjs.kjframe.Core;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends ToolBarBaseActivity {
    @InjectView(R.id.ll_loginout)
    AutoLinearLayout llLoginout;
    View bg_view;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        bg_view = findViewById(R.id.bg_gone);
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.setting));
    }

    @OnClick({R.id.ll_clearCache, R.id.ll_push_manage, R.id.ll_about, R.id.ll_feedback, R.id.ll_blank_list, R.id.rl_change_pass, R.id.ll_check_update, R.id.ll_loginout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_clearCache:
                clearCache();
                break;
            case R.id.ll_push_manage:
                UIHelper.showPushManage(this);
                break;
            case R.id.ll_about:
                UIHelper.showWebView(this, AppConfig.MAIN_URL2 + "/index.php?g=portal&m=page&a=lists", "服务条款");
                break;
            //用户反馈
            case R.id.ll_feedback:
                String model = Build.MODEL;
                String release = Build.VERSION.RELEASE;
                UIHelper.showWebView(this, AppConfig.MAIN_URL2 + "/index.php?g=portal&m=page&a=newslist&uid="
                        + AppContext.getInstance().getLoginUid() + "&version=" + release + "&model=" + model, "");
                break;
            case R.id.ll_blank_list:
                UIHelper.showBlackList(SettingActivity.this);
                break;
            case R.id.rl_change_pass:
                UIHelper.showPhoneChangePassActivity(SettingActivity.this);
                break;
            case R.id.ll_check_update:
                checkNewVersion();
                break;
            case R.id.ll_loginout:
                bg_view.setVisibility(View.VISIBLE);
                View mDialogView = View.inflate(this, R.layout.dialog_show_own_info_detail, null);
                TextView msg = (TextView) mDialogView.findViewById(R.id.show_msg);
                msg.setText("确认退出登录吗");
                Button no = (Button) mDialogView.findViewById(R.id.no);
                Button yes = (Button) mDialogView.findViewById(R.id.yes);
                yes.setText("退出");

                final Dialog dialog = new Dialog(this, R.style.dialog);
                dialog.setContentView(mDialogView);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginUtils.outLogin(SettingActivity.this);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bg_view.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    private void checkNewVersion() {
        UpdateManager manager = new UpdateManager(this, true);
        manager.checkUpdate();
    }

    private void clearCache() {
        AppContext.showToastAppMsg(this, "缓存清理成功");
        Core.getKJBitmap().cleanCache();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("设置"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("设置"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }
}
