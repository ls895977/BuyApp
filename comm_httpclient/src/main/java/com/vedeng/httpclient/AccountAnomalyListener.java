package com.vedeng.httpclient;

/**********************************************************
 * @文件名称：AccountAnomalyListener.java
 * @文件作者：聂中泽
 * @创建时间：2016/5/17 16:43
 * @文件描述：账号异常情况回调
 * @修改历史：2016/5/17 创建初始版本
 **********************************************************/
public interface AccountAnomalyListener {
    void onCookieOvertime(String reason);

    void onAccountLock(String reason);
}
