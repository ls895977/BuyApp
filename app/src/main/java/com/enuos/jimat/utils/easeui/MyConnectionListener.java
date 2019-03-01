package com.enuos.jimat.utils.easeui;

import android.app.Activity;
import android.util.Log;

import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import org.greenrobot.eventbus.EventBus;

import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

/**
 * 实现ConnectionListener接口-----监听网络状态---在另外一台手机登录----账号被移除
 * Created by nzz on 2018/6/22.
 */

public class MyConnectionListener implements ChatClient.ConnectionListener {

    Activity context;

    @Override
    public void onConnected() {
    }

    public  MyConnectionListener(Activity context) {
        this.context = context;
    }

    @Override
    public void onDisconnected(final int error) {
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    ToastUtils.show(context, "Account has been removed");
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    ToastUtils.show(context, "The account is logged in on other devices and forced to go offline");
                    EMClient.getInstance().logout(true, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.e("789", "下线成功了");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("789", "下线失败了！" + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });//下线
//                    Intent intentA = new Intent(context, LoginNewActivity.class);
//                    context.startActivity(intentA);
                    // 退出 APP 本身的账号
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            context.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = new User();
                    user.userAccount = "";
                    user.isLogin = "false";
                    dataStorage.storeOrUpdate(user, "User");
                    EventBus.getDefault().post(EventConfig.EVENT_EXIT);

//                    Intent intent = new Intent(context, LoginNewActivity.class);
//                    intent.putExtra("from", "mine");
//                    intent.putExtra("goodsId", "");
//                    intent.putExtra("goodsType", "");
//                    intent.putExtra("homeTime", "0");
//                    context.startActivity(intent);
                    context.finish();
                } else {
                    if (NetUtils.hasNetwork(context)) {
//                        //连接不到聊天服务器
//                        Toast.makeText(context, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        //当前网络不可用，请检查网络设置
//                        Toast.makeText(context, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
