package com.bolema.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.AreaBean;
import com.bolema.phonelive.viewpagerfragment.IndexPagerFragment;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.R;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

//地区选择
public class SelectAreaFragment extends BaseFragment {
    @InjectView(R.id.lv_area)
    ListView mLvArea;
    List<AreaBean> mAreaList = new ArrayList<>();
    @InjectView(R.id.iv_choice_femal)
    ImageView mIconFemal;
    @InjectView(R.id.iv_choice_male)
    ImageView mIconMale;
    @InjectView(R.id.iv_choice_all)
    ImageView mIconAll;
    @InjectView(R.id.tv_choice_femal)
    TextView mTextFemal;
    @InjectView(R.id.tv_choice_male)
    TextView mTextMale;
    @InjectView(R.id.tv_choice_all)
    TextView mTextAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_class,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initData() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                if(null != res){
                    try {
                        JSONArray areaListJsonArray = new JSONArray(res);
                        Gson g = new Gson();
                        for(int i = 0; i<areaListJsonArray.length(); i++ ){
                            mAreaList.add(g.fromJson(areaListJsonArray.getString(i),AreaBean.class));
                        }
                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        PhoneLiveApi.getAreaList(callback);
    }

    private void fillUI() {
        mLvArea.setAdapter(new AreaAdapter());
    }
    @OnClick({R.id.btn_area_complete,R.id.iv_choice_femal,R.id.iv_choice_all,R.id.iv_choice_male})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_area_complete:
                getActivity().finish();
                break;
            case R.id.iv_choice_femal:
                showSelectSex(2);
                break;
            case R.id.iv_choice_all:
                showSelectSex(0);
                break;
            case R.id.iv_choice_male:
                showSelectSex(1);
                break;
        }
    }
    private void showSelectSex(int sex){
        if(sex == 2){
            IndexPagerFragment.mSex = 2;
            mIconFemal.setImageResource(R.drawable.choice_sex_femal);
            mIconMale.setImageResource(R.drawable.choice_sex_un_male);
            mIconAll.setImageResource(R.drawable.choice_sex_un_all);
            mTextFemal.setTextColor(ContextCompat.getColor(getContext(),R.color.global));
            mTextMale.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
            mTextAll.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
        }else if(sex == 0){
            IndexPagerFragment.mSex = 0;
            mIconAll.setImageResource(R.drawable.choice_sex_all);
            mIconMale.setImageResource(R.drawable.choice_sex_un_male);
            mIconFemal.setImageResource(R.drawable.choice_sex_un_femal);
            mTextAll.setTextColor(ContextCompat.getColor(getContext(),R.color.global));
            mTextMale.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
            mTextFemal.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
        }else{
            IndexPagerFragment.mSex = 1;
            mIconMale.setImageResource(R.drawable.choice_sex_male);
            mIconAll.setImageResource(R.drawable.choice_sex_un_all);
            mIconFemal.setImageResource(R.drawable.choice_sex_un_femal);
            mTextMale.setTextColor(ContextCompat.getColor(getContext(),R.color.global));
            mTextMale.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
            mTextAll.setTextColor(ContextCompat.getColor(getContext(),R.color.home_page_text_color));
        }
    }

    @Override
    public void initView(View view) {
        showSelectSex(IndexPagerFragment.mSex);
        mLvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexPagerFragment.mArea = mAreaList.get(position).getProvince();
                getActivity().finish();
            }
        });

    }
    class AreaAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mAreaList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAreaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(null == convertView){
                convertView = View.inflate(getActivity(),R.layout.item_hot_area,null);
                viewHolder = new ViewHolder();
                viewHolder.mArea = (TextView) convertView.findViewById(R.id.tv_area);
                viewHolder.mNums = (TextView) convertView.findViewById(R.id.tv_area_live_num);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AreaBean a = mAreaList.get(position);
            if(IndexPagerFragment.mArea.equals(a.getProvince())){
                viewHolder.mNums.setText(a.getTotal() + "  √");
            }else if(IndexPagerFragment.mArea.equals("")&&a.getProvince().equals("热门")){
                viewHolder.mNums.setText(a.getTotal() + "  √");
            }else{
                viewHolder.mNums.setText(a.getTotal() + "");
            }
            viewHolder.mArea.setText(a.getProvince());
            return convertView;
        }
        class ViewHolder{
            TextView mArea,mNums;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("选择地区"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("选择地区"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
}
