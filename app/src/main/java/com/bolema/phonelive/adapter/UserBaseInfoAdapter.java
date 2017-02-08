package com.bolema.phonelive.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.ui.DrawableRes;
import com.bolema.phonelive.widget.CircleImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.kymjs.kjframe.Core;

import java.util.List;

import okhttp3.Call;

/**
 *关注粉丝列表
 */
public class UserBaseInfoAdapter extends BaseAdapter {
    private List<UserBean> users;
    public UserBaseInfoAdapter(List<UserBean> users) {
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
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_attention_fans,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (TextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mUSign = (TextView) convertView.findViewById(R.id.tv_item_usign);
            viewHolder.mIsFollow = (ImageView) convertView.findViewById(R.id.iv_item_attention);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final UserBean u = users.get(position);
        Core.getKJBitmap().display(viewHolder.mUHead, u.getAvatar());

        if (u.getId()==AppContext.getInstance().getLoginUid()){
            viewHolder.mIsFollow.setVisibility(View.GONE);
        }
        Log.d("isattention", u.getIsattention() + "");
        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        viewHolder.mIsFollow.setImageResource(u.getIsattention() == 1 ? R.drawable.me_following:R.drawable.me_follow);
        viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel() == 0?0:u.getLevel()-1]);
        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mUSign.setText(u.getSignature());
        viewHolder.mIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), u.getId(), AppContext.getInstance().getToken(),new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        if (u.getIsattention() == 1) {//1 已经关注 0未关注
                            u.setIsattention(0);
                            ((ImageView)v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_follow);
                        } else {
                            u.setIsattention(1);
                            ((ImageView)v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_following);
                        }
                    }
                });
            }
        });
        return convertView;
    }
    private class ViewHolder{
       CircleImageView mUHead;
       ImageView mUSex,mULevel,mIsFollow;
       TextView mUNice,mUSign;
    }
}
