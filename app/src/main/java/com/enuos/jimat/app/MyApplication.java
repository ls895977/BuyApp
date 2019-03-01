package com.enuos.jimat.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.enuos.jimat.model.Model;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by nzz on 2018/6/8.
 * app 初始化
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private SharedPreferences sharedPreferences;

    private static Context mContext;//全局上下文对象

    public static IWXAPI weiXinApi;

    public static MyApplication getInstance(){
        if(instance==null){
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

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
        mContext=this;

        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey("1476181225061390#kefuchannelapp62097");
        options.setTenantId("91392");

        // Kefu SDK 初始化
        if (!ChatClient.getInstance().init(this, options)){
            return;
        }
        // Kefu EaseUI的初始化
        UIProvider.getInstance().init(this);
        //后面可以设置其他属性

        // Weixin
        weiXinApi = WXAPIFactory.createWXAPI(this, "wxf58d0e7f21e65393", true);
        weiXinApi.registerApp("wxf58d0e7f21e65393");
    }

    public static Context getGlobalKaKaApplicaotin()
    {
        return mContext;
    }



}
