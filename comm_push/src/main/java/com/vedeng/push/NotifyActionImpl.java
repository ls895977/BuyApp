package com.vedeng.push;

import android.app.PendingIntent;
import android.content.Context;

import com.vedeng.push.module.NotifyLinkType;


/**********************************************************
 * @文件名称：NotifyActionImpl.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月20日 下午3:03:49
 * @文件描述：所有回调的实例化
 * @修改历史：2016年1月20日创建初始版本
 **********************************************************/
public abstract class NotifyActionImpl implements NotifyActionListener {
    @Override
    public PendingIntent onIntentHome(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentDiscovery(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentMailList(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentMailDetail(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentRfqDetail(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentRfqQuotation(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentPurchaseList(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentWebUrl(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentUpdate(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentSpecialDetail(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentNotifyList(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentQuotationList(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentRfqReedit(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentTMChat(Context context, String id, String userId, String chatId, String chatName) {
        return null;
    }

    @Override
    public PendingIntent onIntentTMApplyAddressList(Context context, String id, String userId, String chatId,
                                                    String chatName, long time) {
        return null;
    }

    @Override
    public PendingIntent onIntentTMAgreeApplyAddressList(Context context, String id, String userId, String chatId,
                                                         String chatName, long time) {
        return null;
    }

    @Override
    public PendingIntent onIntentTMRefuseApplyAddressList(Context context, String id, String userId, String chatId,
                                                          String chatName, long time) {
        return null;
    }

    @Override
    public PendingIntent onIntentNotificationDetail(Context context, String id, String title, String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isNotifySoundEnabled() {
        return false;
    }

    @Override
    public boolean isNotifyVibrateEnabled() {
        return false;
    }

    @Override
    public abstract boolean isReceiveNotify(Context context, NotifyLinkType link);

    @Override
    public abstract int getNotifierColor();

    @Override
    public abstract PendingIntent onIntentAppLoading(Context context, String id, String param, NotifyLinkType link);

    @Override
    public PendingIntent onIntentAdvanceList(Context context, String id, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentMessageList(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentFileCheckList(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentConfirmFile(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentCertificationReport(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentConferenceList(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentAppliedConferenceList(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentQuestionDetail(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentOrderDetail(Context context, String mId, String param) {
        return null;
    }

    @Override
    public PendingIntent onIntentOrderList(Context context, String mId, String param) {
        return null;
    }
}
