package com.vedeng.comm.base;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/3 16:15
 * @文件描述：通知类型（SDK26以上需要）
 * @修改历史：2018/5/3 创建初始版本
 **********************************************************/
public enum NotifyChannel {
    TM("NotifyChannel_TM"),
    MAIL("NotifyChannel_MAIL"),
    RFQ("NotifyChannel_RFQ"),
    OTHER("NotifyChannel_OTHER"),
    UPDATE("NotifyChannel_Update");

    private String value;

    NotifyChannel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
