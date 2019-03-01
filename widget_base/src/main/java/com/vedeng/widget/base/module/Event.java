package com.vedeng.widget.base.module;

/**********************************************************
 * @文件名称：Event.java
 * @文件作者：聂中泽
 * @创建时间：2015年9月14日 下午5:18:35
 * @文件描述：用户行为数据模型
 * @修改历史：2015年9月14日创建初始版本
 **********************************************************/
public class Event {
    /**
     * 表示该事件的id编号，该编号在数据库中是递增的
     */
    public String id;
    /**
     * 事件名称
     */
    public String eventName;
    /**
     * 事件发生的时间
     */
    public String eventTime;
    /***
     * 事件类型
     */
    public CollectBehaviorType eventType;
    /**
     * 获得事件的类型
     */
    public String params;
}
