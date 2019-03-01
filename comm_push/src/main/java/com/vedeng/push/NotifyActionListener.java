package com.vedeng.push;

import android.app.PendingIntent;
import android.content.Context;

import com.vedeng.push.module.NotifyLinkType;

/**********************************************************
 * @文件名称：NotifyActionListener.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月20日 下午5:16:09
 * @文件描述：所有通知回调
 * @修改历史：2016年1月20日创建初始版本
 **********************************************************/
public interface NotifyActionListener
{
	/**
	 * 点击通知跳转到App首页
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentHome(Context context, String id, String param);

	/**
	 * 点击通知跳转到发现频道
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentDiscovery(Context context, String id, String param);

	/**
	 * 点击通知跳转到询盘列表
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentMailList(Context context, String id, String param);

	/**
	 * 点击通知跳转到询盘详情
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentMailDetail(Context context, String id, String param);

	/**
	 * 点击通知跳转到RFQ详情
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentRfqDetail(Context context, String id, String param);

	/**
	 * 点击通知跳转到RFQ中报价类型的列表
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentRfqQuotation(Context context, String id, String param);

	/**
	 * 点击通知跳转到采购列表
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentPurchaseList(Context context, String id, String param);

	/**
	 * 点击通知跳转到App内嵌浏览器
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentWebUrl(Context context, String id, String param);

	/**
	 * 点击通知跳转到App检查更新
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentUpdate(Context context, String id, String param);

	/**
	 * 点击通知跳转到专题详情
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentSpecialDetail(Context context, String id, String param);

	/**
	 * 点击通知跳转到消息通知列表
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentNotifyList(Context context, String id, String param);

	/**
	 * 点击通知跳转到报价列表
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentQuotationList(Context context, String id, String param);

	/**
	 * 点击通知跳转到RFQ编辑
	 * @param context
	 * @param id
	 * @param param
	 * @return
	 */
	public PendingIntent onIntentRfqReedit(Context context, String id, String param);

	/**
	 * 点击通知跳转到TM聊天
	 * @param context
	 * @param id
	 * @param userId
	 * @param chatId
	 * @param chatName
	 * @return
	 */
	public PendingIntent onIntentTMChat(Context context, String id, String userId, String chatId, String chatName);

	/**
	 * 点击通知跳转到TM申请添加到通讯录
	 * @param context
	 * @param id
	 * @param userId
	 * @param chatId
	 * @param chatName
	 * @param time
	 * @return
	 */
	public PendingIntent onIntentTMApplyAddressList(Context context, String id, String userId, String chatId,
                                                    String chatName, long time);

	/**
	 * 点击通知跳转到TM同意添加到通讯录
	 * @param context
	 * @param id
	 * @param userId
	 * @param chatId
	 * @param chatName
	 * @param time
	 * @return
	 */
	public PendingIntent onIntentTMAgreeApplyAddressList(Context context, String id, String userId, String chatId,
                                                         String chatName, long time);

	/**
	 * 点击通知跳转到TM拒绝添加到通讯录
	 * @param context
	 * @param id
	 * @param userId
	 * @param chatId
	 * @param chatName
	 * @param time
	 * @return
	 */
	public PendingIntent onIntentTMRefuseApplyAddressList(Context context, String id, String userId, String chatId,
                                                          String chatName, long time);

	/**
	 * 点击通知启动App
	 * @param context
	 * @param id
	 * @param param
	 * @param link
	 * @return
	 */
	public PendingIntent onIntentAppLoading(Context context, String id, String param, NotifyLinkType link);
	
	
	public PendingIntent onIntentNotificationDetail(Context context, String id, String title, String content);

	/**
	 * 是否接收消息提醒
	 * @param context
	 * @param link
	 * @return
	 */
	public boolean isReceiveNotify(Context context, NotifyLinkType link);

	/**
	 * 通知栏背景色
	 * @return
	 */
	public int getNotifierColor();

	/**
	 * 收到推送是否打开声音
	 * @return
	 */
	public boolean isNotifySoundEnabled();

	/**
	 * 收到推送是否打开震动
	 * @return
	 */
	public boolean isNotifyVibrateEnabled();

	/**
	 * 点击通知打开高级服务列表页
	 * @return
	 */
	PendingIntent onIntentAdvanceList(Context context, String id, String param);

	/**
	 * 点击通知打开消息通知列表页
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentMessageList(Context context, String mId, String param);

	/**
	 * 点击通知打开文件审核清单页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentFileCheckList(Context context, String mId, String param);

	/**
	 * 点击通知打开确认函页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentConfirmFile(Context context, String mId, String param);

	/**
	 * 点击通知打开认证报告页面(待确认是下载还是html页面)
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentCertificationReport(Context context, String mId, String param);

	/**
	 * 点击通知打开外贸智慧堂会议列表页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentConferenceList(Context context, String mId, String param);

	/**
	 * 点击通知打开我的活动已参加会议列表页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentAppliedConferenceList(Context context, String mId, String param);

	/**
	 * 点击通知打开天使问吧问题详情页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentQuestionDetail(Context context, String mId, String param);

	/**
	 * 点击通知打开拍摄订单详情页面
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentOrderDetail(Context context, String mId, String param);

	/**
	 * 点击通知打开指定日期的待拍摄订单列表页
	 * @param context
	 * @param mId
	 * @param param
	 * @return
	 */
	PendingIntent onIntentOrderList(Context context, String mId, String param);
}
