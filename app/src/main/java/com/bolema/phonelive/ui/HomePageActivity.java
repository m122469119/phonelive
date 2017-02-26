package com.bolema.phonelive.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.LiveRecordBean;
import com.bolema.phonelive.bean.OrderBean;
import com.bolema.phonelive.bean.PrivateChatUserBean;
import com.bolema.phonelive.bean.ProfitBean;
import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.bean.UserHomePageBean;
import com.bolema.phonelive.cache.DataSingleton;
import com.bolema.phonelive.utils.BlurUtil;
import com.bolema.phonelive.utils.GsonTools;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.AvatarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 他人信息
 */
public class HomePageActivity extends ToolBarBaseActivity {
    @InjectView(R.id.tv_home_page_send_num)
    TextView mSendNum;//送出钻
    @InjectView(R.id.tv_home_page_uname)
    TextView mUNice;//昵称
    @InjectView(R.id.iv_home_page_sex)
    ImageView mUSex;
    @InjectView(R.id.iv_home_page_level)
    ImageView mULevel;
    @InjectView(R.id.av_home_page_uhead)
    AvatarView mUHead;//头像
    @InjectView(R.id.tv_home_page_follow)
    TextView mUFollowNum;//关注数
    @InjectView(R.id.tv_home_page_fans)
    TextView mUFansNum;//粉丝数
    @InjectView(R.id.tv_home_page_sign)
    TextView mUSign;//个性签名
    @InjectView(R.id.tv_home_page_sign2)
    TextView mUSign2;
    @InjectView(R.id.tv_home_page_num)
    TextView mUNum;
    @InjectView(R.id.ll_default_video)
    LinearLayout mDefaultVideoBg;

    @InjectView(R.id.ll_home_page_index)
    LinearLayout mHomeIndexPage;

    @InjectView(R.id.ll_home_page_video)
    LinearLayout mHomeVideoPage;

    @InjectView(R.id.tv_home_page_index_btn)
    TextView mPageIndexBtn;

    @InjectView(R.id.tv_home_page_video_btn)
    TextView mPageVideoBtn;

    @InjectView(R.id.tv_home_page_menu_follow)
    TextView mFollowState;

    @InjectView(R.id.tv_home_page_black_state)
    TextView mTvBlackState;

    @InjectView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;

    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;
    @InjectView(R.id.iv_un_follow)
    ImageView ivUnFollow;
    @InjectView(R.id.head_layout)
    AutoLinearLayout headLayout;
    @InjectView(R.id.tv_now_living)
    TextView tvNowLiving;

    private List<UserBean> mUserList = new ArrayList<>();

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;
    private int uid;
    AvatarView[] mOrderTopNoThree = new AvatarView[3];
    private UserHomePageBean mUserHomePageBean;
    ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

