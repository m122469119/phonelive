package com.bolema.phonelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.adapter.UserBaseInfoAdapter;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.BaseFragment;
import com.bolema.phonelive.bean.TopicBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户搜索
 */
public class SearchFragment extends BaseFragment {
    @InjectView(R.id.et_search_input)
    EditText mSearchKey;
    @InjectView(R.id.lv_search)
    ListView mLvSearch;
    @InjectView(R.id.flex_box_layout)
    FlexboxLayout flexBoxLayout;
    private List<UserBean> mUserList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_index, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        mLvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(getActivity(), mUserList.get(position).getId());
            }
        });
    }

    @Override
    public void initData() {
        requestData();
    }

    private void requestData() {
        PhoneLiveApi.getTopics("999", "2", "0", getTopicsCallback);
    }

    private StringCallback getTopicsCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

            AppContext.showToastShort("网络连接失败，请检查网络");
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);

//            int screenWidth = DensityUtils.getScreenW(getContext());
//            int screenHeight = DensityUtils.getScreenH(getContext());
//            int textViewHeight = 50 * screenHeight / 1280;
//            int textSize = 10 * screenWidth / 720;

            Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();


            if (res != null) {

                try {
                    JSONArray array = new JSONArray(res);
//                    TopicListBean listBean = GsonTools.instance(res, TopicListBean.class);

                    for (int i = 0; i < array.length(); i++) {

                        View view = LayoutInflater.from(getContext()).inflate(R.layout.topic_text_view, null, false);

//                        mTempTopicSquares.add(g.fromJson(array.getString(i), TopicBean.class));
                        final TopicBean topicBean = g.fromJson(array.getString(i), TopicBean.class);
                        TextView textView = (TextView) view.findViewById(R.id.topic_text_view);
                        textView.setText(topicBean.getName().replace("#", ""));
//                        textView.setTextSize(textSize);
//                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.topic_text_color));
//                        textView.setBackgroundResource(R.drawable.topic_background);
//                        textView.setPadding(20* screenWidth / 720,10* screenWidth / 720,20* screenWidth / 720,10* screenWidth / 720);
//
////                        View view = flexBoxLayout.getChildAt(0);
////                        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) view.getLayoutParams();
////                        lp.setOrder(-1);
////                        lp.setFlexGrow(2);
////                        textView.setLayoutParams(lp);
//                        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(10* screenWidth / 720, 12* screenWidth / 720, 10* screenWidth / 720, 12* screenWidth / 720);
//                        textView.setLayoutParams(params);


                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIHelper.showTopicLiveRoomActivity(getActivity(),topicBean.getName());
                            }
                        });
                        flexBoxLayout.addView(view);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    };

    @OnClick({R.id.iv_private_chat_back, R.id.tv_search_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_private_chat_back:
                getActivity().finish();
                break;
            case R.id.tv_search_btn:
                search();
                break;

        }
    }

    //搜索
    private void search() {
        showWaitDialog();
        String screenKey = mSearchKey.getText().toString().trim();
        if (!screenKey.equals("")) {
            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    hideWaitDialog();
                }

                @Override
                public void onResponse(String response) {
                    hideWaitDialog();
                    String res = ApiUtils.checkIsSuccess(response);

                    if (null != res) {
                        Gson g = new Gson();
                        try {
                            JSONArray searchUserJsonArray = new JSONArray(res);
                            mUserList.clear();
                            if (searchUserJsonArray.length() == 0) {
                                flexBoxLayout.setVisibility(View.VISIBLE);
                                return;
                            } else {
                                flexBoxLayout.setVisibility(View.GONE);

                                for (int i = 0; i < searchUserJsonArray.length(); i++) {
                                    mUserList.add(g.fromJson(searchUserJsonArray.getString(i), UserBean.class));
                                }

                                fillUI();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            PhoneLiveApi.search(screenKey, callback, AppContext.getInstance().getLoginUid());
        }
    }

    private void fillUI() {

        mLvSearch.setAdapter(new UserBaseInfoAdapter(mUserList));

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("搜索"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("搜索"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
