package com.bolema.phonelive.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.callback.StringCallback;


public class LoginUtils {
    private static LoginUtils loginUtils = null;

    public static LoginUtils getInstance(){
        if(null == loginUtils){
            loginUtils = new LoginUtils();
        }
        return loginUtils;
    }

    public void  OtherInit(final Activity context){
        //环信注册
        new Thread(){
            @Override
            public void run() {
                int uid = AppContext.getInstance().getLoginUid();
                try {
                    EMClient.getInstance().createAccount(String.valueOf(uid), "fmscms" + uid);//同步方法
                    Log.d("reg_uid", uid+"");
                    TLog.log("环信[注册成功]");
                } catch (HyphenateException e) {
                    int errorCode=e.getErrorCode();
                    TLog.log("环信[注册失败]"+errorCode);
                    e.printStackTrace();
                } finally {
                    UIHelper.showMainActivity(context);
                    context.finish();
                }
            }
        }.start();
        //保存推送状态
        SharedPreUtil.put(context,"isOpenPush",true);
    }

    public static void outLogin(Context context) {
        //环信退出登陆
        EMClient.getInstance().logout(true);
        AppContext.getInstance().Logout();
        UIHelper.showLoginSelectActivity(context);
    }

    public static void contextLoginOut(Context context) {
        //环信退出登陆
        EMClient.getInstance().logout(true);
        AppContext.getInstance().Logout();
        UIHelper.showLoginSelectActivity2(context);
    }



    public static void tokenIsOutTime(StringCallback callback){
        PhoneLiveApi.tokenIsOutTime(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),callback);
    }
}
