package com.vedeng.widget.base.listener;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/10/26 16:22
 * @文件描述：行为收集对外接口
 * @修改历史：2018/10/26 创建初始版本
 **********************************************************/
public interface IAnalyticsListener {
    void activityEvent(String eventName, String... params);

    void clickEvent(String eventName, String... params);
}
