package com.bolema.phonelive.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.bean.LiveRecordBean;
import com.bolema.phonelive.interf.DialogControl;
import com.bolema.phonelive.ui.dialog.CommonToast;
import com.bolema.phonelive.utils.DialogHelp;
import com.bolema.phonelive.widget.materialrefreshlayout.MaterialRefreshLayout;
import com.bolema.phonelive.widget.materialrefreshlayout.MaterialRefreshListener;
import com.bolema.phonelive.widget.slidelistview.SlideBaseAdapter;
import com.bolema.phonelive.widget.slidelistview.SlideListView;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.socks.library.KLog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends AppCompatActivity implements DialogControl {
    //    @InjectView(R.id.lv_live_record)
//    SlideListView mLiveRecordList;
    ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();
    @InjectView(R.id.lv_live_record)
    SlideListView mLiveRecordList;
    @InjectView(R.id.toolbar_back)
    ImageView toolbarBack;
    @InjectView(R.id.toolbar_title)
    TextView toolbarTitle;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.live_record_layout)
    AutoLinearLayout liveRecordLayout;
    @InjectView(R.id.refreshLayout)
    MaterialRefreshLayout refreshLayout;


    private boolean _isVisible;
    private ProgressDialog _waitDialog;

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;

    private int page = 1;
    private LiveRecordAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_record);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
        toolbarTitle.setText(getString(R.string.liverecord));
        _isVisible = true;
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.global));
        requestData();
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);
                showLiveRecord();
            }
        });
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //打开回放记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...");
        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(), showLiveByIdCallback);

    }

    private StringCallback showLiveByIdCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            hideWaitDialog();
        }

        @Override
        public void onResponse(String response) {
            hideWaitDialog();
            String res = ApiUtils.checkIsSuccess(response);
            if (res != null) {
                Log.d("video_url", res.trim());
                mLiveRecordBean.setVideo_url(res.trim());
                VideoBackActivity.startVideoBack(LiveRecordActivity.this, mLiveRecordBean);
            } else {
                showToast3("视频暂未生成,请耐心等待", 3);
            }
        }
    };
    private StringCallback requestLiveRecordDataCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(LiveRecordActivity.this, "获取直播纪录失败");
            refreshLayout.finishRefresh();
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            refreshLayout.finishRefresh();
            refreshLayout.finishRefreshLoadMore();

            if (null != res) {
                try {
                    JSONObject liveRecordJsonObj = new JSONObject(res);
                    JSONArray liveRecordJsonArray = liveRecordJsonObj.getJSONArray("list");
                    if (0 < liveRecordJsonArray.length()) {
                        Gson g = new Gson();
                        for (int i = 0; i < liveRecordJsonArray.length(); i++) {
                            mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i), LiveRecordBean.class));
                        }
                    }
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private StringCallback loadMoreCallBack = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(LiveRecordActivity.this, "获取直播纪录失败");
//            refreshLayout.finishRefresh();
            refreshLayout.finishRefreshLoadMore();
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
//            refreshLayout.finishRefresh();
            refreshLayout.finishRefreshLoadMore();
            if (null != res) {
                try {
                    JSONObject liveRecordJsonObj = new JSONObject(res);
                    JSONArray liveRecordJsonArray = liveRecordJsonObj.getJSONArray("list");
                    if (0 < liveRecordJsonArray.length()) {
                        Gson g = new Gson();
                        for (int i = 0; i < liveRecordJsonArray.length(); i++) {
                            mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i), LiveRecordBean.class));
                        }
                    } else {
                        AppContext.showToastShort("没有更多数据了~~");
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

//    @Override
//    public void initData() {
//        setActionBarTitle(getString(R.string.liverecord));
//        requestData();
//    }

    //请求数据
    private void requestData() {

        PhoneLiveApi.getLiveRecord(getIntent().getIntExtra("uid", 0), requestLiveRecordDataCallback);
    }

    private void fillUI() {

        adapter = new LiveRecordAdapter(LiveRecordActivity.this, mRecordList);

        mLiveRecordList.setAdapter(adapter);

        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mRecordList.clear();
                requestData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                page++;
                PhoneLiveApi.getLiveRecordByPage(getIntent().getIntExtra("uid", 0), loadMoreCallBack, page);

            }

            @Override
            public void onfinish() {
                super.onfinish();

            }
        });


