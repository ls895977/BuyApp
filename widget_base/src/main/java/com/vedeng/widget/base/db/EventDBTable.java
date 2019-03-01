package com.vedeng.widget.base.db;

/**********************************************************
 * @文件名称：EventDBTable.java
 * @文件作者：聂中泽
 * @创建时间：2016/9/19 17:09
 * @文件描述：事件数据库表模型
 * @修改历史：2016/9/19 创建初始版本
 **********************************************************/
public class EventDBTable extends DBTable {
    public static final String TABLE_NAME = "event";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_PARAMS = "event_params";

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] toColumns() {
        return new String[]{EVENT_NAME + TEXT_TYPE, EVENT_TYPE + TEXT_TYPE, EVENT_PARAMS + TEXT_TYPE};
    }
}
