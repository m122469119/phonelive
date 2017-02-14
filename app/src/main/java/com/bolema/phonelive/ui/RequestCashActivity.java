package com.bolema.phonelive.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.api.remote.ApiUtils;
import com.bolema.phonelive.api.remote.PhoneLiveApi;
import com.bolema.phonelive.base.ToolBarBaseActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

public class RequestCashActivity extends ToolBarBaseActivity {


    @InjectView(R.id.et_cash_num)
    EditText etCashNum;
    @InjectView(R.id.btn_request_cash)
    Button btnRequestCash;

    @Override
    protected int getLayoutId() {

        return R.layout.activity_request_cash;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        setActionBarTitle("金额提现");
    }

    @Override
    public void initData() {

    }


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }



    @OnClick(R.id.btn_request_cash)
    public void onClick(View v) {

        if(etCashNum.getText().toString().equals(""))
        {
            showToast2("请输入提现金额");
            return;
        }

        PhoneLiveApi.requestCash(getUserID(),getUserToken(),etCashNum.getText().toString(),
                new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e) {
                        AppContext.showToastAppMsg(RequestCashActivity.this,"接口请求失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        String res = ApiUtils.checkIsSuccess(response);
                        if(null != res){
                            AppContext.showToastAppMsg(RequestCashActivity.this,res);
                        }
                    }
                });
    }
}
