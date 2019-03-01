package com.vedeng.widget.base.analysis;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.TimeUtils;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.analysis.runnable.ActionUploadRunnable;
import com.vedeng.widget.base.analysis.runnable.AnalyticsUploadRunnable;
import com.vedeng.widget.base.listener.AnalyticsDataListener;
import com.vedeng.widget.base.module.ActionData;
import com.vedeng.widget.base.module.CollectBehaviorType;
import com.vedeng.widget.base.module.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2017/10/31 11:06
 * @文件描述：行为收集逻辑管理
 * @修改历史：2017/10/31 创建初始版本
 **********************************************************/
public class AnalyticsManager implements AnalyticsDataListener {
    private static final int MSG_GET_SERVICE_TIME = 0;
    private static final int MSG_GET_SERVICE_TIME_SUCCESS = 1;
    private static final int MSG_GET_SERVICE_TIME_FAIL = 2;
    private static final int MSG_COLLECT_SUMMARY_EVENT_SUCCESS = 3;
    private static final int MSG_GET_ACTION_SUCCESS = 4;
    private static final String TAG = "AnalyticsTracker";
    private ArrayList<Event> events;
    private String eventsJson;
    private String userName;

    private static AnalyticsManager instance;

    private AnalyticsManager() {

    }

    public static AnalyticsManager getInstance() {
        if (instance == null) {
            instance = new AnalyticsManager();
        }
        return instance;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    void startUploadAnalyticsData() {
        if (!AnalyticsThreadExecutor.newInstance().isUploadThreadFull()) {
            AnalyticsThreadExecutor.newInstance().submitUploadRunnable(new AnalyticsUploadRunnable(this));
            AnalyticsThreadExecutor.newInstance().submitUploadRunnable(new ActionUploadRunnable(this));
        }
    }

    /**
     * 这是一个静态方法，该方法可以将事件对象解析成json表示的数据
     *
     * @param events 一系列的事件对象
     * @return 返回一个json数据
     * @throws JSONException
     */
    public String changeEventDataToJson(ArrayList<Event> events) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eventName", event.eventName);
            jsonObject.put("time", event.eventTime);
            jsonObject.put("param", Utils.isEmpty(event.params) ? "" : event.params);
            jsonObject.put("actType", event.eventType.toString());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    private String getEventTimeByDay(String eventTime) {
        Date date = TimeUtils.transformGMTtoDate(eventTime);
        if (date != null) {
            return TimeUtils.toGMTString(TimeUtils.getStartTimeOfOneDay(date));
        }
        return null;
    }

    @Override
    public void onGetDataSuccess(ArrayList<Event> events, String eventsJson) {
        if (!Utils.isEmpty(events) && !Utils.isEmpty(eventsJson)) {
            this.events = events;
            this.eventsJson = eventsJson;
            handler.sendEmptyMessage(MSG_GET_SERVICE_TIME);
        }
    }

    @Override
    public void onGetActionSuccess(ArrayList<ActionData> actionList) {
        if (!Utils.isEmpty(actionList)) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_GET_ACTION_SUCCESS;
            msg.obj = actionList;
            handler.sendMessage(msg);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_SERVICE_TIME:
                    getServiceTime();
                    break;
                case MSG_GET_SERVICE_TIME_SUCCESS:
                case MSG_GET_SERVICE_TIME_FAIL:
                    collectSummaryEvent(msg.obj == null ? null : (String) msg.obj);
                    break;
                case MSG_COLLECT_SUMMARY_EVENT_SUCCESS:
                    /*MobRequestCenter.sendUserCollect(eventsJson, userName, new DisposeDataListenerImpl() {
                        @Override
                        public void onSuccess(Object obj) {
                            AnalyticsThreadExecutor.newInstance().submitCollectRunnable(new AnalyticsDeleteRunnable(events));
                        }

                        @Override
                        public void onNetworkAnomaly(String errorMsg) {

                        }
                    });*/
                    break;
                case MSG_GET_ACTION_SUCCESS:
                    collectActionData((ArrayList<ActionData>) msg.obj);
                    break;
            }

        }

    };

    private HashMap<String, String> parseCountMap(HashMap<String, Integer> counts) {
        HashMap<String, String> result = new HashMap<>(counts.size());
        for (String key : counts.keySet()) {
            result.put(key, String.valueOf(counts.get(key).intValue()));
        }
        return result;
    }

    private void collectActionData(final ArrayList<ActionData> actionList) {
        JSONArray data = new JSONArray();
        if (actionList != null) {
            for (ActionData action : actionList) {
                data.put(action.toJSON());
            }
        }
        /*MobRequestCenter.sendActionData(data.toString(), new DisposeDataListenerImpl() {
            @Override
            public void onSuccess(Object obj) {
                AnalyticsThreadExecutor.newInstance().submitCollectRunnable(new ActionDeleteRunnable(actionList));
            }

            @Override
            public void onNetworkAnomaly(String errorMsg) {

            }
        });*/
    }

    private void getServiceTime() {
        /*MobRequestCenter.getServiceTime(new DisposeDataListenerImpl() {
            @Override
            public void onSuccess(Object obj) {
                ServiceTime data = (ServiceTime) obj;
                handler.sendMessage(handler.obtainMessage(MSG_GET_SERVICE_TIME_SUCCESS, data.content));
            }

            @Override
            public void onFailure(String errorCode, String failedMsg) {
                handler.sendEmptyMessage(MSG_GET_SERVICE_TIME_FAIL);
            }

            @Override
            public void onNetworkAnomaly(String errorMsg) {
                onFailure(HttpResponseCodeDefine.UNKNOWN.toString(), errorMsg);
            }
        });*/
    }

    private void collectSummaryEvent(String serviceTime) {
        AnalyticsSummary summary = new AnalyticsSummary();
        HashMap<String, Integer> counts = new HashMap<>();
        if (!Utils.isEmpty(events) && !Utils.isEmpty(eventsJson)) {
            boolean first = true;
            for (Event event : events) {
                if (first) {
                    AnalyticsParams analyticsParams = AnalyticsParams.parse(event.params);
                    if (analyticsParams != null) {
                        first = false;
                        summary.deviceId = analyticsParams.deviceId;
                        summary.companyId = analyticsParams.companyId;
                        summary.operationId = analyticsParams.operationId;
                    }
                }
                String time = AnalyticsManager.getInstance().getEventTimeByDay(event.eventTime);
                if (!TextUtils.isEmpty(time)) {
                    int count = counts.get(time) == null ? 0 : counts.get(time);
                    counts.put(time, ++count);
                }
            }

            summary.eventsSummary = parseCountMap(counts);
            if (!TextUtils.isEmpty(serviceTime)) {
                summary.serviceTime = serviceTime;
            }
            Event event = new Event();
            event.eventName = "";
            event.eventTime = TimeUtils.toGMTString(new Date());
            event.eventType = CollectBehaviorType.SUMMARY;
            event.params = summary.toString();
            ArrayList<Event> eventList = new ArrayList<>();
            eventList.add(event);

            LogUtils.d(TAG, "Event: " + event.eventName + "\nuserName: " + userName + "\ntype: " + event.eventType + "\nparams: " + event.params);

            /*try {
                MobRequestCenter.sendUserCollect(AnalyticsManager.getInstance().changeEventDataToJson(eventList),
                        userName, new DisposeDataListenerImpl() {
                            @Override
                            public void onSuccess(Object obj) {
                                handler.sendEmptyMessage(MSG_COLLECT_SUMMARY_EVENT_SUCCESS);
                            }

                            @Override
                            public void onNetworkAnomaly(String errorMsg) {

                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }
}
