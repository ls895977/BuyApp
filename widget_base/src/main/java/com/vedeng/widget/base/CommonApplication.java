package com.vedeng.widget.base;

import android.app.Application;

import com.vedeng.comm.base.utils.MobileUtils;
import com.vedeng.widget.base.analysis.AnalyticsTracker;

/**********************************************************
 * @文件名称：CommonApplication.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月7日 下午3:25:27
 * @文件描述：公共Application
 * @修改历史：2016年1月7日创建初始版本
 **********************************************************/
public abstract class CommonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initCommonConfig();
        initExceptionHandler();
        initAnalyticsService();
        initPushChannel();
    }

    /**
     * 初始化异常处理对象
     */
    protected void initExceptionHandler() {
        if (MicBusinessConfigHelper.getInstance().isEnableCrashHandler()) {
            MicCrashHandler.newInstance();
        }
    }

    protected boolean isMainProcess() {
        return MobileUtils.getProcessName(MicBusinessConfigHelper.getInstance().getContext()).equals(MicBusinessConfigHelper.getInstance().getContext().getPackageName());
    }

    protected void initAnalyticsService() {
        if (isMainProcess() && MicBusinessConfigHelper.getInstance().isUploadAnalyticsData()) {
            AnalyticsTracker.getInstances().startUploadAnalyticsData();
        }
    }


    /**
     * 初始化公共模块需要的配置
     */
    protected abstract void initCommonConfig();

    /**
     * 初始化推送渠道
     */
    protected abstract void initPushChannel();
}
