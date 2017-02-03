package com.bolema.phonelive.api.remote;

import android.content.Intent;
import android.widget.Toast;

import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.ui.LiveLoginSelectActivity;
import com.bolema.phonelive.AppContext;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtils {
    public final static int SUCCESS_CODE = 200;//成功请求到服务端
    public final static String TOKEN_TIMEOUT = "700";

    public static String checkIsSuccess(String res){

        KLog.json("tokenJson", res);

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
                }else if(!code.equals("0")){
                    Toast.makeText(AppContext.getInstance(),dataJson.get("msg").toString(),Toast.LENGTH_SHORT).show();
                    return null;
                }else {
                    return dataJson.get("info").toString();
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
