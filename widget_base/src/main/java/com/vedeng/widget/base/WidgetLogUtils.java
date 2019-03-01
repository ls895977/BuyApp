package com.vedeng.widget.base;

import com.vedeng.comm.base.utils.LogUtils;

/**********************************************************
 * @文件名称：WidgetLogUtil.java
 * @文件作者：聂中泽
 * @创建时间：2017/3/30 11:39
 * @文件描述：控件日志打印工具类
 * @修改历史：2017/3/30 创建初始版本
 **********************************************************/
public class WidgetLogUtils {
    private static final String LOG_TAG = "PullToRefresh";

    public static void warnDeprecation(String depreacted, String replacement) {
        LogUtils.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
    }
}
