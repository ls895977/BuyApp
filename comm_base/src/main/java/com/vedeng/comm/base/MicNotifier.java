package com.vedeng.comm.base;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.vedeng.comm.base.utils.LogUtils;

import java.util.Random;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/23 14:09
 * @文件描述：通知构造器
 * @修改历史：2018/5/23 创建初始版本
 **********************************************************/
public class MicNotifier {
    private static final String TAG = MicNotifier.class.getSimpleName();

    private static final Random random = new Random(System.currentTimeMillis());
    private static volatile MicNotifier instance = null;

    protected Context context;

    private NotificationManager notificationManager;
    private boolean isNotificationEnabled = true;
    private boolean isNotificationToastEnabled = false;
    private boolean isNotificationSoundEnabled = true;
    private boolean isNotificationVibrateEnabled = true;
    /**
     * 固定的通知ID
     */
    private int STICKY_NOTIFY_ID = -1;

    private MicNotifier() {
        this.context = MicCommonConfigHelper.getInstance().getContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static MicNotifier getInstance() {
        if (instance == null) {
            synchronized (MicNotifier.class) {
                if (instance == null) {
                    instance = new MicNotifier();
                }
            }
        }
        return instance;
    }

    public void resetNotifyStickyId() {
        STICKY_NOTIFY_ID = -1;
    }

    /**
     * 设置推送是否接收
     *
     * @param isNotificationEnabled
     */
    public void setNotificationEnabled(boolean isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }

    /**
     * 设置是否弹出提示
     *
     * @param isNotificationToastEnabled
     */
    public void setNotificationToastEnabled(boolean isNotificationToastEnabled) {
        this.isNotificationToastEnabled = isNotificationToastEnabled;
    }

    /**
     * 设置推送是否有声音
     *
     * @param isNotificationSoundEnabled
     */
    public void setNotificationSoundEnabled(boolean isNotificationSoundEnabled) {
        this.isNotificationSoundEnabled = isNotificationSoundEnabled;
    }

    /**
     * 设置推送是否震动
     *
     * @param isNotificationVibrateEnabled
     */
    public void setNotificationVibrateEnabled(boolean isNotificationVibrateEnabled) {
        this.isNotificationVibrateEnabled = isNotificationVibrateEnabled;
    }

    /**
     * 构造通知栏消息体
     *
     * @param notifyChannelBuilder
     * @param smallIcon
     * @param title
     * @param body
     * @param pendingIntent
     */
    public void notify(NotifyChannelBuilder notifyChannelBuilder, int smallIcon,
                       String title, String body, PendingIntent pendingIntent) {
        notify(notifyChannelBuilder, smallIcon, -1, -1, title, body, null, pendingIntent, false);
    }

    /**
     * 构造通知栏消息体
     *
     * @param notifyChannelBuilder
     * @param smallIcon
     * @param largeIcon
     * @param title
     * @param body
     * @param pendingIntent
     */
    public void notify(NotifyChannelBuilder notifyChannelBuilder, int smallIcon, int largeIcon,
                       String title, String body, PendingIntent pendingIntent) {
        notify(notifyChannelBuilder, smallIcon, largeIcon, -1, title, body, null, pendingIntent, false);
    }


    /**
     * 构造通知栏消息体
     *
     * @param notifyChannelBuilder
     * @param smallIcon
     * @param largeIcon
     * @param color
     * @param title
     * @param body
     * @param contentView
     * @param pendingIntent
     */
    public void notify(NotifyChannelBuilder notifyChannelBuilder, int smallIcon, int largeIcon,
                       int color, String title, String body, RemoteViews contentView,
                       PendingIntent pendingIntent, boolean isStickyNotifyId) {
        if (isNotificationEnabled) {
            // Show the toast
            if (isNotificationToastEnabled) {
                Toast.makeText(context, body, Toast.LENGTH_LONG).show();
            }
            NotificationCompat.Builder notification = getNotify(notifyChannelBuilder, smallIcon, largeIcon,
                    color, title, body, contentView, pendingIntent);
            if (notification != null) {
                if (isStickyNotifyId) {
                    notifyByStickyId(notification.build());
                } else {
                    notify(notification.build());
                }
            }
        } else {
            LogUtils.w(TAG, "Notifications disabled.");
        }
    }

    private NotificationCompat.Builder getNotify(NotifyChannelBuilder notifyChannelBuilder, int smallIcon, int largeIcon,
                                                 int color, String title, String body, RemoteViews contentView,
                                                 PendingIntent pendingIntent) {
        NotificationCompat.Builder notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = buildNotify_26(notifyChannelBuilder, smallIcon, largeIcon,
                    color, title, body, contentView, pendingIntent);
        } else {
            notification = buildNotify(notifyChannelBuilder, smallIcon, largeIcon,
                    color, title, body, contentView, pendingIntent);
        }
        return notification;
    }

