package com.bolema.phonelive.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.widget.AvatarView;
import com.bolema.phonelive.widget.LoadUrlImageView;
import com.bumptech.glide.Glide;
import com.bolema.phonelive.R;

import java.util.List;

//热门主播
public class LiveUserAdapter extends BaseAdapter {
    private List<UserBean> mUserList;
    private LayoutInflater inflater;

    public LiveUserAdapter(LayoutInflater inflater, List<UserBean> mUserList) {
        this.mUserList = mUserList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_hot_user,null);
            viewHolder = new ViewHolder();
            viewHolder.mUserNick = (TextView) convertView.findViewById(R.id.tv_live_nick);
            viewHolder.mUserLocal = (TextView) convertView.findViewById(R.id.tv_live_local);
            viewHolder.mUserNums = (TextView) convertView.findViewById(R.id.tv_live_usernum);
            viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
            viewHolder.mUserPic = (ImageView) convertView.findViewById(R.id.iv_live_user_pic);
            viewHolder.mRoomTitle = (TextView) convertView.findViewById(R.id.tv_hot_room_title);
            convertView.setTag(viewHolder);
        }
        UserBean user = mUserList.get(position);
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mUserNick.setText(user.getUser_nicename());
        viewHolder.mUserLocal.setText(user.getCity());
        //viewHolder.mUserPic.setImageLoadUrl(user.getAvatar());
        viewHolder.mUserHead.setAvatarUrl(user.getAvatar());
        viewHolder.mUserNums.setText(String.valueOf(user.getNums()));
        //用于平滑加载图片
        Glide
                .with(AppContext.getInstance())
                .load(user.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.null_blacklist)
                .crossFade()
                .into(viewHolder.mUserPic);

        if(null !=user.getTitle()){
            viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
            viewHolder.mRoomTitle.setText(user.getTitle());
        }else{
            viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
            viewHolder.mRoomTitle.setText("");
        }
        return convertView;
    }
    private class ViewHolder{
         TextView mUserNick,mUserLocal,mUserNums,mRoomTitle;
         ImageView mUserPic;
         AvatarView mUserHead;
    }
}


