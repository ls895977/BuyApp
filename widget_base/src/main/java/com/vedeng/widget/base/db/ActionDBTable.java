package com.vedeng.widget.base.db;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2017/1/9 13:44
 * @文件描述：接口行为数据表
 * @修改历史：2017/1/9 创建初始版本
 **********************************************************/

public class ActionDBTable extends DBTable {

    public static final String TABLE_NAME = "action_info";
    public static final String URL = "url";
    public static final String PARAM = "param";
    public static final String SEND_TIME = "sendTime";
    public static final String RECEIVE_TIME = "receiveTime";
    public static final String CODE = "code";
    public static final String ERR = "err";
    public static final String DEVICE_MODEL = "deviceModel";
    public static final String SYSTEM_OS = "systemOS";
    public static final String NETWORK = "network";


    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] toColumns() {
        return new String[]{URL + TEXT_TYPE,
                PARAM + TEXT_TYPE,
                SEND_TIME + TEXT_TYPE,
                RECEIVE_TIME + TEXT_TYPE,
                CODE + TEXT_TYPE,
                ERR + TEXT_TYPE,
                DEVICE_MODEL + TEXT_TYPE,
                SYSTEM_OS + TEXT_TYPE,
                NETWORK + TEXT_TYPE
        };
    }
}
