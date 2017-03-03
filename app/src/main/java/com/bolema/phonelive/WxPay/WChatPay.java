package com.bolema.phonelive.WxPay;

import android.app.Activity;
import android.widget.Toast;

import com.bolema.phonelive.api.remote.ApiUtils;
import com.socks.library.KLog;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WChatPay {
    IWXAPI msgApi;
    //appid
    final String appId = "wxb98f25d7ab87d1cd";

    private Activity context;
    public WChatPay(Activity context) {
        this.context = context;
        // 将该app注册到微信
        msgApi = WXAPIFactory.createWXAPI(context,null);
        msgApi.registerApp(appId);
    }

    /**
     * @dw 初始化微信支付
     * @param price 价格
     * @param num 数量
     * */
    public void initPay(final String price, final String num) {
        PhoneLiveApi.wxPay(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),price,new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(AppContext.getInstance(),"获取订单失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String response) {
//                        String res = ApiUtils.getResponse(response);
//                        KLog.json(res);
//                        if(null == res) return;
//                        callWxPay(res);
                        try {
                            JSONObject json = new JSONObject(response);
                            int ret = json.getInt("ret");
                            if (ret == 200) {
                                JSONObject data = json.getJSONObject("data");
                                int code = data.getInt("code");
                                if (price.equals("1") && num.equals("200")) {
                                    if (code == 0) {
                                        callWxPay(data.getString("info"));
                                    } else {
                                        Toast.makeText(AppContext.getInstance(), "新人礼包已使用", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    callWxPay(data.getString("info"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void callWxPay(String res) {
        try {
            JSONObject signInfo = new JSONObject(res);
            PayReq req = new PayReq();
            req.appId        = signInfo.getString("appid");
            req.partnerId    = signInfo.getString("partnerid");
            req.prepayId     = signInfo.getString("prepayid");//预支付会话ID
            req.packageValue = "Sign=WXPay";
            req.nonceStr     = signInfo.getString("noncestr");
            req.timeStamp    = signInfo.getString("timestamp");
            req.sign         = signInfo.getString("sign");
            if(msgApi.sendReq(req)){
                AppContext.showToastAppMsg(context,"微信支付");
            }else{
                AppContext.showToastAppMsg(context,"请查看您是否安装微信");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
