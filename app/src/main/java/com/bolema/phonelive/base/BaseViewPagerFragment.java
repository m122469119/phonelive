package com.bolema.phonelive.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.ViewPageFragmentAdapter;
import com.hyphenate.util.DensityUtil;

/**
 * 带有导航条的基类
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ViewPageFragmentAdapter mTabsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_viewpage_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mTabStrip = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabs);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(), mViewPager);
        setScreenPageLimit();
        onSetupTabAdapter(view,mTabsAdapter, mViewPager);
        mTabStrip.setViewPager(mViewPager);
        mTabStrip.setDividerColor(ContextCompat.getColor(getContext(),R.color.global));
        mTabStrip.setIndicatorColor(ContextCompat.getColor(getContext(),R.color.backgroudcolor));
        mTabStrip.setTextColor(ContextCompat.getColor(getContext(),R.color.tab_text_unselected));
        mTabStrip.setTextSize(DensityUtil.sp2px(getContext(), 15));
        mTabStrip.setIndicatorHeight(10);
//        mTabStrip.setSelectedTextColor(ContextCompat.getColor(getContext(),R.color.tab_text_selected));
//        mTabStrip.setUnderlineHeight();
        mTabStrip.setUnderlineColorResource(R.color.global);

        // if (savedInstanceState != null) {
        // int pos = savedInstanceState.getInt("position");
        // mViewPager.setCurrentItem(pos, true);
        // }
    }
    
    protected void setScreenPageLimit() {
    }

    // @Override
    // public void onSaveInstanceState(Bundle outState) {
    // //No call for super(). Bug on API Level > 11.
    // if (outState != null && mViewPager != null) {
    // outState.putInt("position", mViewPager.getCurrentItem());
    // }
    // //super.onSaveInstanceState(outState);
    // }

    protected abstract void onSetupTabAdapter(View view,ViewPageFragmentAdapter adapter,ViewPager viewPager);
}