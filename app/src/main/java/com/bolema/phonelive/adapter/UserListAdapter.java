package com.bolema.phonelive.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.R;
import com.bolema.phonelive.utils.StringUtils;
import com.bolema.phonelive.widget.AvatarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.socks.library.KLog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户列表adapter
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolderUserList> {
    private List<UserBean> mUsers = new ArrayList<UserBean>();
    private LayoutInflater mLayoutInflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public UserListAdapter(LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
    }

    public void setUserList(List<UserBean> users){
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderUserList onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderUserList(mLayoutInflater.inflate(R.layout.item_live_user_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolderUserList holder, final int position) {
        final UserBean u = mUsers.get(position);

        if(StringUtils.toInt(u.getVip_type(),0) > 0){
            //holder.mUhead.setCorner(getCorner(mUsers.get(position).getLevel()));
            //holder.mLevel.setAvatarUrl(u.getVipthumb());
            KLog.d("thumbURL", u.getVipthumb());
            Glide
                    .with(AppContext.getInstance()) // could be an issue!
                    .load(u.getVipthumb())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.mUhead.setCorner(resource);
                            holder.mUhead.setAvatarUrl(u.getAvatar());
                        }
                    });

        }else{
            holder.mUhead.setCorner(null);
            holder.mUhead.setAvatarUrl(u.getAvatar());
        }
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.mUhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOnItemClickListener){
                    mOnItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    private int getCorner(int level){
        int[] levelSection = new int[]{107,87,67,47,27};
        int[] corners = new int[]{R.drawable.global_xing_1,R.drawable.global_xing_2,R.drawable.global_xing_3,
                R.drawable.global_xing_4,R.drawable.global_xing_5};
        int i = 0;
        for(int l : levelSection){
            if(level >= 107){
                return corners[0];
            }
            if(level >= levelSection[i] && level <= levelSection[i - 1]){
                return corners[i];
            }
            i++;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    class ViewHolderUserList extends RecyclerView.ViewHolder {
        AvatarView mUhead;//,mLevel;

        ViewHolderUserList(View itemView) {
            super(itemView);
            mUhead = (AvatarView) itemView.findViewById(R.id.av_userHead);
            //mLevel = (AvatarView) itemView.findViewById(R.id.item_live_user_list_level);
            AutoUtils.autoSize(itemView);
        }

    }
    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view,int data);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
