package com.vedeng.widget.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**********************************************************
 * @文件名称：ActivityFinishReceiver.java
 * @文件作者：聂中泽
 * @创建时间：2015年6月12日 上午9:38:19
 * @文件描述：广播监听，用于关闭制定的Activity
 * @修改历史：2015年6月12日创建初始版本
 **********************************************************/
public class ActivityFinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).finish();
        } else if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).finish();
        } else if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

}
