package com.enuos.jimat.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.home.MainActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.model.bean.UserInfo;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMClient;
import com.hyphenate.helpdesk.callback.Callback;

import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class WelcomeActivity extends BaseActivity {
    private String hxCommonAccountHead = "jimataccounts";
    private String hxCommonPswHead = "jimatpassword";

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
        handler.sendEmptyMessageDelayed(0, 2000); // 延迟2秒

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
        if (isLogin()) {//此处判断是我改的
            if (ChatClient.getInstance().isLoggedInBefore() == false) {
                Log.e("aa","----账号---"+hxCommonAccountHead + user.userID+"----psd----"+hxCommonPswHead + user.userID);
                ChatClient.getInstance().login(hxCommonAccountHead + user.userID, hxCommonPswHead + user.userID,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                // 处理模型层数据
                                Model.getInstance().loginSuccess(new UserInfo(hxCommonAccountHead + user.userID));
                                // 保存到本地数据库
                                Model.getInstance().getUserAccountDao().addAccount(new UserInfo(hxCommonAccountHead + user.userID));
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                Log.e("789", "环信登陆成功");
                                Intent intent = new Intent(mBaseActivity, MainActivity.class);
                                intent.putExtra("item", "1");
                                intent.putExtra("goodsType", "1");
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(int code, String error) {
                                Log.e("789", "环信登陆失败" + String.valueOf(code));
                                Log.e("789", "环信登陆失败" + error);
                            }

                            @Override
                            public void onProgress(int progress, String status) {
                            }
                        });
            }
        } else {
            Intent intent = new Intent(mBaseActivity, MainActivity.class);
            intent.putExtra("item", "0");
            intent.putExtra("goodsType", "0");
            startActivity(intent);
            finish();
        }
    }

    /**
     * 判断是否登录
     */
    User user;

    public boolean isLogin() {
        // 取出信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        user = dataStorage.load(User.class, "User");
        if (user != null && !user.userAccount.equals("")) {
            return true;
        }
        return false;
    }

}
