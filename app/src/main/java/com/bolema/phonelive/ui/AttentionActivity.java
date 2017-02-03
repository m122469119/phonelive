package com.bolema.phonelive.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bolema.phonelive.adapter.UserBaseInfoAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 关注列表
 */
public class AttentionActivity extends ToolBarBaseActivity {
    @InjectView(R.id.lv_attentions)
    ListView mAttentionView;
    @InjectView(R.id.sr_refresh)
    SwipeRefreshLayout mRefreshLayout;
    private List<UserBean> mAttentionList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attention;
    }

    @Override
    public void initView() {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mAttentionList != null){
                    mAttentionList.clear();
                }

                initData();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void initData() {
        setActionBarTitle(getResources().getString(R.string.attention));
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(res != null){
                    try {
                        JSONArray fansJsonArr = new JSONArray(res);
                        if(fansJsonArr.length() > 0){
                            Gson gson = new Gson();
                            mAttentionList = new ArrayList<>();
                            for(int i =0;i<fansJsonArr.length(); i++){
                                mAttentionList.add(gson.fromJson(fansJsonArr.getString(i), UserBean.class));
                            }
                            fillUI();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getAttentionList(getIntent().getIntExtra("uid", 0), AppContext.getInstance().getLoginUid(), callback);
    }

    private void fillUI() {
        try {
            mAttentionView.setAdapter(new UserBaseInfoAdapter(mAttentionList));
            mAttentionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UIHelper.showHomePageActivity(AttentionActivity.this,mAttentionList.get(position).getId());
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

    }


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("关注列表"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("关注列表"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
