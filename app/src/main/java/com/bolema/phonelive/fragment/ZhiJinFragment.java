package com.bolema.phonelive.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.HotViewPagerAdapter;
import com.bolema.phonelive.adapter.LiveBroadcastAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.UserBean;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhiJinFragment extends BaseFragment implements ViewPager.OnPageChangeListener{


    @InjectView(R.id.zhijin_view_pager)
    ViewPager zhijinViewPager;
    @InjectView(R.id.recycler_live_broadcast)
    RecyclerView recyclerLiveBroadcast;
    @InjectView(R.id.recycler_recorded_broadcast)
    RecyclerView recyclerRecordedBroadcast;

    ArrayList<View> imageViews = new ArrayList<>();
    ArrayList<UserBean> userBeens = new ArrayList<>();

    String[] imageUrls = {"http://bolema.wanchuangzhongchou.com/data/upload/20170122/58844c5ce8783.jpg",
            "http://bolema.wanchuangzhongchou.com/data/upload/20170122/58844e8d76cd7.jpg",
            "http://bolema.wanchuangzhongchou.com/data/upload/20170122/58844e98683c8.jpg",
            "http://bolema.wanchuangzhongchou.com/data/upload/20170122/58844ea71b2ff.jpg",
            "http://bolema.wanchuangzhongchou.com/data/upload/20170122/58844ea71b2ff.jpg"};
    @InjectView(R.id.viewPager_container)
    AutoLinearLayout viewPagerContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhi_jin, container, false);
        ButterKnife.inject(this, view);
        progressImage();
        progressLiveBroadcast();
        return view;
    }


    /**
     * 轮播图加载
     */
    public void progressImage() {
        for (int i = 0; i < imageUrls.length; i++) {
            View pager = LayoutInflater.from(getContext()).inflate(R.layout.zhijin_page_viewpager, null,false);
            ImageView imageView = (ImageView) pager.findViewById(R.id.imageView_pager);
            Glide.with(getContext())
                    .load(imageUrls[i])
                    .into(imageView);
            imageViews.add(pager);
        }
        MyAdapter adapter = new MyAdapter(imageViews);
        // 1.设置幕后item的缓存数目
        zhijinViewPager.setOffscreenPageLimit(3);
        // 2.设置页与页之间的间距
        zhijinViewPager.setPageMargin(8);
       //  3.将父类的touch事件分发至viewPgaer，否则只能滑动中间的一个view对象
        viewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return zhijinViewPager.dispatchTouchEvent(event);
            }
        });
        zhijinViewPager.setAdapter(adapter);
        zhijinViewPager.addOnPageChangeListener(this);
//        zhijinViewPager.setCurrentItem(imageViews.size()*100);

    }

    /**
     * 现场直播
     */
    public void progressLiveBroadcast(){
        PhoneLiveApi.getLiveBroadcast(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastShort("网络错误");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.getResponse(response);
                Gson g = new Gson();
                try {
                    JSONObject json = new JSONObject(res);
                    JSONArray array = json.getJSONArray("info");
                    for (int i=0; i<array.length();i++) {
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

    private void fillUI() {
        LiveBroadcastAdapter liveadapter = new LiveBroadcastAdapter(userBeens,getContext());
        LinearLayoutManager linearmanage = new LinearLayoutManager(getContext());
        linearmanage.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerLiveBroadcast.setLayoutManager(linearmanage);
        recyclerLiveBroadcast.setAdapter(liveadapter);
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














    // PagerAdapter是object的子类
    class MyAdapter extends PagerAdapter {

        private int newPosition;
        private ArrayList<View> imageViews;
        public MyAdapter(ArrayList<View> imageViews) {
            this.imageViews = imageViews;
        }
        /**
         * PagerAdapter管理数据大小
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
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
            System.out.println("pos:" + position);
            newPosition = position % imageViews.size();

            View view = imageViews.get(newPosition);
            // 如果访问网络下载图片，此处可以进行异步加载
//            ImageView img = (ImageView) view.findViewById(R.id.icon);
//            img.setImageBitmap(BitmapFactory.decodeFile(dir + getFile(position)));
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(view);
            return imageViews.get(newPosition); // 返回该view对象，作为key
        }
    }
}
