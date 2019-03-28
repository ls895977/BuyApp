package com.enuos.jimat.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.enuos.jimat.model.Model;
import com.example.myvideoplayer.JCVideoPlayer;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;


/**
 * Created by nzz on 2018/6/8.
 * app 初始化
 */
public class MyApplication extends Application {

    private static MyApplication     instance;
    private        SharedPreferences sharedPreferences;

    private static Context mContext;//全局上下文对象

    public static IWXAPI     weiXinApi;
    private       UIProvider _uiProvider;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        // EaseUI Initial
        /*EMOptions optionsA = new EMOptions();
        optionsA.setAcceptInvitationAlways(false);
        optionsA.setAutoTransferMessageAttachments(true);
        optionsA.setAutoDownloadThumbnail(true);
        EMClient.getInstance().init(this, optionsA);
        EMClient.getInstance().setDebugMode(true);*/

        // Model Initial
        Model.getInstance().init(this);

        //  初始化全局上下文对象
        mContext = this;
        JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey("1476181225061390#kefuchannelapp62097");
        options.setTenantId("91392");
        // Kefu EaseUI的初始化
        // Kefu SDK 初始化
        if (ChatClient.getInstance().init(this, options)) {
            _uiProvider = UIProvider.getInstance();
            //初始化EaseUI
            _uiProvider.init(this);
            //调用easeui的api设置providers

        }

        //后面可以设置其他属性
        // Weixin
        weiXinApi = WXAPIFactory.createWXAPI(this, "wxf58d0e7f21e65393", true);
        weiXinApi.registerApp("wxf58d0e7f21e65393");


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    public static Context getGlobalKaKaApplicaotin() {
        return mContext;
    }
}
