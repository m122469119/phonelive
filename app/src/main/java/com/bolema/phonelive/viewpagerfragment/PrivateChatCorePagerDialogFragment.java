package com.bolema.phonelive.viewpagerfragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.ViewPageFragmentAdapter;
import com.bolema.phonelive.fragment.FollowPrivateChatFragment;
import com.bolema.phonelive.fragment.NotFollowPrivateChatFragment;
import com.hyphenate.util.DensityUtil;

import org.kymjs.kjframe.utils.DensityUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 *
 * Created by Administrator on 2016/5/26.
 */
public class PrivateChatCorePagerDialogFragment extends DialogFragment implements View.OnClickListener {
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager)
    ViewPager mViewPager;
    @InjectView(R.id.iv_close)
    ImageView mIvBack;

    private com.bolema.phonelive.interf.DialogInterface mDialogInterface;

    private ViewPageFragmentAdapter mTabsAdapter;


    private void onSetupTabAdapter() {

        Bundle b1 = new Bundle();
        b1.putInt("ACTION",1);
        Bundle b2 = new Bundle();
        b2.putInt("ACTION",0);
        mTabsAdapter.addTab(getString(R.string.friends), "friends", FollowPrivateChatFragment.class, b1);
        mTabsAdapter.addTab(getString(R.string.nofollow), "nofollow", NotFollowPrivateChatFragment.class, b2);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(1);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_viewpage_dialog_fragment,null);

        ButterKnife.inject(this,view);
        initData();
        initView();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),R.style.BottomViewTheme_Transparent);
        dialog.setContentView(R.layout.base_viewpage_dialog_fragment);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    public void initData() {

    }

    @OnClick({R.id.iv_private_chat_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_private_chat_back:
                AppContext.showToastAppMsg(getActivity(), "7");
                getActivity().finish();
                break;
        }
    }


    private void initView() {

        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(), mViewPager);

        onSetupTabAdapter();


        mTabStrip.setDividerColor(ContextCompat.getColor(getContext(),R.color.global));
        mTabStrip.setIndicatorColor(ContextCompat.getColor(getContext(),R.color.white));
        mTabStrip.setTextColor(ContextCompat.getColor(getContext(),R.color.tab_text_unselected));
        int height = DensityUtils.getScreenH(getContext());
        int textSize = DensityUtil.sp2px(getContext(), 10) * height / 1280;
        mTabStrip.setTextSize(textSize);
        mTabStrip.setIndicatorHeight(10);
        mTabStrip.setUnderlineColor(ContextCompat.getColor(getContext(),R.color.global));
        mViewPager.setAdapter(mTabsAdapter);
        mTabStrip.setViewPager(mViewPager);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mDialogInterface != null){
            mDialogInterface.cancelDialog(null,null);
        }
    }

    public void setDialogInterface(com.bolema.phonelive.interf.DialogInterface dialogInterface){
        mDialogInterface = dialogInterface;
    }
}
