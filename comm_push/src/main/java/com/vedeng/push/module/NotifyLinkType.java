package com.vedeng.push.module;

import android.text.TextUtils;

/**********************************************************
 * @文件名称：NotifyLinkType.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月20日 上午11:44:41
 * @文件描述：推送消息跳转目标标示
 * @修改历史：2016年1月20日创建初始版本
 **********************************************************/
public enum NotifyLinkType {
    /**
     * 未知目标
     */
    UNKNOWN("-1"),
    /**
     * 跳转到App首页
     */
    HOME("1"),
    /**
     * 跳转到发现频道
     */
    DISCOVERY("2"),
    /**
     * 跳转到询盘列表
     */
    MAIL_LIST("3"),
    /**
     * 跳转到询盘详情
     */
    MAIL_DETAIL("4"),
    /**
     * 跳转到RFQ详情
     */
    RFQ_DETAIL("5"),
    /**
     * 跳转到RFQ中报价类型的列表
     */
    RFQ_QUOTATION("6"),
    /**
     * 跳转到采购列表
     */
    PURCHASE_LIST("7"),
    /**
     * 跳转到App内嵌浏览器
     */
    WEB_URL("8"),
    /**
     * 跳转到App检查更新
     */
    UPDATE("9"),
    /**
     * 跳转到专题详情
     */
    SPECIAL_DETAIL("10"),
    /**
     * 跳转到消息通知列表
     */
    NOTIFY_LIST("11"),
    /**
     * 跳转到报价列表
     */
    QUOTATION_LIST("12"),
    /**
     * 跳转到RFQ编辑
     */
    RFQ_REEDIT("13"),
    /**
     * 跳转到TM聊天
     */
    TM_CHAT("14"),
    /**
     * 跳转到TM申请添加到通讯录
     */
    TM_APPLY_ADDRESS_LIST("15"),
    /**
     * 跳转到TM同意添加到通讯录
     */
    TM_AGREE_APPLY_ADDRESS_LIST("16"),
    /**
     * 跳转到TM拒绝添加到通讯录
     */
    TM_REFUSE_APPLY_ADDRESS_LIST("17"),

    /**
     * 跳转到消息通知详情页
     */
    MESSAGE_NOTIFICATION_DETAIL("18"),

    /**
     * 订单系统成功发送服务变更信
     */
    ORDER_SERVICE_CHANGED("19"),

    /**
     * 自动执行服务开通、订单系统成功发送服务开通通知
     */
    ORDER_SERVICE_OPEN("20"),

    /**
     * 服务暂停/终止通知发送成功
     */
    ORDER_SERVICE_STOP("21"),

    /**
     * 广告服务开通
     */
    ORDER_ADVERTISEMENT_OPEN("22"),

    /**
     * 认证服务逾期提醒
     */
    ORDER_AUTHENTICATION_SERVICE_EXPIRED("23"),

    /**
     * 金牌会员到期前30天提醒
     */
    ORDER_GOLD_MEMBER_EXPIRED_BEFORE_30_DAYS("24"),

    /**
     * 查看文件审核清单(文件审核)
     */
    ORDER_VIEW_FILE_CHECK_LIST("30"),

    /**
     * 查看确认函
     */
    ORDER_VIEW_CONFIRM_FILE("31"),

    /**
     * 查看认证报告.
     */
    ORDER_VIEW_CERTIFICATION_REPORT("32"),

    /**
     * 寄送认证报告.
     */
    ORDER_SEND_CERTIFICATION_REPORT("33"),

    /**
     * 寄送奖牌.
     */
    ORDER_SEND_MEDAL("34"),

    /**
     * 查看文件审核清单(实地审核)
     */
    ORDER_VIEW_FILE_CHECK_LIST2("35"),

    /**
     * 外贸智慧堂会议列表(可报名)
     */
    CONFERENCE_LIST("36"),

    /**
     * 我的活动——已参加会议列表(已报名)
     */
    APPLIED_CONFERENCE_LIST("37"),

    /**
     * 天使问吧——问题详情页
     */
    ASK_BAR_QUESTION_DETAIL("38"),

    /**
     * 39,展示升级服务订单已接收触发,点击进入通知中心->服务通知页面
     */
    SHOW_UPGRADE_ORDER_RECEIVED("39"),
    /**
     * 40:展示升级服务拍摄前一天触发,点击进入通知中心->服务通知页面
     */
    SHOW_UPGRADE_SHOOT_NOTIFY("40"),
    /**
     * 41:展示升级服务拍摄当天触发,点击进入通知中心->服务通知页面
     */
    SHOW_UPGRADE_SHOOT_BEGIN("41"),
    /**
     * 42:展示升级服务拍摄完成触发,点击进入通知中心->服务通知页面
     */
    SHOW_UPGRADE_SHOOT_SERVICE_FINISH("42"),
    /**
     * 合作APP进入订单详情页
     */
    RECORD_ORDER_DETAIL("51"),
    /**
     * 合作APP进入指定日期待拍摄订单列表页
     */
    RECORD_ORDER_LIST("50");


    private String value;

    private NotifyLinkType(String value) {
        this.value = value;
    }

    public static NotifyLinkType getValueByTag(String type) {
        if (TextUtils.isEmpty(type)) {
            return UNKNOWN;
        }
        for (NotifyLinkType target : NotifyLinkType.values()) {
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
