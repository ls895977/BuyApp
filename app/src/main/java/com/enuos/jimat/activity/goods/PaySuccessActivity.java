package com.enuos.jimat.activity.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.order.OrderDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaySuccessActivity extends BaseActivity {

    @BindView(R.id.pay_success_back)
    ImageView mBack;
    @BindView(R.id.pay_success_order_number)
    TextView mPaySuccessOrderNumber;
    @BindView(R.id.pay_success_order_time)
    TextView mPaySuccessOrderTime;
    @BindView(R.id.pay_success_order_price)
    TextView mPaySuccessOrderPrice;

    private String orderNumber, orderTime, orderPrice, orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);

        orderNumber = getIntent().getStringExtra("orderNumber");
        orderTime = getIntent().getStringExtra("orderTime");
        orderPrice = getIntent().getStringExtra("orderPrice");
        orderId = getIntent().getStringExtra("orderId");

        mPaySuccessOrderNumber.setText("Order No: " + orderNumber);
        mPaySuccessOrderTime.setText("Placed on: " + orderTime);
        mPaySuccessOrderPrice.setText("Grand Total: RM " + orderPrice);

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.pay_success_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回 A-B-C  C直接回A
            case R.id.pay_success_back:
                Intent intent = new Intent(mBaseActivity, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

}
