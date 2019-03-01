package com.enuos.jimat.activity.goods;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayFailActivity extends BaseActivity {

    @BindView(R.id.pay_fail_back)
    ImageView mBack;
    @BindView(R.id.pay_fail_result)
    TextView mPayFailResult;

    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fail);
        ButterKnife.bind(this);

        orderNumber = getIntent().getStringExtra("orderNumber");
        mPayFailResult.setText("For unknown reason, Payment for Order # " + orderNumber
                + " has failed. Please order again or contact to our Customer Service. We apologize for any inconvenience caused.");
    }


    /**
     * 点击事件
     */
    @OnClick({R.id.pay_fail_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.pay_fail_back:
                finish();
                break;
        }
    }

}
