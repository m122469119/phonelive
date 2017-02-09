package com.bolema.phonelive.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.bolema.phonelive.R;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

public class BuyVipActivity extends ToolBarBaseActivity {


    @InjectView(R.id.tv_vip_name)
    TextView tvVipName;
    @InjectView(R.id.tv_vip_coin)
    TextView tvVipCoin;
    @InjectView(R.id.av_vip_thumb)
    ImageView avVipThumb;
    @InjectView(R.id.tv_vip_endtime)
    TextView tvVipEndtime;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_vip;
    }

    @Override
    public void initView() {
        setActionBarTitle("购买VIP");
    }

    @Override
    public void initData() {
        requestData();
    }

    //请求vip列表
    private void requestData() {
        PhoneLiveApi.getOtherUserInfo(AppContext.getInstance().getLoginUid(), getOtherUserInfoCallback);
        PhoneLiveApi.getShowVip(getShowVipCallback);
    }

    private StringCallback getShowVipCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            showToast2("获取信息失败,请检查网络设置");
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            Gson g = new Gson();
            KLog.json(res);

            if (res != null) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    //avVipThumb.setImageResource();
                    //avVipThumb.setAvatarUrl(jsonObject.getString("thumb"));

                    Glide.with(BuyVipActivity.this)
                            .load(jsonObject.getString("img"))
                            .centerCrop()
                            .placeholder(R.drawable.null_blacklist)
                            .crossFade()
                            .fitCenter()
                            .into(avVipThumb);

                    tvVipCoin.setText(jsonObject.getString("coin"));
//                    tvVipName.setText(jsonObject.getString("name"));
                    //fillVipUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private  StringCallback getOtherUserInfoCallback  = new StringCallback() {

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            if (res != null) {
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.getString("vip_type").equals("1")) {

                        tvVipEndtime.setText("到期时间:" + DateUtil.stampToDate(String.valueOf(jsonObject.getLong("vip_endtime") * 1000)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private StringCallback buyVipCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            showToast2("获取信息失败,请检查网络设置");
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            if (res != null) {
                showToast2("购买成功");

                PhoneLiveApi.getOtherUserInfo(AppContext.getInstance().getLoginUid(), getOtherUserInfoCallback);

            }
        }
    };


    //购买vip
    private void buyVip() {
        PhoneLiveApi.buyVip(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), buyVipCallback);
    }

    private void fillVipUI() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getShowVip");
        OkHttpUtils.getInstance().cancelTag("getOtherUserInfo");
    }

    @OnClick(R.id.btn_buy_vip)
    @Override
    public void onClick(View view) {

        buyVip();

    }


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
