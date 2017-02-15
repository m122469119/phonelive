package com.bolema.phonelive.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bolema.phonelive.adapter.UserBaseInfoPrivateChatAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.fragment.MessageDetailDialogFragment;
import com.bolema.phonelive.utils.TLog;
import com.bolema.phonelive.utils.UIHelper;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.interf.DialogInterface;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;


public abstract class PrivateChatPageBase extends BaseFragment {
    private BroadcastReceiver broadCastReceiver;

    //回话列表
    protected ArrayList<PrivateChatUserBean> mPrivateChatListData = new ArrayList<>();
    protected ListView mPrivateListView;
    protected int mPosition = -1;
    protected UserBean mUser;
    protected Map<String, EMConversation> emConversationMap;
    protected Gson mGson = new Gson();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_private_chat,null);
        initCreateView(inflater,container,savedInstanceState);

        ButterKnife.inject(this,v);

        initBroadCast();

        initView(v);

        initData();
        return v;
    }

    @Override
    public void initView(View view) {
        mPrivateListView = (ListView) view.findViewById(R.id.lv_privatechat);


        mPrivateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPrivateChatItemClick(position);
            }
        });
    }

    //回话点击时间
    private void onPrivateChatItemClick(int position) {
        mPosition = position;
        PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(position);
        //标注私信状态为已读
        mPrivateChatListData.get(position).setUnreadMessage(false);

        updatePrivateChatList();

        //判断当前页面是直播间弹窗还是单页面
        if(getParentFragment() instanceof DialogFragment){
            MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("user",privateChatUserBean);
            messageFragment.setArguments(bundle);

            messageFragment.show(getFragmentManager(),"MessageDetailDialogFragment");
            messageFragment.setDialogInterface(new DialogInterface() {
                @Override
                public void cancelDialog(View v, Dialog d) {
                    onResumeUpdate();
                }

                @Override
                public void determineDialog(View v, Dialog d) {

                }
            });
        }else {
            UIHelper.showPrivateChatMessage(getActivity(),privateChatUserBean);
        }
    }


    //注册监听私信消息广播
    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.bolema.phonelive");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub

                    onNewMessage((EMMessage)intent.getParcelableExtra("cmd_value"));

                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("未关注私信"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
        onResumeUpdate();


    }

    public void onResumeUpdate(){
        if(null == mPrivateChatListData || mPrivateChatListData.size() == 0) return;
        //获取有没有最后一条消息
        try {
            //将该条会话消息未读清零
            EMConversation conversation = EMClient.getInstance()
                    .chatManager()
                    .getConversation(String.valueOf(mPrivateChatListData.get(mPosition).getId()));
            conversation.markAllMessagesAsRead();
            //设置最后一条消息
            mPrivateChatListData.get(mPosition).setLastMessage(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());

        }catch (Exception e){
            //无最后一条消息
        }
        mPosition = -1;
        updatePrivateChatList();
    }
    //更新回话列表
    protected void updatePrivateChatList(){
        mPrivateListView.setAdapter(new UserBaseInfoPrivateChatAdapter(mPrivateChatListData));
    }

    //初始化会话列表
    protected void initConversationList(int action) {
        //获取所有会话消息列表
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();

        StringBuilder keys = new StringBuilder();
        for(String key : emConversationMap.keySet()){
            keys.append(key).append(",");
        }
        if(keys.toString().length() == 0)return;
        String uidList = keys.toString().substring(0,keys.length()-1);

        //获取每个绘画用户的信息和关注状态

        PhoneLiveApi.getMultiBaseInfo(action,mUser.getId(),uidList,multiBaseInfoCallback);

    }


    protected void fillUI() {
        if(mPrivateChatListData == null){
            return;
        }
        updatePrivateChatList();
    }

    private StringCallback multiBaseInfoCallback = new StringCallback(){

        @Override
        public void onError(Call call, Exception e) {

            TLog.log("[获取会话列表用户信息error]:" + call.request().toString());
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            TLog.log("[获取会话列表用户信息success]:" + res);
            KLog.json(res);

            if(null != res){
                try {
                    JSONArray fansJsonArr = new JSONArray(res);
                    if(fansJsonArr.length() > 0){
                        Gson gson = new Gson();
                        for(int i =0;i<fansJsonArr.length(); i++){

                            PrivateChatUserBean chatUserBean = gson.fromJson(fansJsonArr.getString(i), PrivateChatUserBean.class);
                            //获取单条会话信息
                            EMConversation conversation = EMClient.getInstance()
                                    .chatManager()
                                    .getConversation(String.valueOf(chatUserBean.getId()));
                            try {
                                //设置该会话最后一条信息
                                chatUserBean.setLastMessage(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
                                Log.d("messageBean", chatUserBean.getLastMessage());
                                //获取该会话是否有未读消息
                                chatUserBean.setUnreadMessage(conversation.getUnreadMsgCount() > 0);

                            }catch (Exception e){
                                //无最后一条消息纪录
                                e.printStackTrace();
                            }

                            if(conversation.getUnreadMsgCount() > 0){
                                mPrivateChatListData.add(0,chatUserBean);
                            }else{
                                mPrivateChatListData.add(chatUserBean);
                            }

                        }
                        //填充会话列表
                        fillUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //会话列表中已存在更新该会话最后一条信息
    protected void updataLastMessage(final EMMessage messages) {

        for(int i =0; i < mPrivateChatListData.size(); i++){
            PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(i);

            //判断当前新消息是那个会话
            if(privateChatUserBean.getId() == Integer.parseInt(messages.getFrom())){
                privateChatUserBean.setLastMessage(((EMTextMessageBody)(messages.getBody())).getMessage());
                if(i == mPosition){
                    //未读消息
                    privateChatUserBean.setUnreadMessage(false);
                }else{
                    privateChatUserBean.setUnreadMessage(true);
                }
                //privateChatUserBean.setUnreadMessage(true);
                //将该回话移到第一位
                mPrivateChatListData.remove(i);
                mPrivateChatListData.add(0,privateChatUserBean);
                //mPrivateChatListData.set(i, privateChatUserBean);
                updatePrivateChatList();
            }
        }



    }


    //没有在会话列表中,所以添加一个item
    protected void inConversationMapAddItem(final EMMessage messages) {
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
        PhoneLiveApi.getPmUserInfo(mUser.getId(),Integer.parseInt(messages.getFrom()), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(null != res){
                    PrivateChatUserBean privateChatUserBean = mGson.fromJson(res,PrivateChatUserBean.class);
                    privateChatUserBean.setLastMessage(((EMTextMessageBody)(messages.getBody())).getMessage());
                    privateChatUserBean.setUnreadMessage(true);
                    //将该回话移到第一位
                    mPrivateChatListData.add(0,privateChatUserBean);
                    //mPrivateChatListData.add(privateChatUserBean);
                    updatePrivateChatList();
                }

            }
        });


    }

    protected abstract void initCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract void onNewMessage(EMMessage messages);

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        //获取是否有未读消息
        super.onDestroy();

        getActivity().unregisterReceiver(broadCastReceiver);


    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("私信"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
