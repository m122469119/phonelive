package com.bolema.phonelive.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolema.phonelive.base.ShowLiveActivityBase;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.bean.ChatBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间聊天
 */
public class ChatListAdapter extends BaseAdapter {
    private List<ChatBean> mChats = new ArrayList<>();
    private Context mContext;

    public ChatListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setChats(List<ChatBean> chats){
        this.mChats = chats;
    }
    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_live_chat,null);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams();
//            convertView.setLayoutParams(params);

            viewHolder = new ViewHolder();
            viewHolder.mChat1 = (TextView) convertView.findViewById(R.id.tv_chat_1);
//            viewHolder.mChat2 = (TextView) convertView.findViewById(R.id.tv_chat_2);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatBean c = mChats.get(position);
        if(c.getType() != 13){
            viewHolder.mChat1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ShowLiveActivityBase)mContext).chatListItemClick(mChats.get(position));
                }
            });
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.insert(0, c.getUserNick());

        ssb.insert(ssb.length(), c.getSendChatMsg());
        viewHolder.mChat1.setText(ssb);
        viewHolder.mChat1.setShadowLayer(10F, 1F, 1F, Color.BLACK);
//        viewHolder.mChat2.setText(c.getSendChatMsg());
        return convertView;
    }
    protected class ViewHolder{
        TextView mChat1;
    }
}
