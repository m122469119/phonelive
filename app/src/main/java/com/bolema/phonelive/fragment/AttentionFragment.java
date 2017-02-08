package com.bolema.phonelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.LiveUserAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.broadcast.BroadCastManager;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.ui.VideoPlayerActivity;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.viewpagerfragment.IndexPagerFragment;
import com.google.gson.Gson;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 首页左边关注
 */
public class AttentionFragment extends BaseFragment {
    @InjectView(R.id.lv_attentions)
    ListView mLvAttentions;
    List<UserBean> mUserList = new ArrayList<>();
    @InjectView(R.id.mSwipeRefreshLayout)
    SwipeRefreshLayout mRefresh;


    //默认提示
//    @InjectView(R.id.tv_attention)
//    TextView mTvPrompt;
    @InjectView(R.id.btn_look_at_other_live)
    Button btnLookAtOtherLive;
    @InjectView(R.id.layout_attention)
    AutoLinearLayout layoutAttention;

    private View view;
    private LiveUserAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_attention, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserList.clear();
                initData();
            }
        });
        btnLookAtOtherLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("skip_to_pager_1");
                BroadCastManager.getInstance().sendBroadCast(getActivity(), intent);
            }
        });
    }

    @Override
    public void initData() {
        int uid = AppContext.getInstance().getLoginUid();
        PhoneLiveApi.getAttentionLive(AppContext.getInstance().getLoginUid(), callback);


    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
        }

        @Override
        public void onResponse(String response) {
            if (mRefresh.isRefreshing()) {
                mRefresh.setRefreshing(false);
            }
            String res = ApiUtils.checkIsSuccess(response);
            if (null != res) {

                try {
                    JSONArray liveAndAttentionUserJson = new JSONObject(res).getJSONArray("attentionlive");
                    if (0 == liveAndAttentionUserJson.length()) {
                        layoutAttention.setVisibility(View.VISIBLE);
                        mUserList.clear();
                        try {
                            mAdapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                    Gson g = new Gson();

                    for (int i = 0; i < liveAndAttentionUserJson.length(); i++) {
                        mUserList.add(g.fromJson(liveAndAttentionUserJson.getString(i), UserBean.class));
                    }

                    fillUI();
                } catch (JSONException e) {
                    mAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);
                    mLvAttentions.setAdapter(mAdapter);
                    e.printStackTrace();
                }
            }
        }
    };


    private void fillUI() {
        layoutAttention.setVisibility(View.GONE);
        mLvAttentions.setVisibility(View.VISIBLE);
        if (getActivity() != null) {
            mAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);
            mLvAttentions.setAdapter(mAdapter);
            mLvAttentions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
                    DataSingleton.getInstance().setPostion(position);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(VideoPlayerActivity.USER_INFO, mUserList.get(position));
                    UIHelper.showLookLiveActivity(getActivity(), bundle);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}