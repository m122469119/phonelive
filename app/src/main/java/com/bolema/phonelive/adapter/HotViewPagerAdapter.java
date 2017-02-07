package com.bolema.phonelive.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * viewpager适配器
 * Created by venus on 2016/6/28.
 */
public class HotViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> imageViews;
    private int newPosition;
    public HotViewPagerAdapter(ArrayList<View> imageViews) {
        this.imageViews = imageViews;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        newPosition = position % imageViews.size();
        View imageView = imageViews.get(newPosition);
        ViewGroup parent = (ViewGroup) imageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}