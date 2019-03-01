package com.vedeng.widget.base.constant;

/**********************************************************
 * @文件名称：MobConstants.java
 * @文件作者：聂中泽
 * @创建时间：2015年8月5日 下午2:51:59
 * @文件描述：公共模块常量
 * @修改历史：2015年8月5日创建初始版本
 **********************************************************/
public class MobConstants {
    /**
     * 默认渠道
     */
    public static final String DEFAULT_PRODUCT_CHANNEL = "self";
    /**
     * 默认接口版本号
     */
    public static final String DEFAULT_VERSION_CODE = "1";
    /**
     * 平台名称
     */
    public static final String PLATFORM_NAME = "android";
    /**
     * 匹配弹出推送消息的Action
     */
    public static final String ACTION_SHOW_NOTIFICATION = "org.focuscommon.client.SHOW_NOTIFICATION";
    /**
     * 行为上报默认间隔
     */
    public static final long DEFAULT_ANALYTICS_TIME = 180000;

    public static final String DEFAULT_SHARED_PREFERENCE_DB_NAME = "smallDatabase";

    /**
     * 8.0以上请求安装未知来源apk权限的编码（最大不可超过65536，否则闪退）
     */
    public static final int PERMISSION_REQUEST_INSTALL_APK_CODE = 56874;
    /**
     * 行为上报单次最大数量
     */
    public static final int ANALYTICS_UPLOAD_COUNT_LIMIT = 50;
    /**
     * 默认接口版本号
     */
    public static final String DEFAULT_SQLITE_DB_VERSION_CODE = "1";
}
