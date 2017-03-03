package com.bolema.phonelive.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bolema.phonelive.R;
import com.bolema.phonelive.bean.RecordedLiveBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.ui.RecordedVideoActivity;
import com.bumptech.glide.Glide;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuanshuo on 2017/3/3.
 */

public class LiveRecordedAdapter extends RecyclerView.Adapter<RecordedViewHolder> {

    ArrayList<RecordedLiveBean> recordBeans;
    private Context context;
    VideoView videoView ;

    public LiveRecordedAdapter(ArrayList<RecordedLiveBean> recordBeans, Context context) {
        this.recordBeans = recordBeans;
        this.context = context;
        videoView = new VideoView(context);
    }

    @Override
    public RecordedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_live_broadcast, parent, false);
        RecordedViewHolder holder = new RecordedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecordedViewHolder holder, final int position) {
        holder.title.setText(recordBeans.get(position).getTitle());
        holder.tvWatchTimes.setText(recordBeans.get(position).getSort());
        Glide.with(context)
                .load(recordBeans.get(position).getImg())
                .centerCrop()
                .placeholder(R.drawable.null_blacklist)
                .error(R.drawable.null_blacklist)
                .into(holder.liveBroadcastPic);
        holder.liveBroadcastItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url", recordBeans.get(position).getUrl());
                Intent intent = new Intent(context, RecordedVideoActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return recordBeans.size();
    }


}

class RecordedViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.live_broadcast_pic)
    ImageView liveBroadcastPic;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.tv_watch_times)
    TextView tvWatchTimes;
    @InjectView(R.id.live_broadcast_item)
    AutoLinearLayout liveBroadcastItem;

    RecordedViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }
}