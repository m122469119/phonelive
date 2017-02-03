package com.bolema.phonelive.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.bolema.phonelive.bean.ProfitBean;
import com.bolema.phonelive.bean.RechargeBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 兑换播币
 */
public class ExchangeVoteActivity extends ToolBarBaseActivity {

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
            int votesNum = Integer.parseInt(etDiamondsNum.getText().toString()) * exRate;
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
                        tvExchangeNote.setText(exRate+"魅力值可兑换1播币");
                        tvExchangeRate.setText("1:" + exRate);
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
                if(res != null){
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
