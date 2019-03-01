package com.vedeng.widget.base.analysis;

import android.text.TextUtils;

import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.TimeUtils;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.analysis.runnable.ActionCollectRunnable;
import com.vedeng.widget.base.analysis.runnable.AnalyticsActionRunnable;
import com.vedeng.widget.base.db.DBData;
import com.vedeng.widget.base.db.MobDBManager;
import com.vedeng.widget.base.module.ActionData;
import com.vedeng.widget.base.module.Analytics;
import com.vedeng.widget.base.module.CollectBehaviorType;
import com.vedeng.widget.base.module.Event;

import java.util.ArrayList;
import java.util.Date;

/**********************************************************
 * @文件名称：AnalyticsTracker.java
 * @文件作者：聂中泽
 * @创建时间：2015年6月18日 下午3:17:52
 * @文件描述：行为收集管理类
 * @修改历史：2015年6月18日创建初始版本
 **********************************************************/
public class AnalyticsTracker {
    private static final String TAG = "AnalyticsTracker";
    private static AnalyticsTracker instance;
    private MobDBManager eventDBOperate;

    protected AnalyticsTracker() {
        eventDBOperate = MobDBManager.getInstance();
    }

    public static AnalyticsTracker getInstances() {
        if (instance == null) {
            instance = new AnalyticsTracker();
        }
        return instance;
    }

    public ArrayList<Event> getEventsData() {
        return eventDBOperate.selectEvents();
    }

    public void deleteEvents(ArrayList<Event> events) {
        eventDBOperate.deleteEvent(events);
    }

    public void deleteActionList(ArrayList<ActionData> actionList) {
        eventDBOperate.deleteActionList(actionList);
    }

    public void saveAction(ActionData data) {
        eventDBOperate.saveAction(data);
    }

    public ArrayList<ActionData> getActionList() {
        ArrayList<DBData> result = eventDBOperate.getActionList();
        ArrayList<ActionData> parsed = new ArrayList<>(result.size());
        for (DBData data : result) {
            parsed.add((ActionData) data);
        }
        return parsed;
    }

    /**
     * 收集一个行为
     *
     * @param eventName 行为名称， 例如“登录”
     * @param type      行为类型
     * @param params    行为的附带参数，例如 "用户名"
     */
    public void saveEvent(String eventName, CollectBehaviorType type, String params) {
        Event event = new Event();
        event.eventName = eventName;
        event.eventTime = TimeUtils.toGMTString(new Date());
        if (!Utils.isEmpty(params)) {
            event.params = params;
        }
        event.eventType = type;
        eventDBOperate.saveEvent(event);
    }

    /**
     * 页面行为
     *
     * @param eventName
     * @param userName
     */
    public void trackActivityEvent(String eventName, String userName) {
        trackActivityEvent(eventName, userName, null);
    }

    /**
     * 页面行为
     *
     * @param eventName
     * @param userName
     * @param params
     */
    public void trackActivityEvent(String eventName, String userName, Analytics params) {
        trackEvent(eventName, userName, params, CollectBehaviorType.ACTIVITY);
    }

    /**
     * 点击行为
     *
     * @param eventName
     * @param userName
     */
    public void trackClickEvent(String eventName, String userName) {
        trackClickEvent(eventName, userName, null);
    }

    /**
     * 点击行为
     *
     * @param eventName
     * @param userName
     * @param params
     */
    public void trackClickEvent(String eventName, String userName, Analytics params) {
        trackEvent(eventName, userName, params, CollectBehaviorType.CLICK);
    }

    /**
     * @param eventName
     * @param userName
     * @param params
     * @param type
     */
    private void trackEvent(String eventName, String userName, Analytics params, CollectBehaviorType type) {
        LogUtils.d(TAG, "Event: " + eventName + "\nuserName: " + userName + "\ntype: " + type + "\nparams: " + params);
        if (!TextUtils.isEmpty(userName)) {
            AnalyticsManager.getInstance().setUserName(userName);
        }
        AnalyticsThreadExecutor.newInstance().submitCollectRunnable(new AnalyticsActionRunnable(eventName,
                type, params != null ? params.toString() : null));
    }

    void trackRequestAction(ActionData data) {
        LogUtils.d(TAG, "ActionData: " + data.toJSON().toString());
        AnalyticsThreadExecutor.newInstance().submitCollectRunnable(new ActionCollectRunnable(data));
    }

    public void startUploadAnalyticsData() {
        AnalyticsManager.getInstance().startUploadAnalyticsData();
    }

    public void stopAnalytics() {
        AnalyticsThreadExecutor.newInstance().shutdown();
    }
}
