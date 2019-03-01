package com.vedeng.widget.base.listener;

import com.vedeng.widget.base.module.ActionData;
import com.vedeng.widget.base.module.Event;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：AnalyticsDataListener.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月4日 下午4:39:50
 * @文件描述：行为收集数据处理回调
 * @修改历史：2016年2月4日创建初始版本
 **********************************************************/
public interface AnalyticsDataListener {
    void onGetDataSuccess(ArrayList<Event> events, String eventsJson);

    void onGetActionSuccess(ArrayList<ActionData> actionList);
}
