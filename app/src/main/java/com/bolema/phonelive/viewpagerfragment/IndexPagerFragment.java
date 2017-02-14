package com.bolema.phonelive.viewpagerfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.  LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bolema.phonelive.broadcast.BroadCastManager;
import com.bolema.phonelive.fragment.HotFragment;
import com.bolema.phonelive.utils.UIHelper;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.ViewPageFragmentAdapter;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.fragment.AttentionFragment;
import com.bolema.phonelive.fragment.NewestFragment;
import com.bolema.phonelive.fragment.TopicSquaresFragment;
import com.bolema.phonelive.interf.ListenMessage;
import com.bolema.phonelive.interf.PagerSlidingInterface;
import com.bolema.phonelive.ui.other.PhoneLivePrivateChat;
import com.bolema.phonelive.widget.PagerSlidingTabStrip;

import org.kymjs.kjframe.utils.DensityUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IndexPagerFragment extends BaseFragment implements ListenMessage{

    private View view;
    @InjectView(R.id.mviewpager)
    public ViewPager pager;


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;


    @InjectView(R.id.fl_tab_container)
    FrameLayout flTabContainer;

    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;

    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    public static int mSex = 0;

    public static String mArea = "";

    //是否在后台
    private boolean isPause = false;

    private  EMMessageListener mMsgListener;
    private LocalReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null){
            view = inflater.inflate(R.layout.fragment_hot,null);
            ButterKnife.inject(this,view);

            initView();
            initData();

        }else{
            mIvNewMessage.setVisibility(View.GONE);
            tabs.setViewPager(pager);
        }

//        int widthPixels =   getResources().getDisplayMetrics().widthPixels;
//        int scale =  widthPixels/720;

        //flTabContainer.setLayoutParams(new ViewGroup.LayoutParams(DpOrSp2PxUtil.dp2pxConvertInt(getActivity(),scale*270), ViewGroup.LayoutParams.MATCH_PARENT));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("skip_to_pager_1");
        receiver = new LocalReceiver();
        BroadCastManager.getInstance().registerReceiver(getActivity(), receiver, intentFilter);

        return view;
    }

    @Override
    public void initData() {
        //获取私信未读数量
        if(PhoneLivePrivateChat.getUnreadMsgsCount() > 0){
            mIvNewMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //注册私信广播接受
        isPause = false;
        listenMessage();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        unListen();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick({R.id.iv_hot_private_chat,R.id.iv_hot_search})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_hot_private_chat:
                int uid = AppContext.getInstance().getLoginUid();
                if(0 < uid){
                    mIvNewMessage.setVisibility(View.GONE);
                    UIHelper.showPrivateChatSimple(getActivity(),uid);
                }
                break;
            case R.id.iv_hot_search:
                UIHelper.showScreen(getActivity());
                break;
        }
    }

    private void initView() {
        mIvNewMessage.setVisibility(View.GONE);
        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getFragmentManager(),pager);
        viewPageFragmentAdapter.addTab(getString(R.string.attention), "gz", AttentionFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.hot), "rm", HotFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.daren), "dr", NewestFragment.class, getBundle());
        viewPageFragmentAdapter.addTab("话题", "ht", TopicSquaresFragment.class, getBundle());

        pager.setAdapter(viewPageFragmentAdapter);

        pager.setOffscreenPageLimit(2);

        tabs.setViewPager(pager);
        tabs.setUnderlineColor(ContextCompat.getColor(getContext(),R.color.global));
        tabs.setDividerColor(ContextCompat.getColor(getContext(),R.color.global));
        tabs.setIndicatorColor(ContextCompat.getColor(getContext(),R.color.backgroudcolor));
        tabs.setTextColor(ContextCompat.getColor(getContext(),R.color.tab_text_unselected));
        //适配指示器文字大小
        int height = DensityUtils.getScreenH(getContext());
        int textSize = 32 * height / 1280;
        tabs.setTextSize(textSize);

        tabs.setSelectedTextColor(ContextCompat.getColor(getContext(),R.color.tab_text_selected));
        tabs.setIndicatorHeight(2);
        tabs.setZoomMax(0);
        tabs.setIndicatorColorResource(R.color.white);
        tabs.setPagerSlidingListen(new PagerSlidingInterface() {
            @Override
            public void onItemClick(View v,int currentPosition,int position) {

                if(currentPosition == position && position == 1){
                    UIHelper.showSelectArea(getActivity());
                }
            }
        });

        pager.setCurrentItem(1);




        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void listenMessage(){

        mMsgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvNewMessage.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMsgListener);

    }
    public void unListen(){
        EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
    }



    @Override
    public void onDestroy() {
        //注销广播
        super.onDestroy();
        unListen();

    }

    private Bundle getBundle( ) {
       Bundle bundle = new Bundle();

       return bundle;
   }


   public   class LocalReceiver extends BroadcastReceiver{

         @Override
         public void onReceive(Context context, Intent intent) {
//             Bundle bundle = intent.getExtras();
             if (intent.getAction().equals("skip_to_pager_1")) {
                 pager.setCurrentItem(1);
             }
//             getContext().unregisterReceiver(this);
//             BroadCastManager.getInstance().unregisterReceiver(getActivity(), receiver);
         }

     }
}
