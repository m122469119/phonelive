package com.bolema.phonelive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.hyphenate.chat.EMMessage;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.MessageAdapter;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.bean.PrivateMessage;
import com.bolema.phonelive.ui.other.PhoneLivePrivateChat;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


//个人中心私信发送页面
public class MessageDetailFragment extends BaseFragment {
    @InjectView(R.id.tv_private_chat_title)
    TextView mTitle;
    @InjectView(R.id.et_private_chat_message)
    EditText mMessageInput;
    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;
    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;
    private BroadcastReceiver broadCastReceiver;
    private long lastTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_chat_message,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    @OnClick({R.id.iv_private_chat_send,R.id.et_private_chat_message,R.id.iv_private_chat_back,R.id.iv_private_chat_user})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发送私信
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

                break;
            case R.id.iv_private_chat_back:
                getActivity().finish();
                break;
            case R.id.iv_private_chat_user:
                UIHelper.showHomePageActivity(getActivity(),mToUser.getId());
                break;
        }

    }
    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0){
            Toast.makeText(getActivity(),"操作频繁",Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = System.currentTimeMillis();
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
            return;
        }
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
        }
        EMMessage emMessage = PhoneLivePrivateChat.sendChatMessage(mMessageInput.getText().toString(),mToUser);

        //更新列表
        updateChatList(PrivateMessage.crateMessage(emMessage,mUser.getAvatar()));
        mMessageInput.setText("");
    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = getActivity().getIntent().getParcelableExtra("user");
        Log.d("chatbean",mToUser.toString());
        mTitle.setText(mToUser.getUser_nicename());

        //获取历史消息
        mChats = PhoneLivePrivateChat.getUnreadRecord(mUser,mToUser);

        //初始化adapter
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setChatList(mChats);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

        initBroadCast();
    }

    //注册监听私信消息广播
    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.bolema.phonelive");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    final EMMessage emMessage = intent.getParcelableExtra("cmd_value");
                    //判断是否是当前回话的消息
                    if(emMessage.getFrom().trim().equals(String.valueOf(mToUser.getId()))) {

                        updateChatList(PrivateMessage.crateMessage(emMessage,mToUser.getAvatar()));

                    }
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }

    @Override
    public void initView(View view) {

    }
    private void updateChatList(PrivateMessage message){
        //更新聊天列表
        mMessageAdapter.addMessage(message);
        //mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mMessageAdapter.getCount()-1);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("私信聊天页面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长

    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadCastReceiver);
        }catch (Exception ignored){

        }

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("私信聊天页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }


}
