package com.vedeng.widget.base.update;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.vedeng.comm.base.utils.FileUtils;
import com.vedeng.comm.base.utils.MobileUtils;
import com.vedeng.comm.base.utils.ToastUtils;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.R;
import com.vedeng.widget.base.constant.MobConstants;
import com.vedeng.widget.base.db.MobDBManager;
import com.vedeng.widget.base.listener.IDownloadListener;
import com.vedeng.widget.base.module.Download;
import com.vedeng.widget.base.module.DownloadApkContent;
import com.vedeng.widget.base.view.dialog.CommonDialog;
import com.vedeng.widget.base.view.dialog.CommonTwoBtnDialog;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**********************************************************
 * @文件名称：UpdateManager.java
 * @文件作者：聂中泽
 * @创建时间：2014年10月24日 上午10:14:55
 * @文件描述：检查更新管理类
 * @修改历史：2014年10月24日创建初始版本
 **********************************************************/
public class UpdateManager {
    private static volatile UpdateManager manager;
    private ThreadPoolExecutor threadPool;
    private UpdateDownloadRunnable downloadRunnable;
    private String currentDownloadUrl;
    private String currentApkFilePath;

    private UpdateManager() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static UpdateManager getInstance() {
        if (manager == null) {
            synchronized (UpdateManager.class) {
                if (manager == null) {
                    manager = new UpdateManager();
                }
            }
        }
        return manager;
    }

    public void doDownloadApk(DownloadApkContent downloadApkContent) {
        Intent serviceIntent = new Intent(MicBusinessConfigHelper.getInstance().getContext(), UpdateService.class);
        Download download = MobDBManager.getInstance().selectDownload(downloadApkContent.downloadUrl);
        if (download != null) {
            downloadApkContent.startPos = download.startPos + download.completeSize;
        }
        serviceIntent.putExtra(UpdateService.DOWNLOAD_EXTRA, downloadApkContent);
        MicBusinessConfigHelper.getInstance().getContext().startService(serviceIntent);
    }

    public void doInstallApk(final Activity activity, String filePath) {
        if (!FileUtils.isFileExists(filePath)) {
            ToastUtils.showLong(activity, R.string.install_apk_failed);
            return;
        }
        this.currentApkFilePath = filePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {
                MobileUtils.startInstallApk(filePath);
            } else {
                CommonDialog dialog = new CommonTwoBtnDialog(activity);
                dialog.setDialogCancelable(false);
                dialog.setConfirmDialogListener(new CommonDialog.DialogClickListener() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onDialogClick() {
                        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                        //注意这个是8.0新API
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                        activity.startActivityForResult(intent, MobConstants.PERMISSION_REQUEST_INSTALL_APK_CODE);
                    }
                });
                dialog.setCancelDialogListener(new CommonDialog.DialogClickListener() {
                    @Override
                    public void onDialogClick() {
                        ToastUtils.showLong(activity, R.string.install_apk_failed);
                    }
                });
                dialog.buildSimpleDialog(activity.getString(R.string.install_apk_dialog_tips));
            }
        } else {
            MobileUtils.startInstallApk(filePath);
        }
    }

    public void onInstallApkResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == MobConstants.PERMISSION_REQUEST_INSTALL_APK_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (FileUtils.isFileExists(currentApkFilePath)) {
                    MobileUtils.startInstallApk(currentApkFilePath);
                } else {
                    ToastUtils.showLong(activity, R.string.install_apk_failed);
                }
            } else {
                ToastUtils.showLong(activity, R.string.install_apk_failed);
            }
        }
    }

    void startDownload(String downloadUrl, String localFilePath, long contentLength,
                       IDownloadListener downloadListener) {
        if (downloadRunnable != null && downloadRunnable.isDownloading()) {
            return;
        }
        this.currentDownloadUrl = downloadUrl;
        checkLocalFilePath(localFilePath);
        MobDBManager.getInstance().saveDownload(downloadUrl, localFilePath, (int) contentLength);
        downloadRunnable = new UpdateDownloadRunnable(downloadUrl, localFilePath, contentLength, downloadListener);
        Future<?> request = threadPool.submit(downloadRunnable);
        new WeakReference<Future<?>>(request);
    }

    void restartUpdateService() {
        if (Utils.isEmpty(currentDownloadUrl))
            return;
        Download download = MobDBManager.getInstance().selectDownload(currentDownloadUrl);
        if (download != null) {
            Intent serviceIntent = new Intent(MicBusinessConfigHelper.getInstance().getContext(), UpdateService.class);
            DownloadApkContent apkContent = new DownloadApkContent(
                    download.url,
                    download.localFilePath,
                    (long) download.contentLength,
                    download.startPos + download.completeSize
            );
            serviceIntent.putExtra(UpdateService.DOWNLOAD_EXTRA, apkContent);
            MicBusinessConfigHelper.getInstance().getContext().startService(serviceIntent);
        }
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0, localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void pause() {
        if (downloadRunnable != null) {
            downloadRunnable.stopDownloading();
        }
    }

    public boolean isDownloading() {
        return downloadRunnable != null && downloadRunnable.isDownloading();
    }

}
