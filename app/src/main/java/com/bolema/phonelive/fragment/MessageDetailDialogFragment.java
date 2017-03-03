package com.bolema.phonelive.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.MessageAdapter;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.bean.PrivateMessage;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.ui.other.PhoneLivePrivateChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 直播间私信发送页面
 */
public class MessageDetailDialogFragment extends DialogFragment {
    @InjectView(R.id.tv_private_chat_title)
    TextView mTitle;

    @InjectView(R.id.et_private_chat_message)
    EditText mMessageInput;

    private com.bolema.phonelive.interf.DialogInterface mDialogInterface;

    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;


    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;

    private BroadcastReceiver broadCastReceiver;

    private long lastTime = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomViewTheme_Transparent);
        dialog.setContentView(R.layout.dialog_fragment_private_chat_message);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        ButterKnife.inject(this, dialog);
        initData();
        initView(dialog);

        return dialog;
    }

    @OnClick({R.id.iv_private_chat_send, R.id.et_private_chat_message, R.id.iv_close})

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

                break;
        }

    }

    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if ((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0) {
            Toast.makeText(getActivity(), "操作频繁", Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = System.currentTimeMillis();
        if (mMessageInput.getText().toString().equals("")) {
            AppContext.showToastAppMsg(getActivity(), "内容为空");
            return;
        }
        if (mMessageInput.getText().toString().equals("")) {
            AppContext.showToastAppMsg(getActivity(), "内容为空");
        }
        EMMessage emMessage = PhoneLivePrivateChat.sendChatMessage(mMessageInput.getText().toString(), mToUser);

        //更新列表
        updateChatList(PrivateMessage.crateMessage(emMessage, mUser.getAvatar()));
        mMessageInput.setText("");
    }

    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = (PrivateChatUserBean) getArguments().getParcelable("user");
        if (mToUser != null) {
            mTitle.setText(mToUser.getUser_nicename());
        }
        //获取历史消息
        mChats = PhoneLivePrivateChat.getUnreadRecord(mUser, mToUser);

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
        if (broadCastReceiver == null) {
            broadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    final EMMessage emMessage = intent.getParcelableExtra("cmd_value");
                    //判断是否是当前回话的消息
                    if (emMessage.getFrom().trim().equals(String.valueOf(mToUser.getId()))) {

                        updateChatList(PrivateMessage.crateMessage(emMessage, mToUser.getAvatar()));

                    }
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver, cmdFilter);
    }


    public void initView(Dialog view) {

    }

    private void updateChatList(PrivateMessage message) {
        //更新聊天列表
        mMessageAdapter.addMessage(message);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mMessageAdapter.getCount() - 1);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadCastReceiver);
        } catch (Exception e) {

        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("私信聊天页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDialogInterface != null) {
            mDialogInterface.cancelDialog(null, null);
        }

    }

    public void setDialogInterface(com.bolema.phonelive.interf.DialogInterface dialogInterface) {
        mDialogInterface = dialogInterface;
    }
}
