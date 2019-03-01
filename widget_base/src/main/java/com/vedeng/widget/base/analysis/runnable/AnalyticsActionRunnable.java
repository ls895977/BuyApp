package com.vedeng.widget.base.analysis.runnable;

import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.module.CollectBehaviorType;

/**********************************************************
 * @文件名称：AnalyticsActionRunnable.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月4日 下午5:00:33
 * @文件描述：行为收集线程
 * @修改历史：2016年2月4日创建初始版本
 **********************************************************/
public class AnalyticsActionRunnable implements Runnable {
    private String eventName;
    private CollectBehaviorType type;
    private String params;

    public AnalyticsActionRunnable(String eventName, CollectBehaviorType type, String params) {
        this.eventName = eventName;
        this.type = type;
        this.params = params;
    }

    @Override
    public void run() {
        synchronized (this) {
            AnalyticsTracker.getInstances().saveEvent(eventName, type, params);
        }
    }

}
