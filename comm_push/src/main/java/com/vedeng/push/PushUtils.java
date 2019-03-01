package com.vedeng.push;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**********************************************************
 * @文件名称：PushUtils.java
 * @文件作者：聂中泽
 * @创建时间：2017/3/14 10:47
 * @文件描述：推送模块工具类
 * @修改历史：2017/3/14 创建初始版本
 **********************************************************/
public class PushUtils {
    /**
     * app的UI进程是否在运行
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isAppRunning() {
        boolean isAppRunning = false;
        try {
            ActivityManager am = (ActivityManager) MicPushConfigHelper.getInstance().getContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = MicPushConfigHelper.getInstance().getContext().getPackageName();
            if (am != null) {
                List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
                for (ActivityManager.RunningTaskInfo info : list) {
                    if (info.topActivity.getPackageName().equals(packageName)
                            && info.baseActivity.getPackageName().equals(packageName)) {
                        isAppRunning = true;
                        break;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return isAppRunning;
    }

    /**
     * app是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) MicPushConfigHelper.getInstance().getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = MicPushConfigHelper.getInstance().getContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * app是否退出
     *
     * @return
     */
    public static boolean isReallyExit() {
        return !isAppRunning() && !isAppOnForeground();
    }
}
