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
    private List<MusiclistBean> mMusicList = new ArrayList<>();
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
                File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + mMusicList.get(position).getSongid() + ".m4a");
                File lrcfile = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + mMusicList.get(position).getSongid() + ".lrc");
                    if (file.exists()) {
                        file.delete();
                        lrcfile.delete();
                        mDbManager.delete(mMusicList.get(position));
                        mMusicList.remove(position);
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
        String keyword = mInputEdit.getText().toString().trim();
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
                        mMusicList.clear();
                        mMusicList.addAll(localMusicBean.getShowapi_res_body().getPagebean().getContentlist());
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
        mAdapter.notifyDataSetChangedMusicList(mMusicList);
    }


    public void initData() {
        mDbManager = new DBManager(getActivity());
        mMusicList = mDbManager.query();

        Collections.reverse(mMusicList);
        mAdapter = new MusicAdapter(mMusicList, this, mDbManager);


        mSearchListView.setAdapter(mAdapter);
        AppContext.showToastShort( "长按删除歌曲");

    }

    /**
     * @dw 获取歌曲信息
     */
    public void downloadMusic(final MusiclistBean music, final CircularProgressButton mBtnDownload) {
        //获取歌曲信息
        if (!TextUtils.isEmpty(music.getM4a())) {
            downloadMusicAndLrc(music.getM4a(), music, mBtnDownload);
        } else {
            AppContext.showToastShort("歌曲无法下载,请换首歌试试");
        }
    }

    //下载歌词和歌曲
    private void downloadMusicAndLrc(String musicUrl, final MusiclistBean music, final CircularProgressButton mBtnDownload) {
        //下载歌曲
        PhoneLiveApi.downloadMusic(musicUrl, new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH, music.getSongid() + ".m4a") {

            @Override
            public void onError(Call call, Exception e) {
                mBtnDownload.setErrorText("下载失败");
            }

            @Override
            public void onResponse(File response) {
                List<MusiclistBean> list = new ArrayList<MusiclistBean>();
                list.add(music);
                mDbManager.add(list);
            }

            @Override
            public void inProgress(float progress, long total) {
                mBtnDownload.setProgress((int) (progress * 100));
            }
        });

        //下载歌词
        PhoneLiveApi.downloadLrc(music.getSongid()+"", new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,music.getSongid() + ".lrc") {
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
