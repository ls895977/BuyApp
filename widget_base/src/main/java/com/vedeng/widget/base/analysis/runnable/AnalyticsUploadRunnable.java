package com.vedeng.widget.base.analysis.runnable;

import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.analysis.AnalyticsManager;
import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.constant.MobConstants;
import com.vedeng.widget.base.listener.AnalyticsDataListener;
import com.vedeng.widget.base.module.Event;

import org.json.JSONException;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：AnalyticsUploadRunnable.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月4日 下午4:44:03
 * @文件描述：行为上传线程
 * @修改历史：2016年2月4日创建初始版本
 **********************************************************/
public class AnalyticsUploadRunnable implements Runnable {
    private AnalyticsDataListener listener;
    private ArrayList<Event> events;
    private String eventsJson;
    private boolean isInterrupt = false;

    public AnalyticsUploadRunnable(AnalyticsDataListener listener) {
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
                    // 从数据库中获得event数据
                    events = AnalyticsTracker.getInstances().getEventsData();
                    if (!Utils.isEmpty(events)) {
                        eventsJson = AnalyticsManager.getInstance().changeEventDataToJson(events);
                        listener.onGetDataSuccess(events, eventsJson);
                    }
                    Thread.sleep(MicBusinessConfigHelper.getInstance().getAnalyticsUploadSpaceTime() > 0 ?
                            MicBusinessConfigHelper.getInstance().getAnalyticsUploadSpaceTime() : MobConstants.DEFAULT_ANALYTICS_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
