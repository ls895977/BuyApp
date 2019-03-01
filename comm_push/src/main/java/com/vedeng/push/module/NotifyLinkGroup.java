package com.vedeng.push.module;

import static com.vedeng.push.module.NotifyLinkType.*;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/8 11:54
 * @文件描述：推送消息跳转目标组名枚举
 * @修改历史：2018/5/8 创建初始版本
 **********************************************************/
public enum NotifyLinkGroup {
    //未识别的组
    UNKNOWN(
            NotifyLinkType.UNKNOWN
    ),
    //TM组
    TM(
            TM_CHAT, TM_APPLY_ADDRESS_LIST, TM_AGREE_APPLY_ADDRESS_LIST, TM_REFUSE_APPLY_ADDRESS_LIST
    ),
    //询盘相关消息组
    MAIL(
            MAIL_LIST, MAIL_DETAIL
    ),
    //RFQ相关消息组
    RFQ(
            RFQ_DETAIL, RFQ_QUOTATION, PURCHASE_LIST, QUOTATION_LIST, RFQ_REEDIT
    ),
    //服务相关消息组
    OTHER(
            HOME, DISCOVERY, WEB_URL, UPDATE, SPECIAL_DETAIL, NOTIFY_LIST,
            MESSAGE_NOTIFICATION_DETAIL, ORDER_SERVICE_CHANGED, ORDER_SERVICE_OPEN,
            ORDER_SERVICE_STOP, ORDER_ADVERTISEMENT_OPEN, ORDER_AUTHENTICATION_SERVICE_EXPIRED,
            ORDER_GOLD_MEMBER_EXPIRED_BEFORE_30_DAYS, ORDER_VIEW_FILE_CHECK_LIST,
            ORDER_VIEW_CONFIRM_FILE, ORDER_VIEW_CERTIFICATION_REPORT, ORDER_SEND_CERTIFICATION_REPORT,
            ORDER_SEND_MEDAL, ORDER_VIEW_FILE_CHECK_LIST2, CONFERENCE_LIST,
            APPLIED_CONFERENCE_LIST, ASK_BAR_QUESTION_DETAIL, SHOW_UPGRADE_ORDER_RECEIVED,
            SHOW_UPGRADE_SHOOT_NOTIFY, SHOW_UPGRADE_SHOOT_BEGIN, SHOW_UPGRADE_SHOOT_SERVICE_FINISH,
            RECORD_ORDER_DETAIL, RECORD_ORDER_LIST

    );

    private NotifyLinkType[] linkTypes;

    NotifyLinkGroup(NotifyLinkType... linkTypes) {
        this.linkTypes = linkTypes;
    }

    public static NotifyLinkGroup getGroupByLink(NotifyLinkType targetLinkType) {
        for (NotifyLinkGroup target : NotifyLinkGroup.values()) {
            for (NotifyLinkType linkType : target.linkTypes) {
                if (linkType == targetLinkType) {
                    return target;
                }
            }
        }
        return UNKNOWN;
    }
}
