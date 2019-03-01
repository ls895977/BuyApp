package com.vedeng.widget.base.analysis.runnable;

import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.module.Event;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：AnalyticsDeleteRunnable.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月5日 上午9:57:20
 * @文件描述：上传行为成功后删除数据库的线程
 * @修改历史：2016年2月5日创建初始版本
 **********************************************************/
public class AnalyticsDeleteRunnable implements Runnable {
    private ArrayList<Event> events;

    public AnalyticsDeleteRunnable(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!Utils.isEmpty(events)) {
                AnalyticsTracker.getInstances().deleteEvents(events);
            }
        }
    }

}
