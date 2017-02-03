package com.bolema.phonelive.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.OrderAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.OrderBean;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.CircleImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.Core;
import java.util.ArrayList;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 贡献排行榜
 */
public class DedicateOrderActivity extends ToolBarBaseActivity {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    @InjectView(R.id.lv_order)
    ListView mOrderListView;
    @InjectView(R.id.tv_order_total)
    TextView mOrderTotal;
    private String mTotal;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void initView() {

    }
    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.yporder));
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(null != res){
                    try {
                        JSONObject resJsonObj = new JSONObject(res);
                        Gson g = new Gson();
                        JSONArray uLsit = resJsonObj.getJSONArray("list");
                        mTotal = resJsonObj.getString("total");
                        for(int i = 0;i<uLsit.length(); i++){
                            OrderBean orderBean = g.fromJson(uLsit.getString(i), OrderBean.class);
                            orderBean.setOrderNo(String.valueOf(i));
                            mOrderList.add(orderBean);

                        }

                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getYpOrder(getIntent().getIntExtra("uid", 0), callback);

    }

    private void fillUI() {
        mOrderTotal.setText(mTotal + getString(R.string.yingpiao));
        if(0 < mOrderList.size()){
            mOrderListView.setAdapter(new OrderAdapter(mOrderList,getLayoutInflater(),this));
            mOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UIHelper.showHomePageActivity(DedicateOrderActivity.this,mOrderList.get(position).getUid());
                }
            });
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("贡献榜"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("贡献榜"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getYpOrder");//BBB
    }
}
