package com.bolema.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.bean.UserBean;
import com.bumptech.glide.Glide;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *
 * Created by yuanshuo on 2017/2/24.
 */

public class LiveBroadcastAdapter extends RecyclerView.Adapter<LiveViewHolder> {

    ArrayList<UserBean> userBeens ;
    private Context context;

    public LiveBroadcastAdapter(ArrayList<UserBean> userBeens,Context context) {
        this.userBeens = userBeens;
        this.context = context;
    }

    @Override
    public LiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_live_broadcast,parent,false);
        LiveViewHolder holder = new LiveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LiveViewHolder holder, int position) {
        Glide.with(context)
                .load(userBeens.get(position).getAvatar())
                .into(holder.liveBroadcastPic);
        holder.title.setText(userBeens.get(position).getTitle());
        holder.tvWatchTimes.setText(userBeens.get(position).getNums());
    }

    @Override
    public int getItemCount() {
        return userBeens.size();
    }


}

class LiveViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.live_broadcast_pic)
    ImageView liveBroadcastPic;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.tv_watch_times)
    TextView tvWatchTimes;
    public LiveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        AutoUtils.autoSize(itemView);
    }
}