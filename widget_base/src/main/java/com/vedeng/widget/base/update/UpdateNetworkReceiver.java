package com.vedeng.widget.base.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vedeng.comm.base.utils.LogUtils;


/**********************************************************
 * @文件名称：UpdateNetworkReceiver.java
 * @文件作者：聂中泽
 * @创建时间：2014年10月28日 下午3:15:31
 * @文件描述：检查更新网络状态监听
 * @修改历史：2014年10月28日创建初始版本
 **********************************************************/
public class UpdateNetworkReceiver extends BroadcastReceiver {
    private static final String LOGTAG = UpdateNetworkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(LOGTAG, "UpdateNetworkReceiver.onReceive()...");
        String action = intent.getAction();
        LogUtils.d(LOGTAG, "action=" + action);

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            LogUtils.d(LOGTAG, "Network Type  = " + networkInfo.getTypeName());
            LogUtils.d(LOGTAG, "Network State = " + networkInfo.getState());
            if (networkInfo.isConnected()) {
                LogUtils.i(LOGTAG, "Network connected");
                UpdateManager.getInstance().restartUpdateService();
            } else {
                LogUtils.e(LOGTAG, "Network unconnected");
                UpdateManager.getInstance().pause();
            }
        } else {
            LogUtils.e(LOGTAG, "Network unavailable");
            UpdateManager.getInstance().pause();
        }
    }

}
