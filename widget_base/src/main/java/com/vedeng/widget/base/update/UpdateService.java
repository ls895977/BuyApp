package com.vedeng.widget.base.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.MobileUtils;
import com.vedeng.comm.base.utils.ToastUtils;
import com.vedeng.push.UpdateAppNotifier;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.R;
import com.vedeng.widget.base.listener.IDownloadListener;
import com.vedeng.widget.base.module.DownloadApkContent;
import com.vedeng.widget.base.module.InstallApkContent;

import org.greenrobot.eventbus.EventBus;

/**********************************************************
 * @文件名称：UpdateService.java
 * @文件作者：聂中泽
 * @创建时间：2014年10月22日 下午4:56:49
 * @文件描述：检查更新服务
 * @修改历史：2014年10月22日创建初始版本
 **********************************************************/
public class UpdateService extends Service {
    public static String DOWNLOAD_EXTRA = "download_extra";
    private NotificationManager notificationManager;
    private Notification mNotification;

    private String updateFilePath;
    private int startPos = 0;
    private long contentLength;
    private BroadcastReceiver connectivityReceiver;

    public UpdateService() {
        connectivityReceiver = new UpdateNetworkReceiver();
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        updateFilePath = MobileUtils.getExternalStoragePath("update") + "UnknownProduct.apk";
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !intent.hasExtra(DOWNLOAD_EXTRA)) {
            UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_failed_msg), null);
            stopSelf();// 停掉服务自身
            return super.onStartCommand(intent, flags, startId);
        }
        DownloadApkContent downloadApkExtra = intent.getParcelableExtra(DOWNLOAD_EXTRA);
        if (!checkExtras(downloadApkExtra)) {
            UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_failed_msg), null);
            stopSelf();// 停掉服务自身
            return super.onStartCommand(intent, flags, startId);
        }
        if (!TextUtils.isEmpty(downloadApkExtra.filePath)) {
            updateFilePath = downloadApkExtra.filePath;
        } else if (!TextUtils.isEmpty(downloadApkExtra.fileName)) {
            updateFilePath = MobileUtils.getExternalStoragePath("update") + downloadApkExtra.fileName;
        }
        contentLength = downloadApkExtra.contentLength > 0 ? downloadApkExtra.contentLength : 0;
        if (downloadApkExtra.startPos > 0) {
            startPos = downloadApkExtra.startPos;
        }
        UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_start), null);
        startDownload(downloadApkExtra.downloadUrl);
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean checkExtras(DownloadApkContent downloadApkExtra) {
        return !TextUtils.isEmpty(downloadApkExtra.downloadUrl)
                && !(TextUtils.isEmpty(downloadApkExtra.fileName)
                && TextUtils.isEmpty(downloadApkExtra.filePath));
    }

    public void startDownload(final String downloadUrl) {
        UpdateManager.getInstance().startDownload(downloadUrl, updateFilePath, contentLength, new IDownloadListener() {
            @Override
            public void onStarted() {
                LogUtils.e("onStarted", downloadUrl);
                ToastUtils.showLong(MicBusinessConfigHelper.getInstance().getContext(), R.string.download_apk_begin);
            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                LogUtils.e("onProgressChanged", progress + downloadUrl);
                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.update_notification);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    contentView.setTextColor(R.id.name, getResources().getColor(R.color.black));
                }
                contentView.setTextViewText(R.id.name, getString(R.string.app_name));
                contentView.setProgressBar(R.id.progressbar, 100, progress, false);
                UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.downloading_apk), contentView);
            }

            @Override
            public void onPrepared(long contentLength, String downloadUrl) {

            }

            @Override
            public void onPaused(int progress, int completeSize, String downloadUrl) {
                LogUtils.e("onPaused", downloadUrl);
                UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_failed_msg), null);
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                LogUtils.e("onFinished", downloadUrl);
                UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_complete), null);
                UpdateAppNotifier.getInstance().resetNotifyId();
                InstallApkContent apkContent = new InstallApkContent();
                apkContent.apkFilePath = updateFilePath;
                EventBus.getDefault().post(apkContent);
                stopSelf();// 停掉服务自身
            }

            @Override
            public void onFailure() {
                LogUtils.e("onFailure", downloadUrl);
                UpdateAppNotifier.getInstance().manualShowNotify(getString(R.string.download_apk_failed_msg), null);
                stopSelf();// 停掉服务自身
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterReceiver();
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    public void unregisterReceiver() {
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

}
