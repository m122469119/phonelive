package com.bolema.phonelive.ui;

import android.view.View;
import android.widget.TextView;

import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.AppManager;
import com.bolema.phonelive.R;
import com.bolema.phonelive.base.ToolBarBaseActivity;

import butterknife.InjectView;

//支付宝回调页面
public class AlipayResultActivity extends ToolBarBaseActivity {
    @InjectView(R.id.tv_alipaypay_result)
    TextView mAliPayResult;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_alipay_result;
    }

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.payresult));
        if(getIntent().getIntExtra("result",0) == 1){
            mAliPayResult.setText("ok");
        }else{
            mAliPayResult.setText("no");
        }
    }

    @Override
    public void onClick(View v) {

    }
}
