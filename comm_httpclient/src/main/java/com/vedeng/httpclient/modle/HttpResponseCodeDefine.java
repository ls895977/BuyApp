package com.vedeng.httpclient.modle;

import android.text.TextUtils;

/**********************************************************
 * @文件名称：HttpResponseCodeDefine.java
 * @文件作者：聂中泽
 * @创建时间：2015年11月17日 上午10:28:56
 * @文件描述：Http请求返回Code定义
 * @修改历史：2016年1月26日创建初始版本
 **********************************************************/
public enum HttpResponseCodeDefine {
    /**
     * 未知
     */
    UNKNOWN("-1"),
    /**
     * 请求成功
     */
    SC_OK("0"),
    /**
     * Cookie超时
     */
    COOKIE_OVERTIME("408");

    private String value;

    private HttpResponseCodeDefine(String value) {
        this.value = value;
    }

    public static HttpResponseCodeDefine getValueByTag(String type) {
        if (TextUtils.isEmpty(type)) {
            return UNKNOWN;
        }
        for (HttpResponseCodeDefine target : HttpResponseCodeDefine.values()) {
            if (target.value.equals(type)) {
                return target;
            }
        }
        return UNKNOWN;
    }

    public static String getValue(HttpResponseCodeDefine target) {
        return target.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