    private int live = 0;
    private UserBean userBean = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);

        mOrderTopNoThree[0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree[1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree[2] = (AvatarView) findViewById(R.id.av_home_page_order3);
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);
                //开始播放
                showLiveRecord();
            }
        });
    }

    @Override
    public void initData() {
        uid = getIntent().getIntExtra("uid", 0);
        if (uid == AppContext.getInstance().getLoginUid()) {
            mLLBottomMenu.setVisibility(View.GONE);
        }
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(HomePageActivity.this, "获取用户信息失败");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);

                try {
                    JSONObject json = new JSONObject(response);

                    JSONObject dataJson = json.getJSONObject("data");

                    live = dataJson.getInt("live");

                    JSONObject liveInfoJson = dataJson.getJSONObject("liveinfo");

                    if (live == 1) {
                        userBean = new Gson().fromJson(liveInfoJson.toString(), UserBean.class);
                        userBean.setId(userBean.getUid());
                        mUserList.add(userBean);
                    } else {
                        userBean = null;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (res != null) {
                    mUserHomePageBean = new Gson().fromJson(res, UserHomePageBean.class);
                    fillUI();
                }



            }
        };
        //请求用户信息
        PhoneLiveApi.getHomePageUInfo(AppContext.getInstance().getLoginUid(), uid, callback);
    }

    private void fillList() {
        mLiveRecordList.setAdapter(new LiveRecordAdapter());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    private class LiveRecordAdapter extends BaseAdapter {

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
            if (null == convertView) {
                convertView = View.inflate(HomePageActivity.this, R.layout.item_live_record, null);
                viewHolder = new ViewHolder();
                viewHolder.mLiveNum = (TextView) convertView.findViewById(R.id.tv_item_live_record_num);
                viewHolder.mLiveTime = (TextView) convertView.findViewById(R.id.tv_item_live_record_time);
                viewHolder.mLiveTitle = (TextView) convertView.findViewById(R.id.tv_item_live_record_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            LiveRecordBean l = mRecordList.get(position);
            viewHolder.mLiveNum.setText(l.getNums());
            viewHolder.mLiveTitle.setText(l.getTitle());
            viewHolder.mLiveTime.setText(l.getDatetime());
            return convertView;
        }

        class ViewHolder {
            TextView mLiveTime, mLiveNum, mLiveTitle;
        }
    }

    private void fillUI() {//ui填充
        mSendNum.setText(getString(R.string.send) + "" + mUserHomePageBean.getConsumption());

        mUNice.setText(mUserHomePageBean.getUser_nicename());
        mUSex.setImageResource(mUserHomePageBean.getSex() == 1 ? R.drawable.choice_sex_male : R.drawable.choice_sex_femal);
        mULevel.setImageResource(DrawableRes.LevelImg[(mUserHomePageBean.getLevel() == 0 ? 0 : mUserHomePageBean.getLevel() - 1)]);
        mUFansNum.setText(getString(R.string.fans) + ":" + mUserHomePageBean.getFansnum());
        mUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.getAttentionnum());
        mUSign.setText(mUserHomePageBean.getSignature());
        mUSign2.setText(mUserHomePageBean.getSignature());
        mUNum.setText(mUserHomePageBean.getId() + "");
        mFollowState.setText(mUserHomePageBean.getIsattention() == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));

        mTvBlackState.setText(mUserHomePageBean.getIsblack() == 0 ? getString(R.string.pullblack) : getString(R.string.relieveblack));
        List<OrderBean> os = mUserHomePageBean.getCoinrecord3();
        for (int i = 0; i < os.size(); i++) {
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }
        tvNowLiving.setVisibility(userBean != null ? View.VISIBLE : View.INVISIBLE);

        tvNowLiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneLiveApi.isStillLiving(String.valueOf(userBean.getUid()), callback);

            }
        });
