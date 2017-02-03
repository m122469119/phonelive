package com.bolema.phonelive.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolema.phonelive.bean.UserBean;
import com.bolema.phonelive.utils.UIHelper;
import com.bolema.phonelive.widget.AvatarView;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.em.ChangInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 用户信息详情页面
 */
public class UserInfoDetailActivity extends ToolBarBaseActivity {

    @InjectView(R.id.et_info_birthday)
    EditText etInfoBirthday;
    private UserBean mUser;
    @InjectView(R.id.rl_userHead)
    RelativeLayout mRlUserHead;
    @InjectView(R.id.rl_userNick)
    RelativeLayout mRlUserNick;
    @InjectView(R.id.rl_userSign)
    RelativeLayout mRlUserSign;
    @InjectView(R.id.rl_userSex)
    RelativeLayout mRlUserSex;
    @InjectView(R.id.tv_userNick)
    TextView mUserNick;
    @InjectView(R.id.tv_userSign)
    TextView mUserSign;
    @InjectView(R.id.av_userHead)
    AvatarView mUserHead;
    @InjectView(R.id.iv_info_sex)
    ImageView mUserSex;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {
        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        final Calendar c = Calendar.getInstance();
        etInfoBirthday.setOnClickListener(new View.OnClickListener() { //生日修改
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(UserInfoDetailActivity.this,R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        if(c.getTime().getTime()>new Date().getTime())
                        {
                            showToast2("请选择正确的日期");
                            return;
                        }
                        final String birthday=DateFormat.format("yyy-MM-dd", c).toString();
                        PhoneLiveApi.saveInfo("birthday", birthday,
                                AppContext.getInstance().getLoginUid(),
                                AppContext.getInstance().getToken(),
                                new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {
                                        showToast2(getString(R.string.editfail));
                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        String res = ApiUtils.checkIsSuccess(response);
                                        if(null != res){
                                            AppContext.showToastAppMsg(UserInfoDetailActivity.this,getString(R.string.editsuccess));
                                            UserBean u =  AppContext.getInstance().getLoginUser();
                                            u.setBirthday(birthday);
                                            AppContext.getInstance().updateUserInfo(u);
                                            etInfoBirthday.setText(birthday);

                                        }
                                    }
                                });

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                try {
                    dialog.getDatePicker().setMinDate(new SimpleDateFormat("yyyy-MM-dd").parse("1950-01-01").getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

                dialog.show();
            }
        });

    }

    @Override
    public void initData() {
        setActionBarTitle(R.string.editInfo);
        sendRequiredData();
    }

    private void sendRequiredData() {
        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), callback);
    }

    @Override
    public void onClick(View v) {
        if (mUser != null) {
            switch (v.getId()) {
                case R.id.rl_userNick:
                    UIHelper.showEditInfoActivity(
                            this, "修改昵称",
                            getString(R.string.editnickpromp),
                            mUser.getUser_nicename(),
                            ChangInfo.CHANG_NICK);
                    break;
                case R.id.rl_userSign:
                    UIHelper.showEditInfoActivity(
                            this, "修改签名",
                            getString(R.string.editsignpromp),
                            mUser.getSignature(),
                            ChangInfo.CHANG_SIGN);
                    break;
                case R.id.rl_userHead:
                    UIHelper.showSelectAvatar(this, mUser.getAvatar());
                    break;
                case R.id.rl_userSex:
                    UIHelper.showChangeSex(this, mUser.getSex());
                    break;

            }
        }

    }

    @Override
    protected void onRestart() {
        mUser = AppContext.getInstance().getLoginUser();
        fillUI();
        super.onRestart();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String s) {

            if (ApiUtils.checkIsSuccess(s) != null) {
                mUser = new Gson().fromJson(ApiUtils.checkIsSuccess(s), UserBean.class);
                fillUI();
            } else {

            }
        }
    };

    @Override
    protected void onStart() {
        if (mUser != null) {
            fillUI();
        }

        super.onStart();
    }


    private void fillUI() {
        mUserNick.setText(mUser.getUser_nicename());
        mUserSign.setText(mUser.getSignature());
        mUserHead.setAvatarUrl(mUser.getAvatar());
        mUserSex.setImageResource(mUser.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
        etInfoBirthday.setText(mUser.getBirthday());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getMyUserInfo");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("个人中心"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人中心"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
