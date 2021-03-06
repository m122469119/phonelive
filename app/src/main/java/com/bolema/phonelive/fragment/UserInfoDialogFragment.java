package com.bolema.phonelive.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tandong.bottomview.view.BottomView;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ShowLiveActivityBase;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.bean.UserAlertInfoBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.ui.DrawableRes;
import com.bolema.phonelive.ui.other.ChatServer;
import com.bolema.phonelive.utils.ImageUtils;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.BottomMenuView;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播间用户列表点击弹窗页面
 */
public class UserInfoDialogFragment extends DialogFragment {

    private UserBean mUser;
    private UserBean mToUser;
    private int mRoomNum;
    private ChatServer mChatServer;
    @InjectView(R.id.tv_show_dialog_u_fllow_num)
    TextView mTvFollowNum;

    @InjectView(R.id.tv_show_dialog_u_fans)
    TextView mTvFansNum;

    @InjectView(R.id.tv_show_dialog_u_send_num)
    TextView mTvSendNum;

    @InjectView(R.id.tv_show_dialog_u_ticket)
    TextView mTvTicketNum;

    @InjectView(R.id.tv_show_dialog_u_fllow_btn)
    TextView mTvFollowBtn;

    @InjectView(R.id.tv_live_manage_or_report)
    TextView mTvReportBtn;

    //只有主页的菜单
    @InjectView(R.id.ll_user_info_dialog_bottom_menu_own)
    LinearLayout mLLUserInfoDialogBottomMenuOwn;

    //私信,关注,主页的菜单
    @InjectView(R.id.ll_user_info_dialog_bottom_menu)
    LinearLayout mLLUserInfoDialogBottomMenu;

    @InjectView(R.id.iv_show_dialog_level)
    ImageView mIvLevel;

    @InjectView(R.id.tv_show_dialog_u_address)
    TextView mTvAddress;

    public UserInfoDialogFragment() {
        // Required empty public constructor
    }

