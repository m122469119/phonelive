package com.bolema.phonelive.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolema.phonelive.adapter.HotViewPagerAdapter;
import com.bolema.phonelive.bean.RollPicBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.TDevice;
import com.bolema.phonelive.widget.SlideshowView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.utils.LiveUtils;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.viewpagerfragment.IndexPagerFragment;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.bolema.phonelive.widget.WPSwipeRefreshLayout;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import rx.Subscription;

/**
 * @author 魏鹏
 * @dw 首页热门
 */
public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.lv_live_room)
    ListView mListUserRoom;
    @InjectView(R.id.refreshLayout)
    WPSwipeRefreshLayout mSwipeRefreshLayout;
    private List<UserBean> mUserList = new ArrayList<>();
    private LayoutInflater inflater;
    private HotUserListAdapter mHotUserListAdapter;
    private Subscription mSubscription;
    private ImageView ivWhitePoint;

    //自定义轮播图的资源
    private String[] imageUrls;
    //跳转连接
    private String[] imageJumps;
    private ViewPager hotViewPager;
    private LinearLayout pointContainer;
    private int mPointDis;
    private Timer timer = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mListUserRoom.addHeaderView(headView);
                    mListUserRoom.setAdapter(mHotUserListAdapter);
                    break;
                case 5:
                    int currentItem = hotViewPager.getCurrentItem();
                    currentItem++;
                    hotViewPager.setCurrentItem(currentItem);
                    break;
            }
        }
    };
    private View headView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_hot,null);


        ButterKnife.inject(this,view);
        this.inflater = inflater;
        initView();
        initData();
        return view;
    }
    private void initView(){
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListUserRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //跳转直播间
                DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
                DataSingleton.getInstance().setPostion(position-1);
                UserBean user = mUserList.get(position-1 );
                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_INFO",user);
                UIHelper.showLookLiveActivity(getActivity(),bundle);
            }
        });
    }

    @Override
    public void initData() {

        //2016.09.06 无数据不显示轮播修改 wp
        mHotUserListAdapter = new HotUserListAdapter();
        headView = inflater.inflate(R.layout.view_hot_rollpic,null);
        pointContainer = (LinearLayout) headView.findViewById(R.id.point_container);
        hotViewPager = (ViewPager) headView.findViewById(R.id.hot_view_pager);
        ivWhitePoint = (ImageView) headView.findViewById(R.id.iv_white_point);
//        int screenWidth = (int) TDevice.getScreenWidth();
        //int sH = (int) (screenWidth / (3/1));
        //slideshowView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // 异步任务获取图片
        PhoneLiveApi.getIndexHotRollpic(pagerCallBack);
    }


    private StringCallback pagerCallBack = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String s) {

            String res = ApiUtils.checkIsSuccess(s);
            try {
                JSONArray rollPics = new JSONArray(res);
                imageUrls = new String[rollPics.length()];
                imageJumps = new String[rollPics.length()];
                if(rollPics == null) return;
                if(rollPics.length() > 0 ){
                    for(int i = 0; i<rollPics.length(); i++){
                        RollPicBean rollPicBean = new Gson().fromJson(rollPics.getJSONObject(i).toString(),RollPicBean.class);
                        imageUrls[i] = rollPicBean.getSlide_pic();
                        imageJumps[i] = rollPicBean.getSlide_url();
                    }
                    processBanner();
                    mHandler.sendEmptyMessage(1);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void processBanner() {
        ArrayList<View> imageLists = new ArrayList<View>();
        ImageView imageView;
        for (int i = 0; i < imageUrls.length; i++) {
            View pager = LayoutInflater.from(getContext()).inflate(R.layout.home_page_viewpager, null,false);
            //设置轮播图，奖轮播图片添加到链表中以便传入适配器
            imageView = (ImageView) pager.findViewById(R.id.imageView_pager);

            Glide.with(getContext())
                    .load(imageUrls[i])
//                    .placeholder(R.drawable.default_pic)
                    .into(imageView);
            //设置轮播图的点击事件
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showWebView(getContext(), imageJumps[finalI],"hehe");
                }
            });
            imageLists.add(pager);
        }
        Log.d("imageSize", imageLists.size() + "");
        hotViewPager.setAdapter(new HotViewPagerAdapter(imageLists));

        hotViewPager.setCurrentItem(imageLists.size()*1000);  //避免用户手动把轮播图向后滑动造成无法滑动的结果，而向后滑动一百年也滑不到头
        initPoint();
        hotViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int newposition = position % imageUrls.length;

                // 更新小白点距离
                int leftMargin = 10 + (int) (mPointDis * positionOffset) + newposition
                        * mPointDis;// 计算小红点当前的左边距  +10是为了修正小白点的初始位置
                Log.d("mPointDis", mPointDis + "  "+ leftMargin);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivWhitePoint
                        .getLayoutParams();
                params.leftMargin = leftMargin;// 修改左边距


                // 重新设置布局参数
                ivWhitePoint.setLayoutParams(params);
                Log.d("params", ((RelativeLayout.LayoutParams) ivWhitePoint.getLayoutParams()).leftMargin + "");
                ivWhitePoint.bringToFront();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 计算两个圆点的距离
        // 移动距离=第二个圆点left值 - 第一个圆点left值
        // measure->layout(确定位置)->draw(activity的onCreate方法执行结束之后才会走此流程)
        // mPointDis = llContainer.getChildAt(1).getLeft()
        // - llContainer.getChildAt(0).getLeft();
        // System.out.println("圆点距离:" + mPointDis);
        // 监听layout方法结束的事件,位置确定好之后再获取圆点间距
        // 视图树
        ivWhitePoint.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 移除监听,避免重复回调
                        ivWhitePoint.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        // layout方法执行结束的回调
                        mPointDis = pointContainer.getChildAt(1).getLeft()
                                - pointContainer.getChildAt(0).getLeft();
                    }
                });


