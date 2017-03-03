package com.bolema.phonelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.MusicAdapter;

import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.LocalMusicBean;
import com.bolema.phonelive.bean.LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean;
import com.bolema.phonelive.utils.DBManager;
import com.bolema.phonelive.utils.GsonTools;
import com.bolema.phonelive.utils.LiveUtils;
import com.bolema.phonelive.utils.MD5;
import com.bolema.phonelive.utils.MD5Encoder;
import com.dd.CircularProgressButton;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播间音乐搜索弹窗
 */
public class SearchMusicDialogFragment extends DialogFragment {
    @InjectView(R.id.iv_search_music_back)
    ImageView mSearchBack;
    @InjectView(R.id.tv_search_btn)
    TextView mSearchBtn;
    @InjectView(R.id.lv_search_music)
    ListView mSearchListView;
    @InjectView(R.id.et_search_input)
    EditText mInputEdit;
    @InjectView(R.id.iv_close)
    ImageView mIvClose;
    String keyword = "";

    private List<MusiclistBean> bMusicList = new ArrayList<>();  //伴奏歌曲类型

    private MusicAdapter mAdapter;
    private DBManager mDbManager;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1009:
                    searchMusic();
                    break;
            }
        }
    };

    private boolean isOriginalSong;
   

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, null);
        ButterKnife.inject(this, view);

        initView(view);

        initData();
        return view;
    }


    public void initView(View view) {
        //返回
        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
            }
        });
        //长按删除歌曲
        mSearchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + bMusicList.get(position).getSongid() + ".mp3");
                File lrcfile = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + bMusicList.get(position).getSongid() + ".lrc");
                    if (file.exists()) {
                        file.delete();

                        lrcfile.delete();

                        mDbManager.delete(bMusicList.get(position));
                        bMusicList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        AppContext.showToast("歌曲已删除");
                    }
                return false;
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //所有音乐
    private void searchMusic() {

             keyword = mInputEdit.getText().toString().trim()+"伴奏";

        if (keyword.equals("")) {
            AppContext.showToastAppMsg(getActivity(), "请输入有效的关键词~");
            return;
        }
        PhoneLiveApi.searchMusic(keyword, new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(getActivity(), "查询失败,请换首歌试试~");
            }

            @Override
            public void onResponse(String response) {
                KLog.json(response);
                LocalMusicBean localMusicBean = GsonTools.instance(response, LocalMusicBean.class);
                if (localMusicBean.getShowapi_res_code() == 0) {
                    if (localMusicBean.getShowapi_res_body().getPagebean().getAllNum() == 0) {
                        //失败
                        AppContext.showToastAppMsg(getActivity(), "查询失败,请换首歌试试~");
                    } else {
                        bMusicList.clear();
//                        bMusicList.clear();
                        for (int i=0; i<localMusicBean.getShowapi_res_body().getPagebean().getContentlist().size();i++) {
                            if (localMusicBean.getShowapi_res_body().getPagebean().getContentlist().get(i).getSongid() == 0&&!localMusicBean.getShowapi_res_body().getPagebean().getContentlist().get(i).getSongname().contains("铃声")) {
                                bMusicList.add(localMusicBean.getShowapi_res_body().getPagebean().getContentlist().get(i));
                            }
                        }
                        fillUI();
                    }
                } else if (localMusicBean.getShowapi_res_code() == -1009) {
                    handler.sendEmptyMessageDelayed(1009, 1000);
                } else {
                    AppContext.showToastAppMsg(getActivity(), "查询失败,请换首歌试试~");
                }
            }
        });
    }



    private void fillUI() {
//        if (which == 0) {

            mAdapter.notifyDataSetChangedMusicList(bMusicList);
//        } else {
//            mAdapter.notifyDataSetChangedMusicList(mMusicList);
//        }
    }


    public void initData() {
        mDbManager = new DBManager(getActivity());

//        if (which == 1) {
//            mMusicList = mDbManager.query();
//
//            Collections.reverse(mMusicList);
//
//            mAdapter = new MusicAdapter(mMusicList, this, mDbManager,which);
//        } else {
            bMusicList = mDbManager.query();

            Collections.reverse(bMusicList);

            mAdapter = new MusicAdapter(bMusicList, this, mDbManager);
//        }

        mSearchListView.setAdapter(mAdapter);
        AppContext.showToastShort( "长按删除歌曲");

    }

    /**
     * @dw 获取歌曲信息
     */
    public void downloadMusic(final MusiclistBean music, final CircularProgressButton mBtnDownload) {
//        if (which == 0) {
            //获取伴奏歌曲信息
            if (!TextUtils.isEmpty(music.getDownUrl())) {
                downloadMusicAndLrc(music.getDownUrl(), music, mBtnDownload);
            } else {
                AppContext.showToastShort("歌曲无法下载,请换首歌试试");
            }
//        } else if (which == 1) {
//            //获取原唱歌曲信息
//            if (!TextUtils.isEmpty(music.getM4a())) {
//                downloadMusicAndLrc(music.getM4a(), music, mBtnDownload);
//            } else {
//                AppContext.showToastShort("歌曲无法下载,请换首歌试试");
//            }
//        }

    }

    //下载歌词和歌曲
    private void downloadMusicAndLrc(String musicUrl, final MusiclistBean music, final CircularProgressButton mBtnDownload) {
//        String type;
//        if (which == 0) {
//            type = ".mp3";
//        } else {
//            type = ".m4a";
//        }
         String fileName = null;
        //下载歌曲
        try {
           fileName = MD5Encoder.encode(music.getDownUrl());

            PhoneLiveApi.downloadMusic(musicUrl, new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,fileName + ".mp3") {


                @Override
                public void onError(Call call, Exception e) {
                    mBtnDownload.setErrorText("下载失败");
                }

                @Override
                public void onResponse(File response) {
                    List<MusiclistBean> list = new ArrayList<>();
                    list.add(music);
                    mDbManager.add(list);
                }

                @Override
                public void inProgress(float progress, long total) {
                    mBtnDownload.setProgress((int) (progress * 100));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



//        if (which == 1) {  //下载原唱歌词
//            downloadLrc(String.valueOf(music.getSongid()), music.getDownUrl());
//        } else {
            //下载伴奏歌词，首先根据伴奏歌曲名和歌手名搜索原唱歌曲，然后根据原唱歌曲的歌曲ID搜索歌词
        final String finalFileName = fileName;
        PhoneLiveApi.searchMusic(keyword.replace("伴奏",""), new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    LocalMusicBean localMusicBean = GsonTools.instance(response, LocalMusicBean.class);
                    if (localMusicBean.getShowapi_res_code() == 0) {
                        if (localMusicBean.getShowapi_res_body().getPagebean().getAllNum() != 0) {
                            int songId = localMusicBean.getShowapi_res_body().getPagebean().getContentlist().get(0).getSongid();
                            String downurl = localMusicBean.getShowapi_res_body().getPagebean().getContentlist().get(0).getDownUrl();
                            downloadLrc(String.valueOf(songId), finalFileName);
                        }
                    }
                }
            });
//        }
    }

    public void downloadLrc(String songid,String downurl) {
        //下载歌词
        try {
            PhoneLiveApi.downloadLrc(songid+"", new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH, downurl+ ".lrc") {
                @Override
                public void inProgress(float progress, long total) {

                }

                @Override
                public void onError(Call call, Exception e) {
                    AppContext.showToastShort("歌词下载失败");
                }

                @Override
                public void onResponse(File response) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("选择音乐"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("选择音乐"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }

    //歌曲选中回调接口
    public interface SearchMusicFragmentInterface {
        void onSelectMusic(Intent intent);
    }

}
