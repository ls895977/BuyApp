package com.vedeng.widget.base.analysis;

import android.text.TextUtils;

import com.vedeng.widget.base.module.Analytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**********************************************************
 * @文件名称：AnalyticsParams.java
 * @文件作者：聂中泽
 * @创建时间：2016/12/15 16:02
 * @文件描述：业务类型的事件模型
 * @修改历史：2016/12/15 创建初始版本
 **********************************************************/
public class AnalyticsParams extends Analytics {
    public HashMap<String, String> eventsParams;

    public static AnalyticsParams parse(String paramsStr) {
        if (TextUtils.isEmpty(paramsStr)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(paramsStr);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        if (jsonObject != null) {
            AnalyticsParams result = new AnalyticsParams();
            try {
                result.deviceId = jsonObject.optString("deviceId");
                result.companyId = jsonObject.optString("companyId");
                result.operationId = jsonObject.optString("operationId");
                JSONArray params = jsonObject.optJSONArray("params");
                if (params != null && params.length() > 0) {
                    result.eventsParams = new HashMap<>(params.length());
                    for (int i = 0; i < params.length(); i++) {
                        JSONObject param = params.getJSONObject(i);
                        Iterator<String> it = param.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            result.eventsParams.put(key, param.getString(key));
                        }
                    }
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
        return null;
    }

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
            if (eventsParams != null && !eventsParams.isEmpty()) {
                JSONArray params = new JSONArray();
                for (String paramKey : eventsParams.keySet()) {
                    JSONObject pair = new JSONObject();
                    pair.put(paramKey, eventsParams.get(paramKey));
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