// 设置自动轮播图片，5s后执行，周期是5s
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 5;
                mHandler.sendMessage(message);
            }
        }, 5000, 5000);

    }
    private void initPoint() {
        int pointcount = imageUrls.length;
        final ImageView[] pointViews = new ImageView[pointcount];

        for (int i = 0; i < pointcount; i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // 设置每个小圆点距离左边的间距

            ImageView pointView = new ImageView(getContext());
            // 设置每个小圆点的宽高
            pointView.setBackgroundResource(R.drawable.dot_blur);
            pointView.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
            margin.setMargins(10, 0, 0, 10);
            pointView.setLayoutParams(margin);
            pointContainer.addView(pointView);

        }
    }


    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            //获取首页热门
            PhoneLiveApi.selectTermsScreen(IndexPagerFragment.mSex,IndexPagerFragment.mArea,callback);
        }
    };

    private void fillUI() {

        mListUserRoom.setVisibility(View.VISIBLE);

        if(mSwipeRefreshLayout.isRefreshing()){
            mHotUserListAdapter.notifyDataSetChanged();
        }else{
            mListUserRoom.setAdapter(mHotUserListAdapter);
        }

    }
    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(getActivity(),"获取数据失败请刷新重试~");
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onResponse(String s) {
            mSwipeRefreshLayout.setRefreshing(false);
            mUserList.clear();
            String res = ApiUtils.checkIsSuccess(s);
            try {
                if(res != null){
                    JSONArray resJa = new JSONArray(res);
                    if(resJa.length() > 0){
                        mHotUserListAdapter.notifyDataSetChanged();
                        for(int i = 0;i<resJa.length();i++){
                            UserBean user =new Gson().fromJson(resJa.getJSONObject(i).toString(),UserBean.class);
                            mUserList.add(user);
                        }
                    }
                }
                fillUI();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(IndexPagerFragment.mSex != 0 || !IndexPagerFragment.mArea.equals("")){
            selectTermsScreen(IndexPagerFragment.mSex,IndexPagerFragment.mArea);
        }
        mSubscription = LiveUtils.startInterval(refresh);

    }

    @Override
    public void onPause() {
        super.onPause();
        //关闭定时刷新
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    public void selectTermsScreen(int sex, String area){
        PhoneLiveApi.selectTermsScreen(sex,area,callback);
    }
    //下拉刷新
    @Override
    public void onRefresh() {
        //PhoneLiveApi.getIndexHotUserList(callback);
        PhoneLiveApi.selectTermsScreen(IndexPagerFragment.mSex,IndexPagerFragment.mArea,callback);

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
                convertView = inflater.inflate(R.layout.item_hot_user,null);
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
            KLog.d("avatarURL",user.getAvatar());
            Glide
                    .with(getContext())
                    .load(user.getAvatar())
//                    .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.null_blacklist)
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
         TextView mUserNick,mUserLocal,mUserNums,mRoomTitle;
         ImageView mUserPic;
         AvatarView mUserHead;
    }
}
