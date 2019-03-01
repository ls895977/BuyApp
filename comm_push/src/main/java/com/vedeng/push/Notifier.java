package com.vedeng.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.vedeng.comm.base.MicNotifier;
import com.vedeng.comm.base.NotifyChannel;
import com.vedeng.comm.base.NotifyChannelBuilder;
import com.vedeng.push.module.NotifyLinkGroup;
import com.vedeng.push.module.NotifyLinkType;
import com.vedeng.push.module.ProductName;
import com.vedeng.push.module.PushChannel;

import org.json.JSONException;
import org.json.JSONObject;

/**********************************************************
 * @文件名称：Notifier.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月8日 下午2:24:58
 * @文件描述：通知工具
 * @修改历史：2016年1月8日创建初始版本
 **********************************************************/
class Notifier {
    /**
     * 检查更新下载APK相关通知
     *
     * @param title
     * @param content
     * @param contentView
     * @param channel
     */
    void autoShowUpdateNotify(String title, String content, RemoteViews contentView, PushChannel channel) {
        NotifyActionImpl listener = MicPushConfigHelper.getInstance().getPushChannel().get(channel);
        if (listener != null && listener.isReceiveNotify(MicPushConfigHelper.getInstance().getContext(), NotifyLinkType.UPDATE)) {
            NotifyChannelBuilder.Builder notifyChannelBuilder = new NotifyChannelBuilder.Builder();
            notifyChannelBuilder.setChannel(NotifyChannel.UPDATE);
            PendingIntent intent = listener.onIntentUpdate(MicPushConfigHelper.getInstance().getContext(), "", "");
            if (intent != null) {
                MicNotifier.getInstance().notify(notifyChannelBuilder.build(), R.drawable.ic_notification, R.drawable.ic_launcher,
                        MicPushConfigHelper.getInstance().getContext().getResources().getColor(listener.getNotifierColor()),
                        title, content, contentView, intent, true);
            }
        }
    }

    /**
     * 自动弹出通知
     *
     * @param title
     * @param content
     * @param prod
     * @param link
     * @param param
     * @param mId
     * @param channel
     */
    void autoShowNotify(String title, String content, ProductName prod, NotifyLinkType link, String param,
                        String mId, PushChannel channel) {
        if (TextUtils.isEmpty(param) || TextUtils.isEmpty(mId)
                || MicPushConfigHelper.getInstance().getProductNameEnum() != prod) {
            return;
        }
        NotifyActionImpl listener = MicPushConfigHelper.getInstance().getPushChannel().get(channel);
        if (listener != null && listener.isReceiveNotify(MicPushConfigHelper.getInstance().getContext(), link)) {
            MicNotifier.getInstance().setNotificationSoundEnabled(listener.isNotifySoundEnabled());
            MicNotifier.getInstance().setNotificationVibrateEnabled(listener.isNotifyVibrateEnabled());
            PendingIntent intent = null;
            NotifyChannelBuilder.Builder notifyChannelBuilder = new NotifyChannelBuilder.Builder();
            switch (NotifyLinkGroup.getGroupByLink(link)) {
                case TM:
                    notifyChannelBuilder.setChannel(NotifyChannel.TM);
                    intent = getTMIntent(MicPushConfigHelper.getInstance().getContext(), link, param, mId, listener);
                    break;
                case MAIL:
                    notifyChannelBuilder.setChannel(NotifyChannel.MAIL);
                    intent = getMailIntent(MicPushConfigHelper.getInstance().getContext(), link, param, mId, listener);
                    break;
                case RFQ:
                    notifyChannelBuilder.setChannel(NotifyChannel.RFQ);
                    intent = getRFQIntent(MicPushConfigHelper.getInstance().getContext(), link, param, mId, listener);
                    break;
                case OTHER:
                    notifyChannelBuilder.setChannel(NotifyChannel.OTHER);
                    intent = getOtherIntent(MicPushConfigHelper.getInstance().getContext(), title, content, link, param, mId, listener);
                    break;
                default:
                    break;
            }
            if (intent != null) {
                MicNotifier.getInstance().notify(notifyChannelBuilder.build(), R.drawable.ic_notification, R.drawable.ic_launcher,
                        MicPushConfigHelper.getInstance().getContext().getResources().getColor(listener.getNotifierColor()),
                        title, content, null, intent, false);
            }
        }
    }

