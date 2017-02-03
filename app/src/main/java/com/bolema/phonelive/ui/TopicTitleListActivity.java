package com.bolema.phonelive.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolema.phonelive.api.remote.ApiUtils;
import com.google.gson.Gson;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

public class TopicTitleListActivity extends ToolBarBaseActivity {

    List<String> mTopicTitleList = new ArrayList<>();
    @InjectView(R.id.sl_topic_title)
    SwipeRefreshLayout mSfTopicTitle;
    @InjectView(R.id.gv_topic_title)
    GridView mGvTopicTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_title_list;
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
    public void initView() {
        setActionBarTitle("今日话题");
        mSfTopicTitle.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mSfTopicTitle.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mTopicTitleList != null){
                    mTopicTitleList.clear();
                }

                initData();
                mSfTopicTitle.setRefreshing(false);
            }
        });

    }



    @Override
    public void initData() {

        requestData();
    }

    private StringCallback getHotTopicCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            mSfTopicTitle.setRefreshing(false);
            AppContext.showToastAppMsg(TopicTitleListActivity.this,"获取最新话题失败");
        }

        @Override
        public void onResponse(String response) {
            mSfTopicTitle.setRefreshing(false);

            String res = ApiUtils.checkIsSuccess(response);

            KLog.json(res);

            if(null != res){
                try {
                    mTopicTitleList.clear();
                    JSONArray resTitleListJsonArr = new JSONArray(res);
                    Gson g = new Gson();
                    for(int i = 0;i<resTitleListJsonArr.length(); i++){
                        mTopicTitleList.add(((JSONObject)resTitleListJsonArr.get(i)).getString("name"));
                    }
                    if(mTopicTitleList.size()>0) {
                        mTopicTitleList.remove(mTopicTitleList.size() - 1);
                    }
                    fillUI();
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        }
    };

    private void fillUI() {

        mGvTopicTitle.setAdapter(new TopicTitleAdapter());
    }

    class   TopicTitleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTopicTitleList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTopicTitleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final  int position, View convertView, ViewGroup parent) {
            TopicViewViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(TopicTitleListActivity.this,R.layout.item_hot_topic,null);
                viewHolder = new TopicViewViewHolder();
                viewHolder.mTvHotTopic = (TextView) convertView.findViewById(R.id.tv_hot_topic);
                viewHolder.mLlTopicTitle=   (LinearLayout) convertView.findViewById(R.id.ll_topic_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (TopicViewViewHolder) convertView.getTag();
            }
            viewHolder.mTvHotTopic.setText(mTopicTitleList.get(position));
            viewHolder.mLlTopicTitle.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(1,new Intent().putExtra("topic",mTopicTitleList.get(position)));
                    finish();

                }
            }));
            return convertView;
        }
    }

    class TopicViewViewHolder{
        public TextView mTvHotTopic;
        public LinearLayout mLlTopicTitle;
    }

    private void requestData() {
        PhoneLiveApi.getTopics("999","2","0",getHotTopicCallback);
    }

    @Override
    public void onClick(View v) {

    }
}
