package com.enuos.jimat.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.home.MainActivity;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(mBaseActivity, MainActivity.class);
//        intent.putExtra("item", "0");
//        startActivity(intent);
//        finish();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏标题栏以及状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉属于View的标题
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(0,2000); // 延迟2秒

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            goToRelativeActivity();
            super.handleMessage(msg);
        }
    };


    /**
     * 跳转到相应的 Activity
     * 1. 如果存在用户信息，跳转到用户首页
     * 3. 如果用户信息不存在，跳转到选择登录界面
     */
    public void goToRelativeActivity() {
        // 取出信息
        /*IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");

        Intent intent;
        if (user != null && !user.userAccount.equals("") ) {
            intent = new Intent(mBaseActivity, MainActivity.class);
            intent.putExtra("item", "0");
        } else {
            intent = new Intent(mBaseActivity, LoginNewActivity.class);
            intent.putExtra("from", "mine");
            intent.putExtra("goodsId", "");
            intent.putExtra("goodsType", "");
            intent.putExtra("homeTime", "0");
        }
        startActivity(intent);
        finish();*/

        Intent intent = new Intent(mBaseActivity, MainActivity.class);
        intent.putExtra("item", "0");
        intent.putExtra("goodsType", "0");
        startActivity(intent);
        finish();
    }

}
