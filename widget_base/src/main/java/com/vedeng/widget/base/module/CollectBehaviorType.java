package com.vedeng.widget.base.module;

import com.vedeng.comm.base.utils.Utils;

/**********************************************************
 * @文件名称：CollectBehaviorType.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月3日 下午2:17:02
 * @文件描述：行为类型
 * @修改历史：2016年2月3日创建初始版本
 **********************************************************/
public enum CollectBehaviorType {
    /**
     * 未归类
     */
    UNKNOWN("0"),
    /**
     * 点击事件
     */
    CLICK("1"),
    /**
     * 页面事件
     */
    ACTIVITY("2"),
    /**
     * 总结性事件
     */
    SUMMARY("3");

    private String value;

    private CollectBehaviorType(String value) {
        this.value = value;
    }

    public static CollectBehaviorType getValueByTag(String type) {
        if (Utils.isEmpty(type)) {
            return UNKNOWN;
        }
        for (CollectBehaviorType target : CollectBehaviorType.values()) {
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
