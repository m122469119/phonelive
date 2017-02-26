package com.bolema.phonelive.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.HotViewPagerAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 轮播图加载
 * Created by yuanshuo on 2017/2/24.
 */

public class ProcessBanner {
    private Timer timer = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局
    private int mPointDis;

    private ImageView ivWhitePoint;
    private  ViewGroup pointContainer;
    private Handler handler;
    private String[] imageUrls;
    private Context context;
    private String[] imageJumps;

    /**
     * @param context
     * @param ivWhitePoint   指示点位置，不一定白色
     * @param pointContainer 指示点容器
     * @param handler        消息传递
     * @param imageUrls      轮播图链接
     * @param imageJumps     轮播图跳转链接
     */
    public ProcessBanner(Context context, ImageView ivWhitePoint, ViewGroup pointContainer, Handler handler, String[] imageUrls, String[] imageJumps) {
        this.ivWhitePoint = ivWhitePoint;
        this.pointContainer = pointContainer;
        this.handler = handler;
        this.imageUrls = imageUrls;
        this.context = context;
        this.imageJumps = imageJumps;
    }

    public void processBanner(ViewPager viewPager, LayoutInflater layoutInflater) {
        ArrayList<View> imageLists = new ArrayList<View>();
        ImageView imageView;
        for (int i = 0; i < imageUrls.length; i++) {

            View pager = layoutInflater.inflate(R.layout.home_page_viewpager, null, false);
            //设置轮播图，奖轮播图片添加到链表中以便传入适配器
            imageView = (ImageView) pager.findViewById(R.id.imageView_pager);

            Glide.with(context)
                    .load(imageUrls[i])
//                    .placeholder(R.drawable.default_pic)
                    .into(imageView);
            //设置轮播图的点击事件
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showWebView(context, imageJumps[finalI], "hehe");
                }
            });
            imageLists.add(pager);
        }

        viewPager.setAdapter(new HotViewPagerAdapter(imageLists));

        viewPager.setCurrentItem(imageLists.size() * 1000);  //避免用户手动把轮播图向后滑动造成无法滑动的结果，而向后滑动一百年也滑不到头
        int pointcount = imageUrls.length;
        final ImageView[] pointViews = new ImageView[pointcount];

        for (int i = 0; i < pointcount; i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // 设置每个小圆点距离左边的间距

            ImageView pointView = new ImageView(context);
            // 设置每个小圆点的宽高
            pointView.setBackgroundResource(R.drawable.dot_blur);
            pointView.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
            margin.setMargins(10, 0, 0, 10);
            pointView.setLayoutParams(margin);
            pointContainer.addView(pointView);

        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int newposition = position % imageUrls.length;

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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 5;
                handler.sendMessage(message);
            }
        }, 5000, 5000);

    }

}
