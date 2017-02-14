package com.bolema.phonelive.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.WxPay.WChatPay;
import com.bolema.phonelive.alipay.AliPay;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.RechargeBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 我的播币
 */
public class UserDiamondsActivity extends ToolBarBaseActivity {

    private RelativeLayout mWxPay;

    private RelativeLayout mAliPay;
    private List<RechargeBean> rechanList;
    @InjectView(R.id.lv_select_num_list)
    ListView mSelectNumListItem;
//    private TextView mPayName;
    private TextView mCoin;
    private final int WXPAY = 1;
    private final int ALIPAY = 2;
    private int PAYMODE = WXPAY;
    private View mHeadView;
    private AliPay mAliPayUtils;
    private int[] price;
    private int[] diamondsNum;
    private WChatPay mWChatPay;


    private TextView mTvCustomDiamondNum;
    private EditText mEtCustomPrice;
    private Button mBtnBuyDiamond;
    private TextView mPaymentType;
    private int ratio;

    //是否是第一次充值
    private boolean isFirstCharge = false;

    private String explain[] = {"新人礼包，仅一次机会", "", "", "赠送200播币", "赠送1200播币", "赠送2200播币", "赠送5200播币"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_diamonds;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mHeadView = getLayoutInflater().inflate(R.layout.view_diamonds_head, null);
        mWxPay = (RelativeLayout) mHeadView.findViewById(R.id.rl_wxpay);
        mAliPay = (RelativeLayout) mHeadView.findViewById(R.id.rl_alipay);
//        mPayName = (TextView) mHeadView.findViewById(R.id.tv_payname);
        mCoin = (TextView) mHeadView.findViewById(R.id.tv_coin);
        mTvCustomDiamondNum = (TextView) mHeadView.findViewById(R.id.tv_custom_diamondsnum);
        mEtCustomPrice = (EditText) mHeadView.findViewById(R.id.et_custom_preice_text);
        mBtnBuyDiamond = (Button) mHeadView.findViewById(R.id.btn_buy_diamond);
        mPaymentType = (TextView) mHeadView.findViewById(R.id.tv_payment_type);

        mBtnBuyDiamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PAYMODE == WXPAY) {
                    WChatPay wxpay = new WChatPay(UserDiamondsActivity.this);
                    wxpay.initPay(mEtCustomPrice.getText().toString(), mTvCustomDiamondNum.getText().toString());
                    return;
                }
                if (PAYMODE == ALIPAY)
                    mAliPayUtils.initPay(mEtCustomPrice.getText().toString(), mTvCustomDiamondNum.getText().toString());
                else
                    mWChatPay.initPay(mEtCustomPrice.getText().toString(), mTvCustomDiamondNum.getText().toString());
            }
        });

        mEtCustomPrice.addTextChangedListener(watcher);

        mSelectNumListItem.addHeaderView(mHeadView);

        getImageView(mWxPay, View.VISIBLE);
        getImageView(mAliPay, View.GONE);
        selected(mWxPay);
        //微信支付
        mWxPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAYMODE = WXPAY;

                selected(mWxPay);

            }
        });
        //支付宝
        mAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAYMODE = ALIPAY;

                selected(mAliPay);

            }
        });
        mSelectNumListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if (PAYMODE == WXPAY) {
                        //showToast2("微信支付暂未开放...");
                        WChatPay wxpay = new WChatPay(UserDiamondsActivity.this);


                        wxpay.initPay(String.valueOf(price[position - 1]), String.valueOf(diamondsNum[position - 1]));
                        return;
                    }
                    if (PAYMODE == ALIPAY) {
                        mAliPayUtils.initPay(String.valueOf(price[position - 1]), String.valueOf(diamondsNum[position - 1]));
                    }
                    else {
                        mWChatPay.initPay(String.valueOf(price[position - 1]), String.valueOf(diamondsNum[position - 1]));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {


        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (mEtCustomPrice.getText().toString().equals("")) {
                mTvCustomDiamondNum.setText("0");
                return;
            }
            int diamondNum = Integer.parseInt(mEtCustomPrice.getText().toString()) * ratio;
            mTvCustomDiamondNum.setText(String.valueOf(diamondNum));

        }

    };

    @Override
    public void initData() {
        requestData();
        mAliPayUtils = new AliPay(this);
        //mWChatPay = new WChatPay(this);
        setActionBarTitle(getString(R.string.mydiamonds));
        //diamondsNum = new int[]{20,60,300,980,2980,5880,15980};
        //price = new int[]{1,6,30,98,298,588,1598};
        diamondsNum = new int[]{200, 600, 3000, 9800, 38800, 58800, 159800};
        price = new int[]{1, 6, 30, 98, 388, 588, 1598};
        rechanList = new ArrayList<>();

    }


    private void requestData() {

        PhoneLiveApi.getPubMsg(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                if (res == null) {
                    return;
                }

                try {
                    JSONObject jsono = new JSONObject(res);
                    if (jsono.has("pub_msg")) {
                        //showToast2(jsono.getString"pub_msg"));
                        mPaymentType.setText(mPaymentType.getText().toString() + "(" + jsono.getString("pub_msg") + ")");
                    } else {
                        showToast2("公众号提示信息获取异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                if (res == null) {
                    return;
                }

                try {
                    JSONObject jsono = new JSONObject(res);
                    if (jsono.has("ratio")) {
                        ratio = jsono.getInt("ratio");
                    } else {
                        showToast2("兑换比率获取异常");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        PhoneLiveApi.getUserDiamondsNum(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response);
                        if (res == null) return;
                        fillUI(res);
                    }
                });

        //判断是否是第一充值
        PhoneLiveApi.getCharge(AppContext.getInstance().getLoginUid(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {

                String res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        JSONObject object = new JSONObject(res);

                        for (int i = 0; i < price.length; i++) {
                            rechanList.add(new RechargeBean(price[i], explain[i], diamondsNum[i], price[i] + ".00"));
                        }
                        mSelectNumListItem.setAdapter(new RechangeAdapter());

                        isFirstCharge = object.getString("isnew").equals("1");

                        mCoin.setText(object.getString("coin") + "播币");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void fillUI(String res) {
        try {
            JSONObject jsonObj = new JSONObject(res);
            mCoin.setText(jsonObj.getString("coin") + "播币");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void selected(RelativeLayout rl) {
//        rl.setBackgroundColor(ContextCompat.getColor(this, R.color.global));
//        rl.getChildAt(1).setVisibility(View.VISIBLE);
//        mPayName.setText(getString(R.string.paymode) + (PAYMODE == WXPAY ? getString(R.string.wxpay) : getString(R.string.alipay)));
        if (PAYMODE == WXPAY) {
            mAliPay.getChildAt(0).setVisibility(View.GONE);
            mWxPay.getChildAt(0).setVisibility(View.VISIBLE);
//            mAliPay.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        } else {
            mWxPay.getChildAt(0).setVisibility(View.GONE);
            mAliPay.getChildAt(0).setVisibility(View.VISIBLE);
//     mWxPay.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private ImageView getImageView(RelativeLayout rl, int displayMode) {
        ImageView imageView = new ImageView(this);
        rl.addView(imageView);
        imageView.setVisibility(displayMode);
        imageView.setImageResource(R.drawable.pay_choose);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = 60;
        params.height = 60;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    //充值结果
    public void rechargeResult(boolean isOk, String rechargeMoney) {
        if (isOk) {
            //mCoin.setText((Integer.parseInt(mCoin.getText().toString()) + Integer.parseInt(rechargeMoney) + ""));
            requestData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserDiamonds Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class RechangeAdapter extends BaseAdapter {

        int drawableRes[] = {R.drawable.one_yuan, R.drawable.two_yuan, R.drawable.three_yuan, R.drawable.four_yuan, R.drawable.five_yuan, R.drawable.six_yuan, R.drawable.seven_yuan};

        @Override
        public int getCount() {
            return rechanList.size();
        }

        @Override
        public Object getItem(int position) {
            return rechanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RechargeBean rechargeBean = rechanList.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_select_num, null);
                holder = new ViewHolder();
                holder.mDiamondsNum = (TextView) convertView.findViewById(R.id.tv_diamondsnum);
                holder.mPrieExplain = (TextView) convertView.findViewById(R.id.tv_price_explain);
                holder.mPriceText = (TextView) convertView.findViewById(R.id.bt_preice_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //判断是否是第一次充值,如果不是就隐藏20
            if (!isFirstCharge && position == 0) {
                rechanList.get(0).setRecharDiamondsNum(100);
                rechanList.get(0).setPriceExplain("");
            }


            holder.mDiamondsNum.setText(rechargeBean.getRecharDiamondsNum() + "");
            holder.mPrieExplain.setText(rechargeBean.getPriceExplain());

            holder.mPriceText.setBackgroundResource(drawableRes[position]);
            return convertView;
        }

        private class ViewHolder {
            TextView mDiamondsNum, mPrieExplain;
            TextView mPriceText;

        }

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的播币"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的播币"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
