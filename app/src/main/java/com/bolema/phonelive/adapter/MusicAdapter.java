package com.bolema.phonelive.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.bean.LocalMusicBean.DataBean.MusicBean;
import com.bolema.phonelive.fragment.SearchMusicDialogFragment2;
import com.bolema.phonelive.ui.StartLiveActivity;
import com.bolema.phonelive.utils.DBManager;
import com.dd.CircularProgressButton;

import java.io.File;
import java.util.List;

/**
 * Created by yuanshuo on 2017/1/25.
 */

public class MusicAdapter extends BaseAdapter{
    private List<MusicBean> mMusicList;
    private SearchMusicDialogFragment2 mFragment;
    private DBManager mDbManager;

    public MusicAdapter(List<MusicBean> MusicList, SearchMusicDialogFragment2 fragment, DBManager dbManager){
        this.mMusicList =  MusicList;
        this.mFragment = fragment;
        this.mDbManager = dbManager;
    }

    public void notifyDataSetChangedMusicList(List<MusicBean> MusicList){
        this.mMusicList =  MusicList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MusicAdapter.ViewHolder viewHolder;
        viewHolder = new MusicAdapter.ViewHolder();
        convertView = View.inflate(AppContext.getInstance(), R.layout.item_search_music,null);
        viewHolder.mMusicName = (TextView) convertView.findViewById(R.id.item_tv_search_music_name);
        viewHolder.mMusicAuthor = (TextView) convertView.findViewById(R.id.item_tv_search_music_author);
        viewHolder.mBtnDownload = (CircularProgressButton) convertView.findViewById(R.id.item_btn_search_music_download);

        final MusicBean music = mMusicList.get(position);
        viewHolder.mMusicName.setText(music.getMc_name());
        viewHolder.mMusicAuthor.setText(music.getSinger());
        final File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + music.getMc_name() + ".mp3");


        //判断该音乐是否存在
        if(mDbManager.queryFromEncryptedSongId(music.getId()).getCount() != 0){
            viewHolder.mBtnDownload.setText(R.string.select);
        }
        //点击下载或播放
        viewHolder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                //判断该音乐是否存在,存在直接播放
                if(mDbManager.queryFromEncryptedSongId(music.getId()).getCount() != 0){
                    intent = new Intent();
                    ((StartLiveActivity)mFragment.getActivity()).onSelectMusic(intent.putExtra("filepath",file.getPath()));
                }else {
                    mFragment.downloadMusic(music,(CircularProgressButton)v);
                }
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView mMusicName,mMusicAuthor;
        CircularProgressButton mBtnDownload;
    }
}
