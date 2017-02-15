package com.bolema.phonelive.utils;

import android.app.Activity;
import android.os.Bundle;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.bean.UserBean;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 判断是否仍在直播
 * Created by yuanshuo on 2017/2/15.
 */

public class StillLiveUtils {
    private Activity activity;
    public StillLiveUtils(Activity activity) {
        this.activity = activity;
    }

    public StringCallback getStillcallback() {
        return stillcallback;
    }
    /**
     * 请求服务器判断主播是否仍在直播回调
     * @param v
     */
    public StringCallback stillcallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
//            String res = ApiUtils.checkIsSuccess(response);
//            KLog.json(res);

            try {
                JSONObject resJson = new JSONObject(response);
                if(Integer.parseInt(resJson.getString("ret")) == 200){
                    JSONObject dataJson =  resJson.getJSONObject("data");
                    JSONArray jsonArray = dataJson.getJSONArray("info");
                    UserBean userBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), UserBean.class);

                    String islive = jsonArray.getJSONObject(0).getString("islive");
                    if (islive.equals("1")) {
//                        DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
//                        DataSingleton.getInstance().setPostion(0);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("USER_INFO", userBean);
                        UIHelper.showLookLiveActivity(activity, bundle);
                    } else {
                        AppContext.showToastShort("直播已结束");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
