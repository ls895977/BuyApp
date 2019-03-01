package com.vedeng.httpclient;

import com.vedeng.comm.base.MicCommonConfig;
import com.vedeng.comm.base.MicCommonConfigHelper;
import com.vedeng.comm.base.utils.Utils;

import okhttp3.Interceptor;

/**********************************************************
 * @文件名称：MicHttpClientConfigHelper.java
 * @文件作者：聂中泽
 * @创建时间：2017/3/13 17:02
 * @文件描述：网络模块配置项工具类
 * @修改历史：2017/3/13 创建初始版本
 **********************************************************/
public class MicHttpClientConfigHelper extends MicCommonConfigHelper {
    private static final String TEST_HOST_NOT_INIT = "MicHttpClient TestMode HOST URL must not null,please init";

    private volatile static MicHttpClientConfigHelper instance = null;
    private Interceptor[] interceptors;
    private AccountAnomalyListener accountAnomalyListener;

    protected MicHttpClientConfigHelper() {

    }

    public static MicHttpClientConfigHelper getInstance() {
        if (instance == null) {
            synchronized (MicHttpClientConfigHelper.class) {
                if (instance == null) {
                    instance = new MicHttpClientConfigHelper();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void init(MicCommonConfig configuration) {
        super.init(configuration);
    }

    public String getReLoginAction() {
        return System.getProperty("reLoginAction");
    }

    public boolean isOpenTestMode() {
        return Boolean.parseBoolean(System.getProperty("isTestMode"));
    }

    public String getTestModeHostUrl() {
        return System.getProperty("testModeHostUrl");
    }

    public void setInterceptors(Interceptor... interceptors) {
        this.interceptors = interceptors;
    }

    public Interceptor[] getInterceptors() {
        return this.interceptors;
    }

    public void setAccountAnomalyListener(AccountAnomalyListener listener) {
        this.accountAnomalyListener = listener;
    }

    public AccountAnomalyListener getAccountAnomalyListener() {
        return accountAnomalyListener;
    }

    protected void checkConfiguration() {
        super.checkConfiguration();
        if (isOpenTestMode() && Utils.isEmpty(getTestModeHostUrl())) {
            throw new IllegalStateException(TEST_HOST_NOT_INIT);
        }
    }
}
