package com.bolema.phonelive.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.LiveRecordBean;
import com.bolema.phonelive.bean.PlaybackBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.ui.HomePageActivity;
import com.bolema.phonelive.ui.VideoBackActivity;
import com.bolema.phonelive.utils.GsonTools;
import com.bolema.phonelive.utils.RegexUtils;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.MyAutoLinearLayout;
import com.bolema.phonelive.widget.MyAutoLinearLayout2;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

//热门主播 直播及回放
public class LiveUserAdapter extends BaseAdapter {
    private List<UserBean> mUserList;
    private LayoutInflater inflater;
    private List<PlaybackBean> mPlaybackList;
    private Activity activity;

    private Gson g = new Gson();
    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;

    private String splendid_playback = " 精彩回放";


    public LiveUserAdapter(LayoutInflater inflater, List<UserBean> mUserList, List<PlaybackBean> mPlaybackList, Activity activity) {
        this.mUserList = mUserList;
        this.inflater = inflater;
        this.activity = activity;
        this.mPlaybackList = mPlaybackList;
    }

    @Override
    public int getCount() {
        return mUserList.size() + mPlaybackList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < mUserList.size()) {
            return mUserList.get(position);
        } else {
            return mPlaybackList.get(position - mUserList.size());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        ViewHolderPlayback viewHolderPlayback;


        if (position < mUserList.size()) {


            if (convertView != null && convertView instanceof MyAutoLinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = inflater.inflate(R.layout.item_hot_user, null);

                viewHolder = new ViewHolder();

                viewHolder.mUserNick = (TextView) convertView.findViewById(R.id.tv_live_nick);
                viewHolder.mUserLocal = (TextView) convertView.findViewById(R.id.tv_live_local);
                viewHolder.mUserNums = (TextView) convertView.findViewById(R.id.tv_live_usernum);
                viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
                viewHolder.mUserPic = (ImageView) convertView.findViewById(R.id.iv_live_user_pic);
                viewHolder.mRoomTitle = (TextView) convertView.findViewById(R.id.tv_hot_room_title);
                convertView.setTag(viewHolder);
                //对于listview，注意添加这一行，即可在item上使用高度
                AutoUtils.autoSize(convertView);
            }

            UserBean user = mUserList.get(position);

            viewHolder.mUserNick.setText(user.getUser_nicename());
            viewHolder.mUserLocal.setText(user.getCity());
            //viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());
            viewHolder.mUserHead.setAvatarUrl(user.getAvatar());
            viewHolder.mUserNums.setText(String.valueOf(user.getNums()));
            //用于平滑加载图片
            Glide
                    .with(AppContext.getInstance())
                    .load(user.getAvatar())
                    .fitCenter()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .into(viewHolder.mUserPic);

            if (null != user.getTitle()) {
                viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
                viewHolder.mRoomTitle.setText(user.getTitle());
            } else {
                viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
                viewHolder.mRoomTitle.setText("");
            }


        } else {

            if (convertView != null&&convertView instanceof MyAutoLinearLayout2) {
                viewHolderPlayback = (ViewHolderPlayback) convertView.getTag();
            } else {
                convertView = inflater.inflate(R.layout.item_attention_playback, null);
                viewHolderPlayback = new ViewHolderPlayback(convertView);
                convertView.setTag( viewHolderPlayback);
                //对于listview，注意添加这一行，即可在item上使用高度
                AutoUtils.autoSize(convertView);
            }

            final PlaybackBean playbackBean = mPlaybackList.get(position - mUserList.size());

            viewHolderPlayback.tvLiveNick.setText(playbackBean.getUserinfo().getUser_nicename());
            viewHolderPlayback.tvLiveLocal.setText(playbackBean.getUserinfo().getCity());
            viewHolderPlayback.ivLiveUserHead.setAvatarUrl(playbackBean.getUserinfo().getAvatar());
            viewHolderPlayback.tvLiveUsernum.setText(String.valueOf(playbackBean.getBack().get(0).getNums()));
            if (playbackBean.getUserinfo().getCity().equals("")) {
                viewHolderPlayback.tvLiveLocal.setText("好像在火星");
            }

            viewHolderPlayback.tvShowDate.setText(RegexUtils.getData(playbackBean.getBack().get(0).getTimes()) + splendid_playback);
            viewHolderPlayback.tvShowDuration.setText("时长 "+RegexUtils.getPlayTime(playbackBean.getBack().get(0).getDuration()));
            if (playbackBean.getBack().size() >= 4) {
                viewHolderPlayback.tvShowDate1.setText(RegexUtils.getData(playbackBean.getBack().get(1).getTimes()));
                viewHolderPlayback.tvShowDate2.setText(RegexUtils.getData(playbackBean.getBack().get(2).getTimes()));
                viewHolderPlayback.tvShowDate3.setText(RegexUtils.getData(playbackBean.getBack().get(3).getTimes()));

                viewHolderPlayback.tvShowDuration1.setText("时长 " + RegexUtils.getPlayTime(playbackBean.getBack().get(1).getDuration()));
                viewHolderPlayback.tvShowDuration2.setText("时长 " + RegexUtils.getPlayTime(playbackBean.getBack().get(2).getDuration()));
                viewHolderPlayback.tvShowDuration3.setText("时长 " + RegexUtils.getPlayTime(playbackBean.getBack().get(3).getDuration()));

                viewHolderPlayback.layoutPlayback1.setVisibility(View.VISIBLE);
                viewHolderPlayback.layoutPlayback2.setVisibility(View.VISIBLE);
                viewHolderPlayback.layoutPlayback3.setVisibility(View.VISIBLE);

               final LiveRecordBean liveRecordBean1 = GsonTools.instance(playbackBean.getBack().get(1).toString(), LiveRecordBean.class);

                viewHolderPlayback.layoutPlayback1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("clickPosition", liveRecordBean1.getId() + "  uid:" + liveRecordBean1.getUid() + "\nurl: " + liveRecordBean1.getVideo_url()
                                + "\norginurl" + playbackBean.getBack().get(1).getVideo_url());
                        VideoBackActivity.startVideoBack(activity, liveRecordBean1);

                    }
                });
                final LiveRecordBean  liveRecordBean2 = GsonTools.instance(playbackBean.getBack().get(2).toString(), LiveRecordBean.class);

