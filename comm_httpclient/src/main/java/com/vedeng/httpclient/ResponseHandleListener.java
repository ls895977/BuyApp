package com.vedeng.httpclient;

import okhttp3.Headers;

/**********************************************************
 * @文件名称：ResponseHandleListener.java
 * @文件作者：聂中泽
 * @创建时间：2014年5月16日 下午3:24:37
 * @文件描述：数据处理回调
 * @修改历史：2014年5月16日创建初始版本
 **********************************************************/
interface ResponseHandleListener {
    void onGetHeaders(Headers headers);

    /**
     * 请求成功
     *
     * @param obj
     */
    void onGetSuccess(Object obj);

    /**
     * 数据异常
     *
     * @param errorCode
     * @param errorMsg
     */
    void onDataError(String errorCode, String status, String errorMsg);

    /**
     * 网络异常
     *
     * @param errorMsg
     */
    void onNetworkError(String errorMsg);

    /**
     * 账号异常
     *
     * @param errorCode
     * @param errorMsg
     */
    void onAccountError(String errorCode, String status, String errorMsg);

    /**
     * 服务器异常
     *
     * @param errorCode
     * @param errorMsg
     */
    void onServerError(String errorCode, String status, String errorMsg);
}