    private NotificationCompat.Builder buildNotify(NotifyChannelBuilder notifyChannelBuilder, int smallIcon, int largeIcon,
                                                   int color, String title, String body, RemoteViews contentView,
                                                   PendingIntent pendingIntent) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(smallIcon);
        if (largeIcon != -1) {
            notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && color != -1) {
            notification.setColor(color);
        }
        if (contentView != null) {
            notification.setContent(contentView);
        }
        notification.setContentTitle(title);
        notification.setContentText(body);
        int defaultsValue = Notification.DEFAULT_LIGHTS;
        //检查更新类的通知不启用任何效果，静默通知（如果开启由于通知机制问题，会不停的发出声音或震动，体验不友好）
        if (notifyChannelBuilder.getChannel() != NotifyChannel.UPDATE) {
            if (isNotificationSoundEnabled) {
                defaultsValue |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled) {
                defaultsValue |= Notification.DEFAULT_VIBRATE;
            }
        }
        notification.setDefaults(defaultsValue);
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker(body);
        notification.setContentIntent(pendingIntent);
        return notification;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private NotificationCompat.Builder buildNotify_26(NotifyChannelBuilder notifyChannelBuilder, int smallIcon, int largeIcon,
                                                      int color, String title, String body, RemoteViews contentView,
                                                      PendingIntent pendingIntent) {
        if (notifyChannelBuilder == null || notifyChannelBuilder.getChannel() == null || notifyChannelBuilder.getChannelResId() == -1)
            return null;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        //检查更新类的通知不启用任何效果，静默通知（如果开启由于通知机制问题，会不停的发出声音或震动，体验不友好）
        if (notifyChannelBuilder.getChannel() == NotifyChannel.UPDATE) {
            importance = NotificationManager.IMPORTANCE_LOW;
        }
        NotificationChannel channel = new NotificationChannel(notifyChannelBuilder.getChannel().toString(),
                context.getString(notifyChannelBuilder.getChannelResId()), importance);
        channel.enableLights(true);
        if (notifyChannelBuilder.getChannel() == NotifyChannel.UPDATE) {
            channel.setSound(null, null);
            channel.enableVibration(false);
        } else {
            if (!isNotificationSoundEnabled) {
                channel.setSound(null, null);
            }
            if (isNotificationVibrateEnabled) {
                channel.enableVibration(true);
            }
        }
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,
                notifyChannelBuilder.getChannel().toString());
        notification.setSmallIcon(smallIcon);
        if (largeIcon != -1) {
            notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && color != -1) {
            notification.setColor(color);
        }
        if (contentView != null) {
            notification.setContent(contentView);
        }
        notification.setContentTitle(title);
        notification.setContentText(body);
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setTicker(body);
        notification.setContentIntent(pendingIntent);
        return notification;
    }

    private void notifyByStickyId(Notification notification) {
        if (STICKY_NOTIFY_ID == -1) {
            STICKY_NOTIFY_ID = random.nextInt();
            LogUtils.d(TAG, String.valueOf(STICKY_NOTIFY_ID));
        }
        notificationManager.notify(STICKY_NOTIFY_ID, notification);
    }

    private void notify(Notification notification) {
        notificationManager.notify(random.nextInt(), notification);
    }
}