                viewHolderPlayback.layoutPlayback2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("clickPosition", liveRecordBean2.getId() + "  uid:" + liveRecordBean2.getUid() + "\nurl: " + liveRecordBean2.getVideo_url()
                                + "\norginurl" + playbackBean.getBack().get(2).getVideo_url());
                        VideoBackActivity.startVideoBack(activity, liveRecordBean2);
                    }
                });
                final LiveRecordBean  liveRecordBean3 = GsonTools.instance(playbackBean.getBack().get(3).toString(), LiveRecordBean.class);

                viewHolderPlayback.layoutPlayback3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("clickPosition", liveRecordBean3.getId() + "  uid:" + liveRecordBean3.getUid() + "\nurl: " + liveRecordBean3.getVideo_url()
                                + "\norginurl" + playbackBean.getBack().get(3).getVideo_url());
                        VideoBackActivity.startVideoBack(activity, liveRecordBean3);

                    }
                });

            } else {
                viewHolderPlayback.layoutPlayback1.setVisibility(View.GONE);
                viewHolderPlayback.layoutPlayback2.setVisibility(View.GONE);
                viewHolderPlayback.layoutPlayback3.setVisibility(View.GONE);
            }


            final LiveRecordBean  mLiveRecordBean = GsonTools.instance(playbackBean.getBack().get(0).toString(), LiveRecordBean.class);



            viewHolderPlayback.layoutLiveUserPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clickPosition", mLiveRecordBean.getId() + "  uid:" + mLiveRecordBean.getUid() + "\nurl: " + mLiveRecordBean.getVideo_url()
                            + "\norginurl" + playbackBean.getBack().get(0).getVideo_url());
                    VideoBackActivity.startVideoBack(activity, mLiveRecordBean);
                }
            });



            //用于平滑加载图片
            Glide
                    .with(AppContext.getInstance())
                    .load(playbackBean.getUserinfo().getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.null_blacklist)
                    .crossFade()
                    .into(viewHolderPlayback.ivLiveUserPic);

        }



        return convertView;
    }



    private class ViewHolder {
        TextView mUserNick, mUserLocal, mUserNums, mRoomTitle;
        ImageView mUserPic;
        AvatarView mUserHead;
    }


    class ViewHolderPlayback {
        @InjectView(R.id.iv_live_user_head)
        AvatarView ivLiveUserHead;
        @InjectView(R.id.tv_live_nick)
        TextView tvLiveNick;
        @InjectView(R.id.tv_live_local)
        TextView tvLiveLocal;
        @InjectView(R.id.tv_live_usernum)
        TextView tvLiveUsernum;
        @InjectView(R.id.iv_transparent_black)
        ImageView ivTransparentBlack;
        @InjectView(R.id.tv_show_date)
        TextView tvShowDate;
        @InjectView(R.id.tv_show_duration)
        TextView tvShowDuration;
        @InjectView(R.id.tv_show_date_1)
        TextView tvShowDate1;
        @InjectView(R.id.tv_show_duration_1)
        TextView tvShowDuration1;
        @InjectView(R.id.layout_playback_1)
        AutoLinearLayout layoutPlayback1;
        @InjectView(R.id.tv_show_date_2)
        TextView tvShowDate2;
        @InjectView(R.id.tv_show_duration_2)
        TextView tvShowDuration2;
        @InjectView(R.id.layout_playback_2)
        AutoLinearLayout layoutPlayback2;
        @InjectView(R.id.tv_show_date_3)
        TextView tvShowDate3;
        @InjectView(R.id.tv_show_duration_3)
        TextView tvShowDuration3;
        @InjectView(R.id.layout_playback_3)
        AutoLinearLayout layoutPlayback3;
        @InjectView(R.id.layout_live_user_pic)
        AutoRelativeLayout layoutLiveUserPic;

        @InjectView(R.id.iv_live_user_pic)
        ImageView ivLiveUserPic;

        ViewHolderPlayback(View view) {
            ButterKnife.inject(this, view);
        }
    }
}


