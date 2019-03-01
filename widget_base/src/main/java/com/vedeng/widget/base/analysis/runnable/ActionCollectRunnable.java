package com.vedeng.widget.base.analysis.runnable;

import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.module.ActionData;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2017/10/31 10:40
 * @文件描述：接口行为收集
 * @修改历史：2017/10/31 创建初始版本
 **********************************************************/
public class ActionCollectRunnable implements Runnable {
    private ActionData actionData;

    public ActionCollectRunnable(ActionData actionData) {
        this.actionData = actionData;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (actionData != null) {
                AnalyticsTracker.getInstances().saveAction(actionData);
            }
        }
    }
}
