package com.vedeng.widget.base.analysis.runnable;


import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.constant.MobConstants;
import com.vedeng.widget.base.listener.AnalyticsDataListener;
import com.vedeng.widget.base.module.ActionData;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2017/1/9 14:08
 * @文件描述：请求信息数据上传线程
 * @修改历史：2017/1/9 创建初始版本
 **********************************************************/

public class ActionUploadRunnable implements Runnable {

    AnalyticsDataListener listener;
    private ArrayList<ActionData> actionList;
    private boolean isInterrupt = false;

    public ActionUploadRunnable(AnalyticsDataListener listener) {
        this.listener = listener;
    }

    public void interrupt() {
        isInterrupt = true;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                if (isInterrupt)
                    break;
                try {
                    actionList = AnalyticsTracker.getInstances().getActionList();
                    if (!Utils.isEmpty(actionList)) {
                        listener.onGetActionSuccess(actionList);
                    }
                    Thread.sleep(MicBusinessConfigHelper.getInstance().getActionUploadSpaceTime() > 0 ?
                            MicBusinessConfigHelper.getInstance().getActionUploadSpaceTime() : MobConstants.DEFAULT_ANALYTICS_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
