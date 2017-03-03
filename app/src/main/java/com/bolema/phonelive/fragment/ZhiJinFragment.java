package com.bolema.phonelive.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;

import com.bolema.phonelive.adapter.HotViewPagerAdapter;
import com.bolema.phonelive.adapter.LiveBroadcastAdapter;
import com.bolema.phonelive.adapter.LiveRecordedAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.RecordedLiveBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.bean.ZhijinBannerBean;
import com.bolema.phonelive.utils.DpOrSp2PxUtil;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.SpaceItemDecoration;
import com.bolema.phonelive.widget.WPSwipeRefreshLayout;
import com.bolema.phonelive.widget.materialrefreshlayout.MaterialRefreshLayout;
import com.bolema.phonelive.widget.materialrefreshlayout.MaterialRefreshListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhiJinFragment extends BaseFragment implements ViewPager.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener {


    @InjectView(R.id.zhijin_view_pager)
    ViewPager zhijinViewPager;
    @InjectView(R.id.recycler_live_broadcast)
    RecyclerView recyclerLiveBroadcast;
    @InjectView(R.id.recycler_recorded_broadcast)
    RecyclerView recyclerRecordedBroadcast;


    ArrayList<UserBean> userBeens = new ArrayList<>();
    ArrayList<RecordedLiveBean> recordBeans = new ArrayList<>();
    Gson g = new Gson();

    ArrayList<ZhijinBannerBean> bannerlist = new ArrayList<>();
    @InjectView(R.id.viewPager_container)
    AutoRelativeLayout viewPagerContainer;
    @InjectView(R.id.refreshLayout)
    WPSwipeRefreshLayout refreshLayout;
    @InjectView(R.id.iv_white_point)
    ImageView ivWhitePoint;
    @InjectView(R.id.point_container)
    AutoLinearLayout pointContainer;

    private int mPointDis;

    private Timer timer2 = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 6:
                    int currentItem2 = zhijinViewPager.getCurrentItem();
                    currentItem2++;
                    zhijinViewPager.setCurrentItem(currentItem2);
                    break;
            }
        }
    };
    private LiveBroadcastAdapter liveadapter;
    private LiveRecordedAdapter recordAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhi_jin, container, false);
        ButterKnife.inject(this, view);

        initData();

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.global));
        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    public void initData() {
        PhoneLiveApi.getZhijinBanner(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.getResponse(response);
//                refreshLayout.finishRefresh();
                try {
                    JSONObject data = new JSONObject(res);
                    JSONArray array = data.getJSONArray("info");
                    bannerlist.clear();
                    for (int i = 0; i < array.length(); i++) {
                        bannerlist.add(i, g.fromJson(array.get(i).toString(), ZhijinBannerBean.class));
                    }
                    progressImage();
                    progressLiveBroadcast();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 轮播图加载
     */
    public void progressImage() {

        final ArrayList<View> imageViews = new ArrayList<>();
        ImageView imageView;
        KLog.d(bannerlist.size()+"");
        for (int i = 0; i < bannerlist.size(); i++) {
            View pager = LayoutInflater.from(getContext()).inflate(R.layout.zhijin_page_viewpager, null, false);
            imageView = (ImageView) pager.findViewById(R.id.imageView_pager);
            Glide.with(getContext())
                    .load(bannerlist.get(i).getSlide_pic())
                    .into(imageView);
            //设置轮播图的点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    UIHelper.showWebView(getContext(), imageJumps[finalI], "hehe");
                }
            });
            imageViews.add(pager);
        }
        MyAdapter adapter = new MyAdapter(imageViews);
        zhijinViewPager.setAdapter(adapter);
        // 1.设置幕后item的缓存数目
//        zhijinViewPager.setOffscreenPageLimit(3);
//        // 2.设置页与页之间的间距
//        zhijinViewPager.setPageMargin(8);
        //  3.将父类的touch事件分发至viewPgaer，否则只能滑动中间的一个view对象
//        viewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return zhijinViewPager.dispatchTouchEvent(event);
//            }
//        });

//        zhijinViewPager.addOnPageChangeListener(this);

        zhijinViewPager.setCurrentItem(0);

        if (imageViews.size() > 1) {
            initPoint();
            zhijinViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int newposition = position % imageViews.size();

                    // 更新小白点距离
                    int leftMargin = 10 + (int) (mPointDis * positionOffset) + newposition
                            * mPointDis;// 计算小红点当前的左边距  +10是为了修正小白点的初始位置
                    Log.d("mPointDis", mPointDis + "  " + leftMargin);

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
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 6;
                        handler.sendMessage(message);
                    }
                }, 5000, 5000);
            }





    }

    private void initPoint() {
        int pointcount = bannerlist.size();
        final ImageView[] pointViews = new ImageView[pointcount];
        pointContainer.removeAllViews();
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

    /**
     * 现场直播
     */
    public void progressLiveBroadcast() {

        PhoneLiveApi.getLiveBroadcast(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastShort("网络错误");
                try {
                    refreshLayout.setRefreshing(false);
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.getResponse(response);
                userBeens.clear();
//                refreshLayout.finishRefresh();
//                try {
//                    refreshLayout.setRefreshing(false);
//                } catch (NullPointerException e1) {
//                    e1.printStackTrace();
//                }

                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray array = json.getJSONArray("info");
                    for (int i = 0; i < array.length(); i++) {
                        UserBean userBean = g.fromJson(array.getString(i), UserBean.class);
                        userBeens.add(userBean);
                    }
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 即时录播
     */
    public void progressRecordedLive() {
        PhoneLiveApi.getLiveRecorded(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastShort("网络错误");
                try {
                    refreshLayout.setRefreshing(false);
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.getResponse(response);
                recordBeans.clear();
                try {
                    refreshLayout.setRefreshing(false);
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
                try {
                    JSONObject data = new JSONObject(res);
                    int code = data.getInt("code");
                    if (code == 0) {
                        JSONArray array = data.getJSONArray("info");
                        for (int i = 0; i < array.length(); i++) {
                            recordBeans.add(i, g.fromJson(array.get(i).toString(), RecordedLiveBean.class));
                        }
                        fillRecordUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void fillRecordUI() {
        if (refreshLayout.isRefreshing()) {
            recordAdapter.notifyDataSetChanged();
        } else {
            recordAdapter = new LiveRecordedAdapter(recordBeans, getActivity());
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerRecordedBroadcast.setLayoutManager(manager);
            recyclerRecordedBroadcast.setAdapter(recordAdapter);
//            recyclerRecordedBroadcast.invalidateItemDecorations();
//            recyclerRecordedBroadcast.addItemDecoration(new SpaceItemDecoration((int) DpOrSp2PxUtil.dp2px(getContext(), 4), recordBeans.size()));

        }


    }

    private void fillUI() {
        if (refreshLayout.isRefreshing()) {
            liveadapter.notifyDataSetChanged();
        } else {
            liveadapter = new LiveBroadcastAdapter(userBeens, getContext());
            LinearLayoutManager linearmanage = new LinearLayoutManager(getContext());
            linearmanage.setOrientation(LinearLayoutManager.HORIZONTAL);

            recyclerLiveBroadcast.setLayoutManager(linearmanage);
            recyclerLiveBroadcast.setAdapter(liveadapter);
//            recyclerLiveBroadcast.invalidateItemDecorations();
//            recyclerLiveBroadcast.addItemDecoration(new SpaceItemDecoration((int) DpOrSp2PxUtil.dp2px(getContext(), 4), userBeens.size()));

        }
        progressRecordedLive();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (zhijinViewPager != null) {
            zhijinViewPager.invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRefresh() {
        progressLiveBroadcast();
    }


    // PagerAdapter是object的子类
    private class MyAdapter extends PagerAdapter {

        private int newPosition;
        private ArrayList<View> imageViews;

        MyAdapter(ArrayList<View> imageViews) {
            this.imageViews = imageViews;
        }

        /**
         * PagerAdapter管理数据大小
         */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 关联key 与 obj是否相等，即是否为同一个对象
         */
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj; // key
        }

        /**
         * 销毁当前page的相隔2个及2个以上的item时调用
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object); // 将view 类型 的object熊容器中移除,根据key
        }

        /**
         * 当前的page的前一页和后一页也会被调用，如果还没有调用或者已经调用了destroyItem
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            View imageView = imageViews.get(position);
            // 如果访问网络下载图片，此处可以进行异步加载
//            ImageView img = (ImageView) view.findViewById(R.id.icon);
//            img.setImageBitmap(BitmapFactory.decodeFile(dir + getFile(position)));
            ViewGroup parent = (ViewGroup) imageView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(imageView);
            return imageView; // 返回该view对象，作为key
        }
    }


}
