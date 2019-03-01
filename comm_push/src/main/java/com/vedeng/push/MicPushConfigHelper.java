package com.vedeng.push;

import com.vedeng.comm.base.MicCommonConfig;
import com.vedeng.comm.base.MicCommonConfigHelper;
import com.vedeng.push.module.ProductName;
import com.vedeng.push.module.PushChannel;

import java.util.HashMap;

/**********************************************************
 * @文件名称：CommonConfigurationHelper.java
 * @文件作者：聂中泽
 * @创建时间：2015年9月14日 下午3:42:48
 * @文件描述：公共模块配置项工具类
 * @修改历史：2015年9月14日创建初始版本
 **********************************************************/
public class MicPushConfigHelper extends MicCommonConfigHelper {
    private static final String PRODUCT_NAME_NOT_INIT = "MicPush productName must not null,please init";

    private volatile static MicPushConfigHelper instance = null;

    private HashMap<PushChannel, NotifyActionImpl> pushChannelMap;

    protected MicPushConfigHelper() {

    }

    public static MicPushConfigHelper getInstance() {
        if (instance == null) {
            synchronized (MicPushConfigHelper.class) {
                if (instance == null) {
                    instance = new MicPushConfigHelper();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void init(MicCommonConfig configuration) {
        super.init(configuration);
    }

    public ProductName getProductNameEnum() {
        return ProductName.getValueByTag(super.getProductName());
    }

    public String getVersionCode() {
        return System.getProperty("versionCode");
    }

    public String getProductChannel() {
        return System.getProperty("productChannel", "self");
    }

    public void addPushChannel(PushChannel pushChannel, NotifyActionImpl notifyAction) {
        if (pushChannelMap == null) {
            pushChannelMap = new HashMap<>();
        }
        pushChannelMap.put(pushChannel, notifyAction);
    }

    public HashMap<PushChannel, NotifyActionImpl> getPushChannel() {
        return pushChannelMap;
    }

    public String getPushChannelValue() {
        String value = "";
        if (pushChannelMap == null || pushChannelMap.size() == 0)
            return value;
        if (pushChannelMap.containsKey(PushChannel.TENCENTXG)) {
            value = PushChannel.TENCENTXG.toString();
        } else if (pushChannelMap.containsKey(PushChannel.GCM)) {
            value = PushChannel.GCM.toString();
        }
        return value;
    }

    public String getPushToken() {
        return "";
    }

    @Override
    protected void checkConfiguration() {
        super.checkConfiguration();
        if (getProductNameEnum() == ProductName.UNKNOWN) {
            throw new IllegalStateException(PRODUCT_NAME_NOT_INIT);
        }
    }
}
