package com.vedeng.comm.base.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.vedeng.comm.base.MicCommonConfigHelper;
import com.vedeng.comm.base.SharedPreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**********************************************************
 * @文件名称：MobileUtil.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月25日 下午4:35:51
 * @文件描述：获取手机设备参数工具类
 * @修改历史：2016年1月25日创建初始版本
 **********************************************************/
public class MobileUtils {
    private MobileUtils() {

    }

    /***
     * 获得手机设备的串号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // Get deviceId
        String deviceId = tm.getDeviceId();
        // If running on an emulator
        if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
            deviceId = (new StringBuilder("EMU")).append((new Random(System.currentTimeMillis())).nextLong())
                    .toString();
        }
        return deviceId;
    }

    public static String getDeviceId() {
        String deviceId = SharedPreferenceManager.getInstance().getString("DEVICE_ID", "");
        if (Utils.isEmpty(deviceId)) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) MicCommonConfigHelper.getInstance().
                        getContext().getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = telephonyManager.getDeviceId();
            } catch (Exception e) {
            }
            // Get deviceId
            // LogUtils.d(LOGTAG, "deviceId=" + deviceId);
            // If running on an emulator
            if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
                deviceId = (new StringBuilder("EMU")).append(UUID.randomUUID().toString()).toString();
            }
            SharedPreferenceManager.getInstance().putString("DEVICE_ID", deviceId);
        }
        return deviceId;
    }

    /**
     * 获得SIM卡的唯一识别号
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsiCode = tm.getSubscriberId();
            return imsiCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /***
     * 获得手机的电话号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断是否安装了sdcard
     *
     * @return
     */
    public static boolean isHaveSDcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
    }

    /**
     * 获取本地文件目录路径
     *
     * @param dirName 目录名
     * @return
     */
    public static String getExternalStoragePath(String dirName) {
        String dirPath = "/focustech/" + MicCommonConfigHelper.getInstance().getProductName() + "/" + dirName + "/";
        if (MobileUtils.isHaveSDcard()) {
            return Environment.getExternalStorageDirectory() + dirPath;
        }
        return MicCommonConfigHelper.getInstance().getContext().getFilesDir() + dirPath;
    }

    /**
     * 是否启用文件共享器
     *
     * @return
     */
    public static boolean isNeedFileProvider() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !TextUtils.isEmpty(MicCommonConfigHelper.getInstance().getFileProviderAuthName());
    }

    /**
     * 获得手机内存大小的信息RAM大小
     */
    public static String getTotalMemory() {
        ArrayList<String> infos = new ArrayList<String>();
        String str1 = "/proc/meminfo";
        String str2 = "";
        FileReader fr = null;
        BufferedReader localBufferedReader = null;
        try {
            fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                String[] strs = str2.split(":");
                infos.add(strs[1].trim());
            }
            return (infos.size() > 0 ? infos.get(0).trim() : "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获得手机版本信息[0] 表示内核版本 ,[1] 表示android 版本,[2] 型号 ,[3] 版本号
     *
     * @return
     */
    public static String[] getVersion() {
        String[] version =
                {"null", "null", "null", "null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;
        try {
            localFileReader = new FileReader(str1);
            localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0] = arrayOfString[2];// KernelVersion
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
                if (localFileReader != null) {
                    localFileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        version[1] = Build.VERSION.RELEASE;// firmware version
        version[2] = Build.MODEL;// model
        version[3] = Build.DISPLAY;// system version
        return version;
    }

    public static String getDevicePosition(Context context) {
        return context.getSharedPreferences("devicePosition", 0).getString("devicePosition", "其他");
    }

    /**
     * 获取设备唯一序列号
     *
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /***
     * 获得设备的唯一哈希值
     *
     * @param context
     * @return
     */
    public static String getDeviceUniqueid(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static String getProcessName(Context context) {
        String processName = "";
        if (context == null) return processName;
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    processName = appProcess.processName;
                    break;
                }
            }
        }
        return processName;
    }

    public static void startInstallApk(String filePath) {
        MicCommonConfigHelper.getInstance().getContext().startActivity(getInstallApkIntent(filePath));
    }

    public static Intent getInstallApkIntent(String filePath) {
        File apkFile = new File(filePath);
        if (!apkFile.exists()) return new Intent();
        Uri data;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 判断版本大于等于7.0
        if (isNeedFileProvider()) {
            data = FileProvider.getUriForFile(
                    MicCommonConfigHelper.getInstance().getContext(),
                    MicCommonConfigHelper.getInstance().getFileProviderAuthName(), apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        return intent;
    }
}
