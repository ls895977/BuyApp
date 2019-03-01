package com.enuos.jimat.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineServiceActivity extends BaseActivity {

    @BindView(R.id.mine_service_back)
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_service);
        ButterKnife.bind(this);

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_service_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_service_back:
                finish();
                break;
        }
    }

}
