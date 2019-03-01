package com.vedeng.httpclient;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/2 11:37
 * @文件描述：请求响应参数枚举
 * @修改历史：2018/5/2 创建初始版本
 **********************************************************/
enum MicResponseParams {
    CODE("code"), MESSAGE("message"), STATUS("status");

    private String value;

    MicResponseParams(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