//// refresh complete
//        refreshLayout.finishRefresh();
//
//// load more refresh complete
//        refreshLayout.finishRefreshLoadMore();
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public ProgressDialog showWaitDialog(String text) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, text);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(text);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    //    @Override
//    public void onClick(View v) {
//
//    }
//
//    @Override
//    protected boolean hasBackButton() {
//        return true;
//    }
    public void showToast3(String msg, int time) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int msgResid, int icon, int gravity) {
        showToast(getString(msgResid), icon, gravity);
    }

    public void showToast2(String msg) {
        AppContext.showToastAppMsg(this, msg);
    }

    public void showToast(String message, int icon, int gravity) {
        CommonToast toast = new CommonToast(this);
        toast.setMessage(message);
        toast.setMessageIc(icon);
        toast.setLayoutGravity(gravity);
        toast.show();
    }

    private class LiveRecordAdapter extends SlideBaseAdapter {

        ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

        public LiveRecordAdapter(Context context, ArrayList<LiveRecordBean> mRecordList) {
            super(context);
            this.mRecordList = mRecordList;
        }

        @Override
        public int getCount() {
            return mRecordList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public SlideListView.SlideMode getSlideModeInPosition(int position) {
            return SlideListView.SlideMode.RIGHT;

//            return super.getSlideModeInPosition(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = createConvertView(position);
                viewHolder = new ViewHolder();
                viewHolder.mLiveNum = (TextView) convertView.findViewById(R.id.tv_item_live_record_num);
                viewHolder.mLiveTime = (TextView) convertView.findViewById(R.id.tv_item_live_record_time);
                viewHolder.mLiveTitle = (TextView) convertView.findViewById(R.id.tv_item_live_record_title);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                AutoUtils.autoSize(convertView);
            }
            LiveRecordBean l = mRecordList.get(position);
            viewHolder.mLiveNum.setText(l.getNums());
            viewHolder.mLiveTitle.setText(l.getTitle());
            viewHolder.mLiveTime.setText(l.getDatetime());
            if (viewHolder.delete != null) {
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mLiveRecordList.setBackgroundResource(R.color.half_transparent);
                        View mDialogView = View.inflate(LiveRecordActivity.this, R.layout.dialog_show_own_info_detail, null);
                        TextView showMessage = (TextView) mDialogView.findViewById(R.id.show_msg);
                        Button yes = (Button) mDialogView.findViewById(R.id.yes);
                        Button no = (Button) mDialogView.findViewById(R.id.no);
                        showMessage.setTextColor(Color.BLACK);
                        showMessage.setText("确定删除吗？");
                        yes.setTextColor(ContextCompat.getColor(LiveRecordActivity.this, R.color.dialog_blue));
                        yes.setText("取消");
                        no.setTextColor(ContextCompat.getColor(LiveRecordActivity.this, R.color.dialog_blue));
                        no.setText("删除");
                        final Dialog dialog = new Dialog(LiveRecordActivity.this, R.style.dialog);
                        dialog.setContentView(mDialogView);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLiveRecordList.setBackgroundResource(R.color.transparent);

                                dialog.dismiss();


//                                finish();
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLiveRecordList.setBackgroundResource(R.color.transparent);
                                dialog.dismiss();
                                PhoneLiveApi.deleteLiveRecord(mRecordList.get(position).getId(), new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {

                                        AppContext.showToastShort("网络错误！");
                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        KLog.json(response);

                                        String res = ApiUtils.getResponse(response);
                                        KLog.json(res);

                                        try {
                                            JSONObject json = new JSONObject(res);
                                            int code = json.getInt("code");
                                            if (code == 0) {
                                                AppContext.showToastShort("删除成功");
                                                mRecordList.remove(mRecordList.get(position));
                                                notifyDataSetChanged();
                                            } else {
                                                AppContext.showToastShort("删除失败");
                                            }
                                        } catch (JSONException | NullPointerException e) {
                                            e.printStackTrace();
                                            AppContext.showToastShort("网络错误！");
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            return convertView;
        }


        @Override
        public int getFrontViewId(int position) {
            return R.layout.item_live_record;
        }

        @Override
        public int getLeftBackViewId(int position) {
            return R.layout.row_left_back_view;
        }

        @Override
        public int getRightBackViewId(int position) {
            return R.layout.row_right_back_view;
        }


        class ViewHolder {
            TextView mLiveTime, mLiveNum, mLiveTitle;
            Button delete;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getLiveRecordById");
    }
}
