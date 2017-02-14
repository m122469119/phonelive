package com.bolema.phonelive.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.AvatarView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.bolema.phonelive.widget.WPSwipeRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * @author 魏鹏
 * @dw 首页热门
 */
public class TopicLiveRoomActivity extends ToolBarBaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.lv_live_room)
    ListView mListUserRoom;
    @InjectView(R.id.refreshLayout)
    WPSwipeRefreshLayout mSwipeRefreshLayout;
    private List<UserBean> mUserList = new ArrayList<>();
    private HotUserListAdapter mHotUserListAdapter;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_live_room;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mListUserRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转直播间
                DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
                DataSingleton.getInstance().setPostion(position );
                UserBean user = mUserList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_INFO", user);
                UIHelper.showLookLiveActivity(TopicLiveRoomActivity.this, bundle);
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle(getIntent().getStringExtra("topic"));
        requestData();
        //2016.09.06 无数据不显示轮播修改 wp
        mHotUserListAdapter = new HotUserListAdapter();
        mListUserRoom.setAdapter(mHotUserListAdapter);

    }


    private void fillUI() {

        mListUserRoom.setVisibility(View.VISIBLE);

        if (mSwipeRefreshLayout.isRefreshing()) {
            mHotUserListAdapter.notifyDataSetChanged();
        } else {
            mListUserRoom.setAdapter(mHotUserListAdapter);
        }

    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(TopicLiveRoomActivity.this, "获取数据失败请刷新重试~");
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onResponse(String s) {
            mSwipeRefreshLayout.setRefreshing(false);

            String res = ApiUtils.checkIsSuccess(s);
            try {
                if (res != null) {
                    JSONArray resJa = new JSONArray(res);

                    if (resJa.length() > 0) {
                        mUserList.clear();
                        for (int i = 0; i < resJa.length(); i++) {
                            UserBean user = new Gson().fromJson(resJa.getJSONObject(i).toString(), UserBean.class);
                            mUserList.add(user);
                        }
                        fillUI();
                    }

                } else {
                    mUserList.clear();
                    mHotUserListAdapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        //关闭定时刷新

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getTopicRooms");
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        //PhoneLiveApi.getIndexHotUserList(callback);
        requestData();

    }

    private void requestData()
    {

        PhoneLiveApi.getTopicRooms(getIntent().getStringExtra("topic"), callback);

    }


    @Override
    public void onClick(View v) {

    }


    private class HotUserListAdapter extends BaseAdapter{

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
            if(convertView == null){
                convertView =  View.inflate(TopicLiveRoomActivity.this,R.layout.item_hot_user,null);
                viewHolder = new ViewHolder();
                viewHolder.mUserNick = (TextView) convertView.findViewById(R.id.tv_live_nick);
                viewHolder.mUserLocal = (TextView) convertView.findViewById(R.id.tv_live_local);
                viewHolder.mUserNums = (TextView) convertView.findViewById(R.id.tv_live_usernum);
                viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
                viewHolder.mUserPic = (ImageView) convertView.findViewById(R.id.iv_live_user_pic);
                viewHolder.mRoomTitle = (TextView) convertView.findViewById(R.id.tv_hot_room_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserBean user = mUserList.get(position);

            viewHolder.mUserNick.setText(user.getUser_nicename());
            viewHolder.mUserLocal.setText(user.getCity());
            //viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());
            //用于加载图片可以平滑滚动
            Glide
                    .with(TopicLiveRoomActivity.this)
                    .load(user.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .fitCenter()
                    .into(viewHolder.mUserPic);
            viewHolder.mUserHead.setAvatarUrl(user.getAvatar());
            viewHolder.mUserNums.setText(String.valueOf(user.getNums()));
            if(null !=user.getTitle()){
                viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
                viewHolder.mRoomTitle.setText(user.getTitle());
            }else{
                viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
                viewHolder.mRoomTitle.setText("");
            }
            return convertView;
        }
    }
    private class ViewHolder{
        public TextView mUserNick,mUserLocal,mUserNums,mRoomTitle;
        public ImageView mUserPic;
        public AvatarView mUserHead;
    }

}
