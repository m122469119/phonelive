package com.bolema.phonelive.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.ui.DrawableRes;
import com.bolema.phonelive.widget.CircleImageView;
import com.zhy.autolayout.utils.AutoUtils;

import org.kymjs.kjframe.Core;

import java.util.ArrayList;
import java.util.List;


//私信会话列表
public class UserBaseInfoPrivateChatAdapter extends BaseAdapter {
    private List<PrivateChatUserBean> users;
    public UserBaseInfoPrivateChatAdapter(List<PrivateChatUserBean> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_private_chat,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (TextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mULastMsg = (TextView) convertView.findViewById(R.id.tv_item_last_msg);
            viewHolder.mUnread = (ImageView) convertView.findViewById(R.id.iv_unread_dot);
            convertView.setTag(viewHolder);
            //对于listview，注意添加这一行，即可在item上使用高度
            AutoUtils.autoSize(convertView);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrivateChatUserBean u = users.get(position);
        Core.getKJBitmap().display(viewHolder.mUHead, u.getAvatar());



        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);


//        Log.e("isVip", u.getVip_type() + u.getViplevel() + u.getVipthumb());
//        if (u.getViplevel()!=null) {
//            viewHolder.mULevel.setImageResource(DrawableRes.LevelVipImg[u.getLevel()-1<0?0:u.getLevel()-1]);
//        } else {
            viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel()-1<0?0:u.getLevel()-1]);
//        }


        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mULastMsg.setText(u.getLastMessage());
        viewHolder.mUnread.setVisibility(u.isUnreadMessage()?View.VISIBLE:View.GONE);

        return convertView;
    }

    public void setPrivateChatUserList(ArrayList<PrivateChatUserBean> privateChatUserList) {
        this.users = privateChatUserList;
    }

    private class ViewHolder{
       CircleImageView mUHead;
       ImageView mUSex,mULevel,mUnread;
       TextView mUNice,mULastMsg;
    }
}
