package com.enuos.jimat.utils.toast;

import android.content.Context;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**********************************************************
 * @文件名称：MicCommonConfigHelper.java
 * @文件作者：聂中泽
 * @创建时间：2018/3/13 16:30
 * @文件描述：公共模块配置参数工具
 * @修改历史：2018/3/13 创建初始版本
 **********************************************************/
public class MicCommonConfigHelper {
    private static final String CONFIG_NOT_INIT = "MicCommonConfig must be init in application with configuration before using";
    private static final String CONTEXT_NOT_INIT = "MicCommonConfig Context must not null,please check it";

    private MicCommonConfig config;
    private volatile static MicCommonConfigHelper instance = null;

    protected MicCommonConfigHelper() {

    }

    public static MicCommonConfigHelper getInstance() {
        if (instance == null) {
            synchronized (MicCommonConfigHelper.class) {
                if (instance == null) {
                    instance = new MicCommonConfigHelper();
                }
            }
        }
        return instance;
    }

    public synchronized void init(MicCommonConfig configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        if (this.config == null) {
            this.config = configuration;
        }
        checkConfiguration();
        initProperties();
    }

    private void initProperties() {
        InputStream inputStream = null;

        try {
            inputStream = getContext().getAssets().open(config.propertyFile);
            Properties properties = new Properties();
            properties.load(inputStream);

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            LogUtils.e("initProperties", "configuration:" + config.propertyFile, e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("initProperties", "configuration:" + config.propertyFile, e);
                }
            }
        }
    }

    public Context getContext() {
        return config.context;
    }

    public boolean isEnableLog() {
        return Boolean.parseBoolean(System.getProperty("isEnableLog"));
    }

    public String getProductName() {
        return System.getProperty("productName");
    }

    public String getFileProviderAuthName() {
        return System.getProperty("fileProviderAuthName");
    }

    public String getSharedPreferenceDBName() {
        return System.getProperty("sharedPreferenceDBName");
    }

    protected void checkConfiguration() {
        if (config == null) {
            throw new IllegalStateException(CONFIG_NOT_INIT);
        }
        if (config.context == null) {
            throw new IllegalStateException(CONTEXT_NOT_INIT);
        }
    }
}
