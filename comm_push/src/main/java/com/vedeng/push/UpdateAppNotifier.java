package com.vedeng.push;

import android.widget.RemoteViews;

import com.vedeng.comm.base.MicNotifier;
import com.vedeng.push.module.PushChannel;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/23 9:27
 * @文件描述：更新App相关通知处理
 * @修改历史：2018/5/23 创建初始版本
 **********************************************************/
public class UpdateAppNotifier extends Notifier {
    private static volatile UpdateAppNotifier notifier;
    private final String TAG = "UpdateAppNotifier";

    private UpdateAppNotifier() {

    }

    public static UpdateAppNotifier getInstance() {
        if (notifier == null) {
            synchronized (UpdateAppNotifier.class) {
                if (notifier == null) {
                    notifier = new UpdateAppNotifier();
                }
            }
        }
        return notifier;
    }

    public void manualShowNotify(String content, RemoteViews contentView) {
        autoShowUpdateNotify(MicPushConfigHelper.getInstance().getContext().getString(R.string.app_name),
                content, contentView, PushChannel.TENCENTXG);
    }

    public void resetNotifyId() {
        MicNotifier.getInstance().resetNotifyStickyId();
    }
}