    private IsAttentionListener mIsAttentionListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),R.style.dialog_no_background);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info_dialog,null);
        dialog.setContentView(view);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return dialog;
    }

    private void initData() {
        //判断用户类型
        if(mUser.getId() == mRoomNum){
            mTvReportBtn.setText("管理");
            mTvReportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showManageBottomMenu(getActivity(),mUser,mToUser,mChatServer,true);
                    dismiss();
                }
            });
        }else{
            PhoneLiveApi.isManage(mRoomNum, mUser.getId(), new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    String res = ApiUtils.checkIsSuccess(response);
                    if(res == null) return;
                    mUser.setuType(Integer.parseInt(res));
                    mTvReportBtn.setText(mUser.getuType() == 40?"管理":"举报");
                    mTvReportBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mUser.getuType() == 40){
                                showManageBottomMenu(getActivity(),mUser,mToUser,mChatServer,false);
                                dismiss();
                            }else {
                                showReportAlert();

                            }
                        }
                    });
                }
            });
        }

        //获取用户详细信息
        PhoneLiveApi.getUserInfo(mToUser.getId(),mUser.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }
            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(res != null){
                    //用户粉丝关注魅力值等信息
                    UserAlertInfoBean u = new Gson().fromJson(res,UserAlertInfoBean.class);
                    mTvFollowNum.setText("关注:" + u.getAttention());
                    mTvFansNum.setText("粉丝:" + u.getFans());
                    mTvSendNum.setText("送出:" + u.getConsumption());
                    mTvTicketNum.setText("魅力值:" + u.getVotestotal());

                    //等级

                    mIvLevel.setImageResource(DrawableRes.LevelImg[u.getLevel()==0?0:u.getLevel()-1]);

                    //位置信息 2016.09.06 wp
                    if(u.getCity() == null || u.getCity().equals("")){
                        mTvAddress.setText("好像在火星");
                    }else{
                        mTvAddress.setText(u.getCity());
                    }

                }


            }
        });
        //获取是否关注该用户
        PhoneLiveApi.getIsFollow(mUser.getId(), mToUser.getId(), new StringCallback() {
            //
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if (null != res) {

                    if (res.equals("0") && isAdded()) {
                        mTvFollowBtn.setText(getString(R.string.follow2));
                        mTvFollowBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), mToUser.getId(),AppContext.getInstance().getToken(), null);
                                mTvFollowBtn.setEnabled(false);
                                mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                                mTvFollowBtn.setText(getString(R.string.alreadyfollow));
                                if(mToUser.getId() == mRoomNum){
                                    mChatServer.doSendMsg(mUser.getUser_nicename() + "关注了主播",mUser,0);
                                }
                                mIsAttentionListener.isAttention();
                            }
                        });
                    } else  {
                        if (isAdded()) {
                            mTvFollowBtn.setText(getString(R.string.alreadyfollow));
                            mTvFollowBtn.setEnabled(false);
                            mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                        }
                    }
                }
            }
        });
        //判断点击的是否主播隐藏管理按钮
        if (mToUser.getId()==mUser.getId()){
            mTvReportBtn.setVisibility(View.GONE);
        }
    }

    public  void setIsAttentionListener(IsAttentionListener l){
        mIsAttentionListener=l;
    }

    public interface IsAttentionListener{
        public void isAttention();
    }

    //举报弹窗
    private void showReportAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确定举报?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PhoneLiveApi.report(mUser.getId(),mToUser.getId());
                AppContext.showToastAppMsg(getActivity(),getString(R.string.reportsuccess));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void initView(final View view) {
        mUser = (UserBean) getArguments().getParcelable("MYUSERINFO");
        mToUser = (UserBean) getArguments().getParcelable("TOUSERINFO");
        mRoomNum = getArguments().getInt("ROOMNUM");
        mChatServer = ((ShowLiveActivityBase)getActivity()).mChatServer;

        //是否是自己点击弹窗
        if(mUser.getId() == mToUser.getId()){
            mTvFollowBtn.setEnabled(false);
            //切换底部菜单
            mLLUserInfoDialogBottomMenuOwn.setVisibility(View.VISIBLE);
            mLLUserInfoDialogBottomMenu.setVisibility(View.GONE);
        }

        view.findViewById(R.id.ib_show_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.tv_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPrivateMessage((ShowLiveActivityBase) getActivity(),mUser.getId(),mToUser.getId());
                //dismiss();
            }
        });

        //主页
        view.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(getActivity(),mToUser.getId());
                dismiss();//BBB
            }
        });
        //主页
        view.findViewById(R.id.tv_show_dialog_u_home_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(getActivity(),mToUser.getId());
                dismiss();//BBB
            }
        });

        ((AvatarView) view.findViewById(R.id.av_show_dialog_u_head)).setAvatarUrl(mToUser.getAvatar());
        ((TextView) view.findViewById(R.id.tv_show_dialog_u_name)).setText(mToUser.getUser_nicename());
        ((ImageView)view.findViewById(R.id.iv_show_dialog_sex)).setImageResource(mToUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);


        final ImageView mIvUserInfoBg = ((ImageView) view.findViewById(R.id.dialog_liv_user_info_bg));

        //背景图
        Core.getKJBitmap().display(mIvUserInfoBg, mToUser.getAvatar(), new BitmapCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                //Bitmap bp = ImageUtils.fastblur(getActivity(),bitmap,5);
                mIvUserInfoBg.setImageBitmap(bitmap);
                super.onSuccess(bitmap);
            }
        });

    }
    //显示管理弹窗
    public void showManageBottomMenu(Activity activity, UserBean mUser, UserBean mToUser, ChatServer chatServer, boolean isEmcee) {
        BottomMenuView mBottomMenuView = new BottomMenuView(activity);
        BottomView mManageMenu = new BottomView(activity, R.style.BottomViewTheme_Transparent,mBottomMenuView);
        mBottomMenuView.setOptionData(mUser,mToUser,mRoomNum,activity,chatServer,mManageMenu);
        mBottomMenuView.setIsEmcee(isEmcee);
        mManageMenu.setAnimation(R.style.BottomToTopAnim);
        mManageMenu.showBottomView(false);
    }
    //跳转私信
    public void showPrivateMessage(final ShowLiveActivityBase activity, int uid, int touid){

        PhoneLiveApi.getPmUserInfo(uid, touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                if(null == res) return;
                //UIHelper.showPrivateChatMessage(activity,new Gson().fromJson(res,PrivateChatUserBean.class));
                PrivateChatUserBean chatUserBean = new Gson().fromJson(res,PrivateChatUserBean.class);
                MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user",chatUserBean);
                messageFragment.setArguments(bundle);
                //messageFragment.setStyle(MessageDetailDialogFragment.STYLE_NO_TITLE,0);
                messageFragment.show(activity.getSupportFragmentManager(),"MessageDetailDialogFragment");
                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("getUserInfo");
        OkHttpUtils.getInstance().cancelTag("getIsFollow");
        OkHttpUtils.getInstance().cancelTag("report");
    }




}
