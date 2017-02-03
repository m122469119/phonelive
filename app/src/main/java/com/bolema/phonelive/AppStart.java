package com.bolema.phonelive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.bolema.phonelive.ui.LiveLoginSelectActivity;
import com.bolema.phonelive.ui.MainActivity;
import com.bolema.phonelive.utils.SharedPreUtil;
import com.hyphenate.chat.EMClient;
import com.bolema.phonelive.R;
import com.bolema.phonelive.ui.SplashActivity;
import com.bolema.phonelive.utils.TDevice;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * 应用启动界面
 *
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAPP(this);
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cacheVersion = PreferenceHelper.readInt(this, "first_install",
                "first_install", -1);
        int currentVersion = TDevice.getVersionCode();
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(this, "first_install", "first_install",
                    currentVersion);
            cleanImageCache();
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private void cleanImageCache() {
        final File folder = FileUtils.getSaveFolder("phoneLive/imagecache");
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (folder.listFiles().length > 0) {
                        for (File file : folder.listFiles()) {
                            if (file != null) {
                                file.delete();
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {

        if(!SharedPreUtil.contains(getApplicationContext(),"IS_FIRST_USE")||SharedPreUtil.getBoolean(getApplicationContext(),"IS_FIRST_USE")) //HHH 2016-09-19 引导页跳转
        {
            Intent intent = new Intent(AppStart.this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;

        }else
        {
            if(!AppContext.getInstance().isLogin()){
                Intent intent = new Intent(AppStart.this, LiveLoginSelectActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        //Intent uploadLog = new Intent(this, LogUploadService.class);
        //startService(uploadLog);
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    int checkAPP(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            int hashcode = sign.hashCode();
            Log.i("myhashcode", "hashCode : " + hashcode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
