package com.enuos.jimat.utils.toast;

import android.util.Log;

/**********************************************************
 * @文件名称：LogUtil.java
 * @文件作者：聂中泽
 * @创建时间：2017年3月10日 下午4:54:01
 * @文件描述：日志工具类
 * @修改历史：2017年3月10日创建初始版本
 **********************************************************/
public class LogUtils {
    private static final String LOG_TAG = "PullToRefresh";
    private static boolean isOpen = MicCommonConfigHelper.getInstance().isEnableLog();

    private LogUtils() {

    }

    public static void e(String tag, String msg) {
        if (isOpen) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isOpen) {
            Log.e(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isOpen) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isOpen) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isOpen) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isOpen) {
            Log.w(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg) {
        if (isOpen) {
            Log.v(tag, msg);
        }
    }
}
