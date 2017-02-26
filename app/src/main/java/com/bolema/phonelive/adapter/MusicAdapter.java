package com.bolema.phonelive.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;

import com.bolema.phonelive.bean.LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean;

import com.bolema.phonelive.fragment.SearchMusicDialogFragment;
import com.bolema.phonelive.ui.StartLiveActivity;
import com.bolema.phonelive.utils.DBManager;
import com.bolema.phonelive.utils.MD5;
import com.bolema.phonelive.utils.MD5Encoder;
import com.dd.CircularProgressButton;

import java.io.File;
import java.util.List;

/**
 * Created by yuanshuo on 2017/1/25.
 */

public class MusicAdapter extends BaseAdapter{
    private List<MusiclistBean> mMusicList;
    private SearchMusicDialogFragment mFragment;
    private DBManager mDbManager;
    public MusicAdapter(List<MusiclistBean> MusicList, SearchMusicDialogFragment fragment, DBManager dbManager){

        this.mMusicList =  MusicList;
        this.mFragment = fragment;
        this.mDbManager = dbManager;

    }

    public void notifyDataSetChangedMusicList(List<MusiclistBean> MusicList){
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

        final MusiclistBean music = mMusicList.get(position);
        viewHolder.mMusicName.setText(music.getSongname());
        viewHolder.mMusicAuthor.setText(music.getSingername());

//        String type;
////        if (which == 1) {
//            type = ".m4a";

        File file = null;
        try {
            file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + MD5Encoder.encode(music.getDownUrl()) + ".mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //判断该音乐是否存在
        try {
            if(mDbManager.queryFromEncryptedDownloadUrl(music.getDownUrl()).getCount()!=0){
                viewHolder.mBtnDownload.setText(R.string.select);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //点击下载或播放
        final File finalFile = file;
        viewHolder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                //判断该音乐是否存在,存在直接播放
                try {
                    AppContext.showToastShort(music.getDownUrl());

                    if(mDbManager.queryFromEncryptedDownloadUrl(music.getDownUrl()).getCount()!=0){
                        intent = new Intent();
                        ((StartLiveActivity)mFragment.getActivity()).onSelectMusic(intent.putExtra("filepath", finalFile.getPath()));
                    }else {
                        mFragment.downloadMusic(music,(CircularProgressButton)v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
