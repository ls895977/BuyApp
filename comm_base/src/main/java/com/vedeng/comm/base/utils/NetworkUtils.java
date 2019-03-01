package com.vedeng.comm.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;

import com.vedeng.comm.base.MicCommonConfigHelper;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**********************************************************
 * @文件名称：NetworkUtils.java
 * @文件作者：聂中泽
 * @创建时间：2014年8月19日 下午2:50:55
 * @文件描述：网络工具类
 * @修改历史：2014年8月19日创建初始版本
 **********************************************************/
public class NetworkUtils {
    public static final String NET_TYPE_WIFI = "WIFI";
    public static final String NET_TYPE_MOBILE = "MOBILE";
    public static final String NET_TYPE_NO_NETWORK = "no_network";

    private NetworkUtils() {

    }

    public static final String IP_DEFAULT = "0.0.0.0";

    /**
     * 是否有网络
     *
     * @param pContext
     * @return
     */
    public static boolean isConnectInternet(final Context pContext) {
        if (pContext == null)
            return false;
        final ConnectivityManager conManager = (ConnectivityManager) pContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 是否链接wifi
     *
     * @param pContext
     * @return
     */
    public static boolean isConnectWifi(final Context pContext) {
        ConnectivityManager mConnectivity = (ConnectivityManager) pContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        // 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = -1;
        if (info != null) {
            netType = info.getType();
        }
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public static String getNetTypeName(final int pNetType) {
        switch (pNetType) {
            case 0:
                return "unknown";
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA: Either IS95A or IS95B";
            case 5:
                return "EVDO revision 0";
            case 6:
                return "EVDO revision A";
            case 7:
                return "1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDen";
            case 12:
                return "EVDO revision B";
            case 13:
                return "LTE";
            case 14:
                return "eHRPD";
            case 15:
                return "HSPA+";
            default:
                return "unknown";
        }
    }

    /**
     * 获取设备IP地址
     *
     * @return
     */
    public static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (VERSION.SDK_INT <= 10) {
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    } else {
                        if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            }
            return IP_DEFAULT;
        } catch (final SocketException e) {
            return IP_DEFAULT;
        }
    }

    public String getConnTypeName() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MicCommonConfigHelper.getInstance()
                .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NET_TYPE_NO_NETWORK;
        } else {
            return networkInfo.getTypeName();
        }
    }
}
