package com.vedeng.widget.base.db;

/**********************************************************
 * @文件名称：DBTable.java
 * @文件作者：聂中泽
 * @创建时间：2016/9/19 15:56
 * @文件描述：数据库表基础类
 * @修改历史：2016/9/19 创建初始版本
 **********************************************************/
public abstract class DBTable {
    protected static final String INTEGER_TYPE = " integer";
    protected static final String TEXT_TYPE = " TEXT";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIME = "time";

    public abstract String tableName();

    public abstract String[] toColumns();
}