//        获取头像
//        mUHead.setAvatarUrl(mUserHomePageBean.getAvatar());
        Glide.with(this)
                .load(mUserHomePageBean.getAvatar())
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    Bitmap bitmap;

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mUHead.setImageBitmap(resource);
                        int mWidth = resource.getWidth();
                        int mHeight = resource.getHeight();
                        if (mHeight < 400) {
                            bitmap = BlurUtil.doBlur(Bitmap.createBitmap(resource, 0, 0, mWidth, mHeight), 50, true);
                        } else {
                            bitmap = BlurUtil.doBlur(Bitmap.createBitmap(resource, 0, 0, mWidth, 400), 50, true);
                        }
                        headLayout.setBackground(new BitmapDrawable(getResources(),
                                bitmap));
                    }
                });
        //字体模糊化效果
        mSendNum.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mUNice.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mUFansNum.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mUFollowNum.setShadowLayer(10F, 1F, 1F, Color.BLACK);
        mUSign.setShadowLayer(10F, 1F, 1F, Color.BLACK);


    }

    /**
     * 请求服务器判断主播是否仍在直播回调
     * @param v
     */
    public StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            KLog.json(res);


            try {
                JSONObject resJson = new JSONObject(response);
                if(Integer.parseInt(resJson.getString("ret")) == 200){
                    JSONObject dataJson =  resJson.getJSONObject("data");
                    JSONArray jsonArray = dataJson.getJSONArray("info");
                    UserBean userBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), UserBean.class);

                    String islive = jsonArray.getJSONObject(0).getString("islive");
                    if (islive.equals("1")) {
                        DataSingleton.getInstance().setUserList(mUserList);  //HHH 2016-09-10
                        DataSingleton.getInstance().setPostion(0);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("USER_INFO", userBean);
                        UIHelper.showLookLiveActivity(HomePageActivity.this, bundle);
                    } else {
                        AppContext.showToastShort("直播已结束");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @OnClick({R.id.ll_home_page_menu_lahei, R.id.ll_home_page_menu_privatechat, R.id.ll_home_page_menu_follow, R.id.rl_home_pager_yi_order, R.id.tv_home_page_follow, R.id.tv_home_page_index_btn, R.id.tv_home_page_video_btn, R.id.iv_home_page_back, R.id.tv_home_page_fans})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_page_menu_privatechat:
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
                pullTheBlack();
                break;
            case R.id.ll_home_page_menu_follow:
                followUserOralready();
                break;
            case R.id.tv_home_page_index_btn:
                mHomeIndexPage.setVisibility(View.VISIBLE);
                mHomeVideoPage.setVisibility(View.GONE);
                mPageIndexBtn.setTextColor(ContextCompat.getColor(this, R.color.global));
                mPageVideoBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                break;
            case R.id.tv_home_page_video_btn:
                mHomeIndexPage.setVisibility(View.GONE);
                mHomeVideoPage.setVisibility(View.VISIBLE);
                mPageIndexBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                mPageVideoBtn.setTextColor(ContextCompat.getColor(this, R.color.global));
                //直播记录回放
                requestData();
                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                UIHelper.showFansActivity(this, uid);
                break;
            case R.id.tv_home_page_follow:
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order://魅力值排行榜
                UIHelper.showDedicateOrderActivity(this, uid);
                break;
        }

    }

    private void requestData() {
        PhoneLiveApi.getLiveRecord(getIntent().getIntExtra("uid", 0), requestLiveRecordDataCallback);
    }

    private StringCallback requestLiveRecordDataCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {
            AppContext.showToastAppMsg(HomePageActivity.this, "获取直播纪录失败");
        }

        @Override
        public void onResponse(String response) {
            String res = ApiUtils.checkIsSuccess(response);
            mRecordList.clear();
            if (null != res) {
                try {
                    JSONObject liveRecordJsonObj = new JSONObject(res);
                    JSONArray liveRecordJsonArray = liveRecordJsonObj.getJSONArray("list");
                    if (0 < liveRecordJsonArray.length()) {
                        Gson g = new Gson();
                        for (int i = 0; i < liveRecordJsonArray.length(); i++) {
                            mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i), LiveRecordBean.class));
                        }
//                        mHomeVideoPage.setVisibility(View.GONE);
//                        mHomeIndexPage.setVisibility(View.VISIBLE);
                        mDefaultVideoBg.setVisibility(View.GONE);
                    } else {
                        mDefaultVideoBg.setVisibility(View.VISIBLE);
                    }
                    fillList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

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
                mLiveRecordBean.setVideo_url(res.trim());
                VideoBackActivity.startVideoBack(HomePageActivity.this, mLiveRecordBean);
            } else {
                //showToast3("视频暂未生成,请耐心等待",3);
            }
        }
    };

    private void pullTheBlack() {// black list
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(), uid,
                AppContext.getInstance().getToken(),
                new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        AppContext.showToastAppMsg(HomePageActivity.this, "操作失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response);
                        if (null == res) return;
                        if (mUserHomePageBean.getIsblack() == 0) {
                            //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                            try {
                                EMClient.getInstance().contactManager().addUserToBlackList(String.valueOf(mUserHomePageBean.getId()), true);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mUserHomePageBean.getId()));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                        AppContext.showToastAppMsg(HomePageActivity.this, mUserHomePageBean.getIsblack() == 0 ? "拉黑成功" : "解除拉黑");
                        mUserHomePageBean.setIsblack(mUserHomePageBean.getIsblack() == 0 ? 1 : 0);
                        mTvBlackState.setText(mUserHomePageBean.getIsblack() == 0 ? getString(R.string.pullblack) : getString(R.string.relieveblack));

                    }
                });
    }

    //私信
    private void openPrivateChat() {
        if (mUserHomePageBean.getIsblackto() == 1) {
            AppContext.showToastAppMsg(this, "你已被对方拉黑无法私信");
            return;
        }
        if (null != mUserHomePageBean) {
            PhoneLiveApi.getPmUserInfo(AppContext.getInstance().getLoginUid(), mUserHomePageBean.getId(), new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    String res = ApiUtils.checkIsSuccess(response);
                    if (null != res)
                        UIHelper.showPrivateChatMessage(HomePageActivity.this, new Gson().fromJson(res, PrivateChatUserBean.class));

                }
            });

        }

    }


    private void followUserOralready() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                KLog.json(response);

                mUserHomePageBean.setIsattention(mUserHomePageBean.getIsattention() == 0 ? 1 : 0);
                mFollowState.setText(mUserHomePageBean.getIsattention() == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));
                ivUnFollow.setImageResource(mUserHomePageBean.getIsattention() == 0 ? R.drawable.weiguanzhu : R.drawable.yiguanzhu);

            }
        };
        PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), uid, AppContext.getInstance().getToken(), callback);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("个人主页"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人主页"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {//BBB
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
    }

    /**
     * 兑换播币
     */
    public static class ExchangeVoteActivity extends ToolBarBaseActivity {

        @InjectView(R.id.tv_coin)
        TextView tvCoin;
        @InjectView(R.id.rl_message)
        LinearLayout rlMessage;
        @InjectView(R.id.tv_exchange_rate)
        TextView tvExchangeRate;
        @InjectView(R.id.et_diamonds_num)
        EditText etDiamondsNum;
        @InjectView(R.id.tv_votes_num)
        TextView tvVotesNum;
        @InjectView(R.id.btn_exchange_vote)
        Button btnExchangeVote;
        @InjectView(R.id.tv_exchange_note)
        TextView tvExchangeNote;


        private ProfitBean mProfitBean;
        private int uid;
        private int exRate;
        private String votes;
        private boolean isLoaded;

        @Override
        protected boolean hasActionBar() {
            return true;
        }

        @Override
        protected boolean hasBackButton() {
            return true;
        }

        @Override
        protected int getLayoutId() {
            return R.layout.activity_exchange_vote;
        }

        private TextWatcher watcher = new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                tvVotesNum.setText("");
                String diamondsNumStr = etDiamondsNum.getText().toString().trim();
                if (diamondsNumStr.equals("")) {
                    tvCoin.setText(votes);
                    return;
                }
                int votesNum = Integer.parseInt(etDiamondsNum.getText().toString()) * exRate/3;
                if (votesNum < 0 || votesNum > Integer.parseInt(votes)) {
                    tvCoin.setText("0");
                    tvVotesNum.setText(votes);
                    btnExchangeVote.setEnabled(false);
                    return;
                } else {
                    btnExchangeVote.setEnabled(true);
                }
                tvVotesNum.setText(String.valueOf(votesNum));
                tvCoin.setText(String.valueOf(Integer.parseInt(votes) - votesNum));

            }

        };

        @Override
        public void initView() {

            etDiamondsNum.addTextChangedListener(watcher);
        }

        @Override
        public void initData() {
            setActionBarTitle("兑换播币");
            uid = getIntent().getIntExtra("uid", -1);
            requestData();
        }

        private StringCallback getVoteRateConfigCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                showToast2("获取信息失败,请检查网络设置");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                Gson g = new Gson();
                if (res != null) {
                    try {
                        JSONObject jsono = new JSONObject(res);
                        if (jsono.has("ex_rate")) {
                            exRate = jsono.getInt("ex_rate");
                            tvExchangeNote.setText(exRate + "魅力值可兑换3播币");
                            tvExchangeRate.setText("3:" + exRate);
                        } else {
                            Toast.makeText(ExchangeVoteActivity.this, "兑换比率获取异常", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        private StringCallback exchangVoteCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                showToast2("获取信息失败,请检查网络设置");
            }

            @Override
            public void onResponse(String response) {
                String res = ApiUtils.checkIsSuccess(response);
                Gson g = new Gson();
                if (res != null) {
                    Toast.makeText(ExchangeVoteActivity.this, res, Toast.LENGTH_SHORT).show();
                    votes = tvCoin.getText().toString();
                    etDiamondsNum.setText("");
                }
            }
        };


        private void requestData() {

            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    String res = ApiUtils.checkIsSuccess(response);

                    if (null != res) {
                        mProfitBean = new Gson().fromJson(res, ProfitBean.class);
                        votes = mProfitBean.getVotes();
                        tvCoin.setText(mProfitBean.getVotes());
                    }
                }
            };
            PhoneLiveApi.getWithdraw(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);

            PhoneLiveApi.getCharge(AppContext.getInstance().getLoginUid(), new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(String response) {

                    String res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        try {
                            JSONObject object = new JSONObject(res);
                            //mCoin.setText(object.getString("coin"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            PhoneLiveApi.getConfig(getVoteRateConfigCallback);
        }

        @OnClick(R.id.btn_exchange_vote)
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_exchange_vote:
                    PhoneLiveApi.exchangVote(uid, tvVotesNum.getText().toString(), exchangVoteCallback);
                    break;
            }

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ButterKnife.inject(this);
        }
    }
}
