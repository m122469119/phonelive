package com.bolema.phonelive.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.LoginUtils;
import com.bolema.phonelive.utils.SharedPreUtil;
import com.bolema.phonelive.utils.TDevice;
import com.bolema.phonelive.utils.UIHelper;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 *手机登陆
 */
public class PhoneLoginActivity extends ToolBarBaseActivity {
    @InjectView(R.id.et_loginphone)
    EditText mEtUserPhone;
    @InjectView(R.id.et_logincode)
    EditText mEtCode;
    @InjectView(R.id.btn_phone_login_send_code)
    Button mBtnSendCode;

    @InjectView(R.id.et_password)
    EditText mEtUserPassword;


    private int waitTime = 30;


    //HHH 2016-09-09
    private String mUserName = "";
    private String mCode = "";
    private String mPassword = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mBtnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
    }

    private void sendCode() {
        mUserName = mEtUserPhone.getText().toString();
        if(!mUserName.equals("") && mUserName.length() == 11) {
            AppContext.showToastAppMsg(PhoneLoginActivity.this, getString(R.string.codehasbeensend));
            PhoneLiveApi.getMessageCode(mUserName);
            mBtnSendCode.setEnabled(false);
            mBtnSendCode.setTextColor(ContextCompat.getColor(this,R.color.white));
            mBtnSendCode.setText("重新获取验证码(" + waitTime + ")");
            handler.postDelayed(runnable,1000);
        }
        else{
            AppContext.showToastAppMsg(PhoneLoginActivity.this, getString(R.string.plasecheckyounumiscorrect));
        }

    }


    @Override
    public void initData() {
        setActionBarTitle("使用手机号码登陆");
    }
    @OnClick({R.id.btn_dologin,R.id.btn_doReg,R.id.tv_findPass})
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_dologin) {

            if (prepareForLogin()) {
                return;
            }
            mUserName = mEtUserPhone.getText().toString();
            mCode = mEtCode.getText().toString();
            mPassword=  mEtUserPassword.getText().toString();
            showWaitDialog(R.string.loading);
            PhoneLiveApi.login(mUserName,mPassword, mCode, callback);

        }else  if (v.getId()==R.id.btn_doReg)
        {
            UIHelper.showMobileRegLogin(this);

        }else  if(v.getId()==R.id.tv_findPass)
        {
            UIHelper.showUserFindPass(this);
        }

    }
    //登录回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String s) {
            KLog.json("login", s);
           hideWaitDialog();
           String requestRes = ApiUtils.checkIsSuccess(s);
           if(requestRes != null){
               Gson gson = new Gson();
               UserBean user = gson.fromJson(requestRes, UserBean.class);
               //友盟登录统计
               MobclickAgent.onProfileSignIn(String.valueOf(user.getId()));

               handler.removeCallbacks(runnable);
               //保存用户信息
               //AppContext.getInstance().setUserBean(user);
               SharedPreUtil.put(getApplicationContext(),"vip_type",user.getVip_type());
               AppContext.getInstance().saveUserInfo(user);
               LoginUtils.getInstance().OtherInit(PhoneLoginActivity.this);
           }
        }
    };


    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }
        if (mEtUserPhone.length() != 11) {
            mEtUserPhone.setError("请输入11位的手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }

        /*
        if (mEtCode.length() == 0) {
            mEtCode.setError("请输入验证码");
            mEtCode.requestFocus();
            return true;
        }*/

        //HHH 2016-09-09
        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }
        /*if (mEtUserPassword.length() <6) {
            mEtUserPassword.setText("");
            mEtUserPassword.setError("密码长度至少为6位");
            mEtUserPassword.requestFocus();
            return true;
        }
        if (mEtUserPassword.length() >12) {
            mEtUserPassword.setText("");
            mEtUserPassword.setError("密码长度最大为12位");
            mEtUserPassword.requestFocus();
            return true;
        }*/

        return false;
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            waitTime -- ;
            mBtnSendCode.setText("重新获取验证码(" + waitTime + ")");
            if(waitTime == 0) {
                handler.removeCallbacks(runnable);
                mBtnSendCode.setText("发送验证码");
                mBtnSendCode.setEnabled(true);
                waitTime = 30;
                return;
            }
            handler.postDelayed(this,1000);

        }
    };

    @Override
    protected boolean hasBackButton() {
        return true;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };
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
}
