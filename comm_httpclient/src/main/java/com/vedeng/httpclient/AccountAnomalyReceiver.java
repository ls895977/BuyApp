package com.vedeng.httpclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vedeng.httpclient.modle.HttpResponseCodeDefine;

/**********************************************************
 * @文件名称：AccountAnomalyReceiver.java
 * @文件作者：聂中泽
 * @创建时间：2016/12/28 10:54
 * @文件描述：账号异常监听
 * @修改历史：2016/12/28 创建初始版本
 **********************************************************/
public class AccountAnomalyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MicHttpClientConfigHelper.getInstance().getReLoginAction())) {
            AccountAnomalyListener listener = MicHttpClientConfigHelper.getInstance().getAccountAnomalyListener();
            if (listener != null) {
                String status = intent.getStringExtra("status");
                String reason = intent.getStringExtra("reLoginReason");
                switch (HttpResponseCodeDefine.getValueByTag(status)) {
                    case COOKIE_OVERTIME:
                        listener.onCookieOvertime(reason);
                        break;
                }
            }
        }
    }
}
