package com.vedeng.httpclient;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/4/28 15:47
 * @文件描述：所有响应数据对象的根模型
 * @修改历史：2018/4/28 创建初始版本
 **********************************************************/
class MicRootResponse<T> {
    String code;
    String message;
    String status;
    T targetObj;
}
