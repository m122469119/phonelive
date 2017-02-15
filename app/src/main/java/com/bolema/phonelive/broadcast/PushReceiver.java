package com.bolema.phonelive.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.ui.HomePageActivity;
import com.bolema.phonelive.ui.MainActivity;
import com.bolema.phonelive.ui.VideoPlayerActivity;
import com.bolema.phonelive.utils.ExampleUtil;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";
    private List<UserBean> mUserList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        TLog.log("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
//		AppContext.showToastShort(printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            TLog.log("[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...


        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            TLog.log("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));


//        	processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            TLog.log("[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            TLog.log("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            TLog.log("[MyReceiver] 接收到推送下来的通知的alert: " + alert);
            if (alert != null && alert.equals("您已被管理员拉黑，请与管理员联系")) {
                Intent pullBlackIntent = new Intent();
                pullBlackIntent.setAction("pullblack");
//				TLog.log("[MyReceiver] 接收到推送下来的通知的alert: " + alert);
                //发送有序广播,通知直播间，播放间和mainactivity关闭拉黑退出登录
                context.sendBroadcast(pullBlackIntent);
            }


//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			TLog.log("[MyReceiver] 接收到推送下来的通知的Message: " + message);


        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //Toast.makeText(context,"用户点击打开了通知",Toast.LENGTH_LONG).show();
            TLog.log(bundle.getString(JPushInterface.EXTRA_EXTRA));
            try {
                JSONObject extra = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                UserBean u = new Gson().fromJson(extra.getString("userinfo"), UserBean.class);
                //打开自定义的Activity
                Intent i = new Intent(context, MainActivity.class);
                Bundle bd = new Bundle();
                bd.putParcelable("USER_INFO",u);
                i.putExtra("USER_INFO",bd);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                context.startActivity(i);

//				UIHelper.showMainActivity(context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            TLog.log(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            TLog.log(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);

        } else {
            TLog.log(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    TLog.log(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
    }

    /**
     * 请求服务器判断主播是否仍在直播回调
     * @param v
     */
//	public StringCallback callback = new StringCallback() {
//		@Override
//		public void onError(Call call, Exception e) {
//
//		}
//
//		@Override
//		public void onResponse(String response) {
//			String res = ApiUtils.checkIsSuccess(response);
//			KLog.json(res);
//
//
//			try {
//				JSONObject resJson = new JSONObject(response);
//				if(Integer.parseInt(resJson.getString("ret")) == 200){
//					JSONObject dataJson =  resJson.getJSONObject("data");
//					JSONArray jsonArray = dataJson.getJSONArray("info");
//					UserBean userBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), UserBean.class);
//
//					String islive = jsonArray.getJSONObject(0).getString("islive");
//					if (islive.equals("1")) {
//						DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
//						DataSingleton.getInstance().setPostion(0);
//						Bundle bundle = new Bundle();
//						bundle.putSerializable("USER_INFO", userBean);
//						UIHelper.showLookLiveActivity(context, bundle);
//					} else {
//						AppContext.showToastShort("直播已结束");
//					}
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	};
}
