package com.bolema.phonelive.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.utils.DpOrSp2PxUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.TopicBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.ui.TopicSquaresActivity;
import com.bolema.phonelive.ui.TopicTitleListActivity;
import com.bolema.phonelive.utils.LiveUtils;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.viewpagerfragment.IndexPagerFragment;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.bolema.phonelive.widget.MyGridView;
import com.bolema.phonelive.widget.WPSwipeRefreshLayout;
import com.socks.library.KLog;
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
 *
 */
public class TopicSquaresFragment extends BaseFragment   {
    List<TopicBean> mTopicSquares= new ArrayList<>();
    @InjectView(R.id.sl_topic_squares)
    SwipeRefreshLayout mSfTopicSquares;
    @InjectView(R.id.gv_topic_squares)
    MyGridView mGvTopicSquares;
    private LayoutInflater inflater;
    private TopicBean mTopic;
    private TopicAdapter topicAdapter;
    List<TopicBean> mTempTopicSquares= new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_topic_squares,null);
        ButterKnife.inject(this,view);
        this.inflater = inflater;
        initView();
        initData();
        return view;
    }
    private void initView(){
        mSfTopicSquares.setColorSchemeColors(getResources().getColor(R.color.actionbarbackground));
        mSfTopicSquares.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mTopicSquares != null){
                    mTopicSquares.clear();
                }
                initData();
                if(mSfTopicSquares!=null)
                {
                    mSfTopicSquares.setRefreshing(false);
                }
            }
        });

        mGvTopicSquares. setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if(position==mTopicSquares.size()-1)
                {
                    UIHelper.showTopicListActivity(getActivity());

                }else{
                    mTopic = mTopicSquares.get(position);
                    UIHelper.showTopicLiveRoomActivity(getActivity(),mTopic.getName());
                }*/
                mTopic = mTopicSquares.get(position);
                UIHelper.showTopicLiveRoomActivity(getActivity(),mTopic.getName());
            }
        });

    }


    @Override
    public void initData() {
        topicAdapter = new TopicAdapter();
        mGvTopicSquares.setAdapter(topicAdapter);
        requestData();
    }

    private void requestData() {
        PhoneLiveApi.getTopics("999","2","0",getTopicsCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mSfTopicSquares.setPadding(0,0,0, DpOrSp2PxUtil.dp2pxConvertInt(getActivity(),50));
    }

    private StringCallback getTopicsCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            mSfTopicSquares.setRefreshing(false);
            Toast.makeText(getActivity(),"获取信息失败,请检查网络设置",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResponse(String response) {
            mSfTopicSquares.setRefreshing(false);
            String res = ApiUtils.checkIsSuccess(response);

            KLog.json(res);

            Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
            if(res != null){
                try {
                    JSONArray array = new JSONArray(res);
                    //topicAdapter.notifyDataSetChanged();
                    mTempTopicSquares.clear();
                    for(int i = 0; i < array.length(); i ++){
                        mTempTopicSquares.add(g.fromJson(array.getString(i), TopicBean.class));
                    }
                    mTopicSquares.clear();
                    mTopicSquares.addAll(mTempTopicSquares);
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private void fillUI() {
        //topicAdapter = new TopicAdapter();
        //mGvTopicSquares.setAdapter(topicAdapter);
        topicAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

    }

    class TopicAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return mTopicSquares.size();
        }

        @Override
        public Object getItem(int position) {
            return mTopicSquares.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final  int position, View convertView, ViewGroup parent) {
            TopicViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new  TopicViewHolder();
                convertView = View.inflate(getActivity(),R.layout.item_topic_square,null);
                viewHolder.mIvTopicImage = (ImageView) convertView.findViewById(R.id.iv_topic_image);
                viewHolder.mTvTopicName = (TextView) convertView.findViewById(R.id.tv_topic_name);
                viewHolder.mRlTopicSquares = (RelativeLayout) convertView.findViewById(R.id.rl_topic_squares);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (TopicViewHolder) convertView.getTag();
            }

            //if(position>=0&&position<=mTopicSquares.size()-1) {
                TopicBean topic = mTopicSquares.get(position);
                if (topic != null) {
                    viewHolder.mTvTopicName.setText(topic.getName());
                    Glide.with(getActivity())
                            .load(topic.getThumb())
                            .centerCrop()
                            .crossFade()
                            .fitCenter()
                            .into(viewHolder.mIvTopicImage);
                }
            //}

            return convertView;
        }


    }



    class TopicViewHolder   {

        public RelativeLayout mRlTopicSquares;
        public TextView mTvTopicName;
        public ImageView mIvTopicImage;

    }

}
