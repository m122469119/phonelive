package com.bolema.phonelive.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.TopicBean;
import com.bolema.phonelive.bean.VipBean;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.DividerItemDecoration;
import com.bolema.phonelive.widget.RecycleViewDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

public class TopicSquaresActivity  extends ToolBarBaseActivity {

    @InjectView(R.id.rv_topic_list)
    RecyclerView mRvTopicList;

    private List<TopicBean> mTopicList = new ArrayList<>();
    private TopicBean mTopic;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_squares;
    }

    @Override
    public void initView() {
        setActionBarTitle("最新话题");
        mRvTopicList.setLayoutManager(new LinearLayoutManager(this));
        mRvTopicList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRvTopicList.setItemAnimator(new DefaultItemAnimator());
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
    public void initData() {

        requestData();
    }

    private void requestData() {
        PhoneLiveApi.getTopics("999","2","0",getTopicsCallback);
    }
    private StringCallback getTopicsCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            showToast2("获取信息失败,请检查网络设置");
        }

        @Override
        public void onResponse(String response) {
            mTopicList.clear();
            String res = ApiUtils.checkIsSuccess(response);
            Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
            if(res != null){
                try {
                    JSONArray array = new JSONArray(res);

                    for(int i = 0; i < array.length(); i ++){

                        mTopicList.add(g.fromJson(array.getString(i), TopicBean.class));

                        if(mTopicList.size()==8)
                        {
                            break;
                        }

                    }

                    if(mTopicList.size()<8)
                    {
                        for(int i=0;i<8-mTopicList.size();i++)
                        {
                            mTopicList.add(null);
                        }
                    }

                    TopicBean topicMore=new TopicBean();
                    topicMore.setName("更多");
                    topicMore.setThumb("http://pic1.cxtuku.com/00/04/57/b3203a3c47c1.jpg");
                    mTopicList.add(topicMore);

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private void fillUI() {

        mRvTopicList.setAdapter(new TopicAdapter());
    }

    @Override
    public void onClick(View v) {

    }

    class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder>{

        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_topic_square,parent,false);
            TopicViewHolder holder = new TopicViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, final int position) {
            TopicBean topic = mTopicList.get(position);
            if(topic!=null) {
                holder.mTvTopicName.setText(topic.getName());
                Glide.with(TopicSquaresActivity.this)
                        .load(topic.getThumb())
                        .centerCrop()
                        .crossFade()
                        .fitCenter()
                        .into(holder.mIvTopicImage);
                holder.mRlTopicSquares.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemOnclick(position);
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return mTopicList.size();
        }
    }

    private void onItemOnclick(int position) {

        if(position!=8)
        {
            return;
        }

        mTopic = mTopicList.get(position);

        UIHelper.showTopicLiveRoomActivity(this,mTopic.getName());

    }

    class TopicViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRlTopicSquares;
        public TextView mTvTopicName;
        public ImageView mIvTopicImage;
        public TopicViewHolder(View itemView) {
            super(itemView);
            mIvTopicImage = (ImageView) itemView.findViewById(R.id.iv_topic_image);
            mTvTopicName = (TextView) itemView.findViewById(R.id.tv_topic_name);
            mRlTopicSquares = (RelativeLayout) itemView.findViewById(R.id.rl_topic_squares);

        }
    }


}
