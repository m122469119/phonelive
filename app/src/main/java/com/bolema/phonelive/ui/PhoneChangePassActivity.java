package com.bolema.phonelive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.LoginUtils;
import com.bolema.phonelive.utils.TDevice;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 密码修改 HHH 2016-09-19
 */
public class PhoneChangePassActivity extends ToolBarBaseActivity {

    @InjectView(R.id.et_old_pass)
    EditText etOldPass;
    @InjectView(R.id.et_new_pass)
    EditText etNewPass;
    @InjectView(R.id.et_second_pass)
    EditText etSecondPass;

    //HHH 2016-09-09
    private String mOldPass = "";
    private String mNewPass = "";
    private String mSecondPass= "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_pass;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        setActionBarTitle("修改密码");
    }

    @OnClick(R.id.btn_change_pass)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change_pass) {
            if (prepareForChangePass()) {
                return;
            }
            mOldPass = etOldPass.getText().toString();
            mNewPass = etNewPass.getText().toString();
            mSecondPass = etSecondPass.getText().toString();
            PhoneLiveApi.getChangePass(String.valueOf(AppContext.getInstance().getLoginUid()),AppContext.getInstance().getToken(),mOldPass,mNewPass,mSecondPass,callback);
        }

    }

    //注册回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            showToast2(getString(R.string.editfail));
        }

        @Override
        public void onResponse(String s) {
            JSONObject dataJson = getDataJson(s);
            if(dataJson!=null) {
                try {
                    if (dataJson.getString("code").equals("0")) {
                        etOldPass.setText("");
                        etNewPass.setText("");
                        etSecondPass.setText("");
                        finish();
                        //showToast2(msg);
                    } else {
                        showToast2(dataJson.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                showToast2(getString(R.string.editfail));
            }

        }
    };

    //HHH 2016-09-09
    private boolean prepareForChangePass() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (etOldPass.length() == 0) {
            etOldPass.setError("请输入旧密码");
            etOldPass.requestFocus();
            return true;
        }

        /*
        if (etOldPass.length() <6 ) {
            etOldPass.setText("");
            etOldPass.setError("密码长度至少为6位");
            etOldPass.requestFocus();
            return true;
        }

        if (etOldPass.length() >12 ) {
            etOldPass.setText("");
            etOldPass.setError("密码长度最大为12位");
            etOldPass.requestFocus();
            return true;
        }*/

        if (etNewPass.length() == 0) {
            etNewPass.setError("请输入新密码");
            etNewPass.requestFocus();
            return true;
        }
        /*if (etNewPass.length() <6 ) {
            etNewPass.setText("");
            etNewPass.setError("密码长度至少为6位");
            etNewPass.requestFocus();
            return true;
        }

        if (etNewPass.length() >12 ) {
            etNewPass.setText("");
            etNewPass.setError("密码长度最大为12位");
            etNewPass.requestFocus();
            return true;
        }*/

        if (!etSecondPass.getText().toString().equals(etNewPass.getText().toString())) {
            etSecondPass.setText("");
            etSecondPass.setError("密码不一致，请重新输入");
            etSecondPass.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("手机登录"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("手机登陆"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    private final static int SUCCESS_CODE = 200;//成功请求到服务端
    private final static String TOKEN_TIMEOUT = "700";
    private JSONObject getDataJson(String res){
        try {
            JSONObject resJson = new JSONObject(res);
            if(Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE){
                JSONObject dataJson =  resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if(code.equals(TOKEN_TIMEOUT)){
                    AppManager.getAppManager().finishAllActivity();
                    Intent intent = new Intent(AppContext.getInstance(), LiveLoginSelectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppContext.getInstance().startActivity(intent);
                    return null;
                }else   {
                    return dataJson;
                    //return dataJson.get("msg").toString();
                }
            }else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }


}
