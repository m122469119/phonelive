package com.bolema.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.ui.HomePageActivity;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.HeaderGridView;
import com.bolema.phonelive.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.utils.LiveUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import rx.Subscription;

/**
 * 首页最新直播
 */
public class NewestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    List<UserBean> mUserList = new ArrayList<>();
    List<String> mHotTopicList = new ArrayList<>();
    @InjectView(R.id.gv_newest)
    HeaderGridView mNewestLiveView;
    @InjectView(R.id.sl_newest)
    SwipeRefreshLayout mRefresh;
    private int wh;
    private Subscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newest, null);
        ButterKnife.inject(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void initData() {

    }


    //最新主播数据请求
    private void requestData() {
        //PhoneLiveApi.getTopics("4", "2", getHotTopicCallback);
        PhoneLiveApi.getNewestUserList(callback);
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            mRefresh.setRefreshing(false);
            AppContext.showToastAppMsg(getActivity(), "获取最新直播失败");
        }

        @Override
        public void onResponse(String response) {

            if(mRefresh!=null)
            {
                mRefresh.setRefreshing(false);
            }

            String res = ApiUtils.checkIsSuccess(response);
            if (null != res) {
                try {
                    mUserList.clear();
                    JSONArray resUserListJsonArr = new JSONArray(res);
                    Gson g = new Gson();
                    for (int i = 0; i < resUserListJsonArr.length(); i++) {
                        mUserList.add(g.fromJson(resUserListJsonArr.getString(i), UserBean.class));
                    }
                    fillUI();
                } catch (JSONException e) {
                    mNewestLiveView.setAdapter(new NewestAdapter());//BBB
                    e.printStackTrace();
                }

            }

        }
    };

    private void fillUI() {
        if (getActivity() != null) {
            //设置每个主播宽度
            int w = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            wh = w / 3;
            mNewestLiveView.setColumnWidth(wh);
            NewestAdapter newestAdapter = new NewestAdapter();
            mNewestLiveView.setAdapter(newestAdapter);
            newestAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void initView(View view) {
        mNewestLiveView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
                int currentPostion = position-mHotTopicList.size();
                DataSingleton.getInstance().setPostion(currentPostion);
                UserBean user = mUserList.get(currentPostion);
                PhoneLiveApi.isStillLiving(String.valueOf(user.getUid()), stillcallback);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("USER_INFO", user);
//                UIHelper.showLookLiveActivity(getActivity(), bundle);
            }
        });
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);

    }

    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            requestData();
        }
    };

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("最新直播"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
        requestData();
        mSubscription = LiveUtils.startInterval(refresh);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("最新直播"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class NewestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_newest_user, null);
                viewHolder = new ViewHolder();
                viewHolder.mUHead = (RoundImageView) convertView.findViewById(R.id.iv_newest_item_user);
                viewHolder.mUHead.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, wh));
                viewHolder.mUText = (TextView) convertView.findViewById(R.id.tv_nicheng);
                viewHolder.mUDistance = (TextView) convertView.findViewById(R.id.tv_distance); //HHH 2016-09-09
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            UserBean u = mUserList.get(position);
            viewHolder.mUText.setText(u.getUser_nicename());

            /*String distanceStr = "";

            if(u.getDistance()!=null) {
                double distance = Double.parseDouble(u.getDistance());
                if (distance < 100) {
                    distanceStr = "100米以内";
                } else {
                    distance = Math.round(distance / 100d) / 10d;
                    distanceStr = distance + "km";
                }
            }else {
                distanceStr="好像在火星";
            }*/
            viewHolder.mUDistance.setText(u.getDistance()); //HHH 2016-09-09

            Glide.with(NewestFragment.this)
                    .load(u.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .fitCenter()
                    .into(viewHolder.mUHead);
            return convertView;
        }

        class ViewHolder {
            RoundImageView mUHead;
            TextView mUText;
            TextView mUDistance; //HHH 2016-09-09
        }
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
            String res = ApiUtils.checkIsSuccess(response);
            KLog.json(res);


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
                        UIHelper.showLookLiveActivity(getActivity(), bundle);
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