    /**
     * 构造TM组相关的Intent
     *
     * @param context
     * @param link
     * @param param
     * @param mId
     * @param listener
     * @return
     */
    private PendingIntent getTMIntent(Context context, NotifyLinkType link, String param, String mId, NotifyActionImpl listener) {
        PendingIntent pendingIntent = null;
        try {
            JSONObject json = new JSONObject(param);
            if (PushUtils.isReallyExit()) {
                pendingIntent = listener.onIntentAppLoading(context, mId, json.getString("cID"), link);
            } else {
                switch (link) {
                    case TM_CHAT:
                        pendingIntent = listener.onIntentTMChat(context, mId, json.getString("uID"), json.getString("cID"),
                                json.getString("cUN"));
                        break;
                    case TM_APPLY_ADDRESS_LIST:
                        pendingIntent = listener.onIntentTMApplyAddressList(context, mId, json.getString("uID"),
                                json.getString("cID"), json.getString("cUN"), json.getLong("time"));
                        break;
                    case TM_AGREE_APPLY_ADDRESS_LIST:
                        pendingIntent = listener.onIntentTMAgreeApplyAddressList(context, mId, json.getString("uID"),
                                json.getString("cID"), json.getString("cUN"), json.getLong("time"));
                        break;
                    case TM_REFUSE_APPLY_ADDRESS_LIST:
                        pendingIntent = listener.onIntentTMRefuseApplyAddressList(context, mId, json.getString("uID"),
                                json.getString("cID"), json.getString("cUN"), json.getLong("time"));
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return pendingIntent;
    }

    /**
     * 构造询盘组相关的Intent
     *
     * @param context
     * @param link
     * @param param
     * @param mId
     * @param listener
     * @return
     */
    private PendingIntent getMailIntent(Context context, NotifyLinkType link, String param, String mId, NotifyActionImpl listener) {
        PendingIntent pendingIntent = null;
        if (PushUtils.isReallyExit()) {
            pendingIntent = listener.onIntentAppLoading(context, mId, param, link);
        } else {
            switch (link) {
                case MAIL_LIST:
                    pendingIntent = listener.onIntentMailList(context, mId, param);
                    break;
                case MAIL_DETAIL:
                    pendingIntent = listener.onIntentMailDetail(context, mId, param);
                    break;
            }
        }
        return pendingIntent;
    }

    /**
     * 构造RFQ组相关的Intent
     *
     * @param context
     * @param link
     * @param param
     * @param mId
     * @param listener
     * @return
     */
    private PendingIntent getRFQIntent(Context context, NotifyLinkType link, String param, String mId, NotifyActionImpl listener) {
        PendingIntent pendingIntent = null;
        if (PushUtils.isReallyExit()) {
            pendingIntent = listener.onIntentAppLoading(context, mId, param, link);
        } else {
            switch (link) {
                case RFQ_DETAIL:
                    pendingIntent = listener.onIntentRfqDetail(context, mId, param);
                    break;
                case RFQ_QUOTATION:
                    pendingIntent = listener.onIntentRfqQuotation(context, mId, param);
                    break;
                case PURCHASE_LIST:
                    pendingIntent = listener.onIntentPurchaseList(context, mId, param);
                    break;
                case QUOTATION_LIST:
                    pendingIntent = listener.onIntentQuotationList(context, mId, param);
                    break;
                case RFQ_REEDIT:
                    pendingIntent = listener.onIntentRfqReedit(context, mId, param);
                    break;
            }
        }
        return pendingIntent;
    }

    /**
     * 构造其他组相关的Intent
     *
     * @param context
     * @param title
     * @param content
     * @param link
     * @param param
     * @param mId
     * @param listener
     * @return
     */
    private PendingIntent getOtherIntent(Context context, String title, String content, NotifyLinkType link, String param, String mId, NotifyActionImpl listener) {
        PendingIntent pendingIntent = null;
        if (PushUtils.isReallyExit()) {
            pendingIntent = listener.onIntentAppLoading(context, mId, param, link);
        } else {
            switch (link) {
                case HOME:
                    pendingIntent = listener.onIntentHome(context, mId, param);
                    break;
                case DISCOVERY:
                    pendingIntent = listener.onIntentDiscovery(context, mId, param);
                    break;
                case WEB_URL:
                    pendingIntent = listener.onIntentWebUrl(context, mId, param);
                    break;
                case UPDATE:
                    pendingIntent = listener.onIntentUpdate(context, mId, param);
                    break;
                case SPECIAL_DETAIL:
                    pendingIntent = listener.onIntentSpecialDetail(context, mId, param);
                    break;
                case NOTIFY_LIST:
                    pendingIntent = listener.onIntentNotifyList(context, mId, param);
                    break;
                case MESSAGE_NOTIFICATION_DETAIL:
                    pendingIntent = listener.onIntentNotificationDetail(context, mId, title, content);
                    break;
                case ORDER_SERVICE_CHANGED:
                case ORDER_SERVICE_OPEN:
                case ORDER_ADVERTISEMENT_OPEN:
                    pendingIntent = listener.onIntentAdvanceList(context, mId, param);
                    break;
                case ORDER_SERVICE_STOP:
                case ORDER_AUTHENTICATION_SERVICE_EXPIRED:
                case ORDER_GOLD_MEMBER_EXPIRED_BEFORE_30_DAYS:
                case ORDER_SEND_CERTIFICATION_REPORT:
                case ORDER_SEND_MEDAL:
                case SHOW_UPGRADE_ORDER_RECEIVED:
                case SHOW_UPGRADE_SHOOT_NOTIFY:
                case SHOW_UPGRADE_SHOOT_BEGIN:
                case SHOW_UPGRADE_SHOOT_SERVICE_FINISH:
                    pendingIntent = listener.onIntentMessageList(context, mId, param);
                    break;
                case ORDER_VIEW_FILE_CHECK_LIST:
                case ORDER_VIEW_FILE_CHECK_LIST2:
                    pendingIntent = listener.onIntentFileCheckList(context, mId, param);
                    break;
                case ORDER_VIEW_CONFIRM_FILE:
                    pendingIntent = listener.onIntentConfirmFile(context, mId, param);
                    break;
                case ORDER_VIEW_CERTIFICATION_REPORT:
                    pendingIntent = listener.onIntentCertificationReport(context, mId, param);
                    break;
                case CONFERENCE_LIST:
                    pendingIntent = listener.onIntentConferenceList(context, mId, param);
                    break;
                case APPLIED_CONFERENCE_LIST:
                    pendingIntent = listener.onIntentAppliedConferenceList(context, mId, param);
                    break;
                case ASK_BAR_QUESTION_DETAIL:
                    pendingIntent = listener.onIntentQuestionDetail(context, mId, param);
                    break;
                case RECORD_ORDER_DETAIL:
                    pendingIntent = listener.onIntentOrderDetail(context, mId, param);
                    break;
                case RECORD_ORDER_LIST:
                    pendingIntent = listener.onIntentOrderList(context, mId, param);
            }
        }
        return pendingIntent;
    }

    /**
     * 清除所有通知
     */
    public void removeAllNotify() {
        NotificationManager manager = (NotificationManager) MicPushConfigHelper.getInstance().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
    }
}
