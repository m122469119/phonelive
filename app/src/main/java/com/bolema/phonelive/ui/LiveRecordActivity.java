package com.bolema.phonelive.ui;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.LiveRecordBean;
import com.google.gson.Gson;
import com.bolema.phonelive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends ToolBarBaseActivity {
    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;
    ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;
    @Override
    protected int getLayoutId() {

        return R.layout.activity_live_record;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);

        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);
                showLiveRecord();
            }
        });
    }

    //打开回放记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...");
        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(),showLiveByIdCallback);

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
            if(res != null){
                Log.d("video_url", res.trim());
                mLiveRecordBean.setVideo_url(res.trim());
                VideoBackActivity.startVideoBack(LiveRecordActivity.this,mLiveRecordBean);
            }else{
                showToast3("视频暂未生成,请耐心等待",3);
            }
        }
    };
    private StringCallback requestLiveRecordDataCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(LiveRecordActivity.this,"获取直播纪录失败");
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            if(null != res){
                try {
                    JSONObject liveRecordJsonObj = new JSONObject(res);
                    JSONArray liveRecordJsonArray = liveRecordJsonObj.getJSONArray("list");
                    if(0 < liveRecordJsonArray.length()){
                        Gson g = new Gson();
                        for(int i = 0; i < liveRecordJsonArray.length(); i++){
                            mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i),LiveRecordBean.class));
                        }
                    }
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.liverecord));
        requestData();
    }

    //请求数据
    private void requestData() {

        PhoneLiveApi.getLiveRecord(getIntent().getIntExtra("uid",0),requestLiveRecordDataCallback);
    }

    private void fillUI() {
        mLiveRecordList.setAdapter(new LiveRecordAdapter());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }
    private class LiveRecordAdapter extends BaseAdapter{

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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(null == convertView){
                convertView = View.inflate(LiveRecordActivity.this,R.layout.item_live_record,null);
                viewHolder = new ViewHolder();
                viewHolder.mLiveNum = (TextView) convertView.findViewById(R.id.tv_item_live_record_num);
                viewHolder.mLiveTime = (TextView) convertView.findViewById(R.id.tv_item_live_record_time);
                viewHolder.mLiveTitle = (TextView) convertView.findViewById(R.id.tv_item_live_record_title);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            LiveRecordBean l = mRecordList.get(position);
            viewHolder.mLiveNum.setText(l.getNums());
            viewHolder.mLiveTitle.setText(l.getTitle());
            viewHolder.mLiveTime.setText(l.getDatetime());
            return convertView;
        }
        class ViewHolder{
            TextView mLiveTime,mLiveNum,mLiveTitle;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getLiveRecordById");
    }
}
