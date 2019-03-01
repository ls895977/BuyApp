package com.vedeng.push.module;

import android.text.TextUtils;

/**********************************************************
 * @文件名称：ProductName.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月26日 下午6:21:07
 * @文件描述：产品名称枚举
 * @修改历史：2016年1月26日创建初始版本
 **********************************************************/
public enum ProductName {
    UNKNOWN("unknown"),
    /**
     * 供应商App
     */
    SUPPLIER("supplier"),
    /**
     * 买家App
     */
    BUYER("buyer"),
    BUYER_OLD("mic"),
    /**
     * 企业定制App
     */
    MEDIA("focusmedia"),
    /**
     * 销售系统App
     */
    SALES("sales"),
    /**
     * 第三方合作系统App
     */
    COOPERATOR("cooperator");

    private String value;

    private ProductName(String value) {
        this.value = value;
    }

    public static ProductName getValueByTag(String type) {
        if (TextUtils.isEmpty(type)) {
            return UNKNOWN;
        }
        for (ProductName target : ProductName.values()) {
            if (target.value.equals(type)) {
                return target;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
