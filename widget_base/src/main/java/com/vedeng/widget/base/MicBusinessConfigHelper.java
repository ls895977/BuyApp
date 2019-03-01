package com.vedeng.widget.base;

import com.vedeng.comm.base.MicCommonConfig;
import com.vedeng.comm.base.MicCommonConfigHelper;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.constant.MobConstants;
import com.vedeng.widget.base.listener.CrashListener;

/**********************************************************
 * @文件名称：MicBusinessConfigHelper.java
 * @文件作者：聂中泽
 * @创建时间：2015年9月14日 下午3:42:48
 * @文件描述：公共模块配置项工具类
 * @修改历史：2015年9月14日创建初始版本
 **********************************************************/
public class MicBusinessConfigHelper extends MicCommonConfigHelper {
    private static final String PRODUCT_NAME_NOT_INIT = "FocusCommon productName must not null,please init";
    private static final String TEST_HOST_NOT_INIT = "FocusCommon TestMode HOST URL must not null,please init";

    private volatile static MicBusinessConfigHelper instance = null;

    private CrashListener crashListener;

    protected MicBusinessConfigHelper() {

    }

    public static MicBusinessConfigHelper getInstance() {
        if (instance == null) {
            synchronized (MicBusinessConfigHelper.class) {
                if (instance == null) {
                    instance = new MicBusinessConfigHelper();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void init(MicCommonConfig configuration) {
        super.init(configuration);
    }

    public String getProductChannel() {
        return System.getProperty("productChannel", "self");
    }

    public String getProductName() {
        return super.getProductName();
    }

    public String getVersionCode() {
        return System.getProperty("versionCode");
    }

    public void setCrashListener(CrashListener crashListener) {
        this.crashListener = crashListener;
    }

    public boolean isAutoSaveBundle() {
        return Boolean.parseBoolean(System.getProperty("isAutoSaveBundle"));
    }

    public boolean isEnableCrashHandler() {
        return Boolean.parseBoolean(System.getProperty("isEnableCrashHandler"));
    }

    public String getFinishAction() {
        return System.getProperty("finishAction");
    }

    public CrashListener getCrashHandlerListener() {
        return crashListener;
    }

    public boolean isOpenTestMode() {
        return Boolean.parseBoolean(System.getProperty("isTestMode"));
    }

    public String getTestModeHostUrl() {
        return System.getProperty("testModeHostUrl");
    }

    public boolean isUploadAnalyticsData() {
        return Boolean.parseBoolean(System.getProperty("isUploadAnalyticsData", "true"));
    }

    public int getAnalyticsUploadCountLimit() {
        return Integer.parseInt(System.getProperty("analyticsUploadCountLimit", String.valueOf(MobConstants.ANALYTICS_UPLOAD_COUNT_LIMIT)));
    }

    public int getAnalyticsUploadSpaceTime() {
        return Integer.parseInt(System.getProperty("analyticsUploadSpaceTime", String.valueOf(MobConstants.DEFAULT_ANALYTICS_TIME)));
    }

    public int getActionUploadSpaceTime() {
        return Integer.parseInt(System.getProperty("actionUploadSpaceTime", String.valueOf(MobConstants.DEFAULT_ANALYTICS_TIME)));
    }

    public int getSQLiteDBVersionCode() {
        return Integer.parseInt(System.getProperty("sqliteDBVersionCode", MobConstants.DEFAULT_SQLITE_DB_VERSION_CODE));
    }

    @Override
    protected void checkConfiguration() {
        super.checkConfiguration();
        if (Utils.isEmpty(getProductName())) {
            throw new IllegalStateException(PRODUCT_NAME_NOT_INIT);
        }
        if (isOpenTestMode() && Utils.isEmpty(getTestModeHostUrl())) {
            throw new IllegalStateException(TEST_HOST_NOT_INIT);
        }
    }
}
