package com.vedeng.widget.base.analysis;

import android.text.TextUtils;

import com.vedeng.widget.base.module.Analytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**********************************************************
 * @文件名称：AnalyticsSummary.java
 * @文件作者：聂中泽
 * @创建时间：2016/12/15 15:53
 * @文件描述：统计类型的事件模型
 * @修改历史：2016/12/15 创建初始版本
 **********************************************************/
public class AnalyticsSummary extends Analytics {
    public String serviceTime;
    public HashMap<String, String> eventsSummary;

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        try {
            if (!TextUtils.isEmpty(deviceId)) {
                result.put("deviceId", deviceId);
            }
            if (!TextUtils.isEmpty(companyId)) {
                result.put("companyId", companyId);
            }
            if (!TextUtils.isEmpty(operationId)) {
                result.put("operationId", operationId);
            }
            result.put("serviceTime", serviceTime);
            if (eventsSummary != null && !eventsSummary.isEmpty()) {
                JSONArray params = new JSONArray();
                for (String paramKey : eventsSummary.keySet()) {
                    JSONObject pair = new JSONObject();
                    pair.put("time", paramKey);
                    pair.put("count", eventsSummary.get(paramKey));
                    params.put(pair);
                }
                result.put("params", params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
