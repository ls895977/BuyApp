package com.enuos.jimat.receiver;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/2 14:11
 * @文件描述：
 * @修改历史： 2018/12/2 创建初始版本
 **********************************************************/
public class ExitReceiver extends BroadcastReceiver {
    public ExitReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            } else if (context instanceof Service) {
                ((Service) context).stopSelf();
            }
        }
    }
}
