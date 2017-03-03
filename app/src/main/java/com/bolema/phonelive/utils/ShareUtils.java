package com.bolema.phonelive.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.R;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

/**
 *
 * Created by Administrator on 2016/4/14.
 */
public class ShareUtils {


    private static String androidapk;


    public static void share(Activity context, int id, UserBean user, PlatformActionListener listener, int s){
        switch (id){
            case R.id.ll_live_shar_qq:
                share(context, 3, user,listener);
                break;
            case R.id.ll_live_shar_pyq:
                share(context,2,user,listener);
                break;
            case R.id.ll_live_shar_qqzone:
                share(context,4,user,listener);
                break;
            case R.id.ll_live_shar_sinna:
                share(context, 0, user, listener);
                break;
            case R.id.ll_live_shar_wechat:
                share(context,1,user,listener);
                break;
        }
    }

    public static void share(final Context context, final int index, final UserBean user, final PlatformActionListener listener){
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Toast.makeText(context,"获取分享地址失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(res != null){
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        String url = jsonObject.getString("wx_siteurl");
                        AppConfig.SHARE_URL = url + "Phone/index?roomnum=";
                        androidapk = jsonObject.getString("app_android");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] names = new  String[]{SinaWeibo.NAME,Wechat.NAME,WechatMoments.NAME,QQ.NAME,QZone.NAME};
                    String content = user.getUser_nicename() + "正在直播,快来一起看吧~";
                    switch (index){
                        case 0:{
                            share(context,names[0],false,content,user,listener);
                            break;
                        }
                        case 1:{
                            share(context,names[1],false,content,user,listener);
                            break;
                        }
                        case 2:{
                            share(context,names[2],false,content,user,listener);
                            break;
                        }
                        case 3:{
                            share(context,names[3],false,content,user,listener);
                            break;
                        }
                        case 4:{
                            share(context,names[4],false,content,user,listener);
                            break;
                        }
                    }
                }

            }
        });

    }
    public static void share(final Context context, String name, boolean showContentEdit, String content, final UserBean user, PlatformActionListener listener) {
        ShareSDK.initSDK(context);
        final OnekeyShare oks = new OnekeyShare();
        oks.setSilent(true);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(name);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(context.getString(R.string.shartitle));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用

        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);

        oks.setImageUrl(user.getAvatar_thumb());

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        if (name.equals(Wechat.NAME) || name.equals(WechatMoments.NAME)) {
            oks.setUrl(AppConfig.SHARE_URL + user.getId());
            oks.setSiteUrl(AppConfig.SHARE_URL + user.getId());
            oks.setTitleUrl(AppConfig.SHARE_URL + user.getId());

        } else {
            oks.setUrl(androidapk);
            oks.setSiteUrl(androidapk);
            oks.setTitleUrl(androidapk);
        }

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment(context.getString(R.string.shartitle));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        oks.setCallback(listener);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用

        // 启动分享GUI
        oks.show(context);
    }
    //分享pop弹窗
    public static void showSharePopWindow(Context context,View v) {

        View view = LayoutInflater.from(context).inflate(R.layout.pop_view_share,null);
        PopupWindow p = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        p.setBackgroundDrawable(new BitmapDrawable());
        p.setOutsideTouchable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        p.showAtLocation(v, Gravity.NO_GRAVITY,location[0] + v.getWidth()/2 - view.getMeasuredWidth()/2,location[1]- view.getMeasuredHeight());

    }
}