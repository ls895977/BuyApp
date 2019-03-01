package com.vedeng.widget.base.module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.db.DBData;

import org.json.JSONObject;

import java.io.Serializable;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2017/1/6 9:24
 * @文件描述：接口请求信息
 * @修改历史：2017/1/6 创建初始版本
 **********************************************************/

public class ActionData extends DBData implements Serializable {
    private static String NETWORK_NONE = "-1";
    private static String NETWORK_WIFI = "0";
    private static String NETWORK_CellularNetwork = "1";
    private static String NETWORK_4G = "2";
    private static String NETWORK_3G = "3";
    private static String NETWORK_2G = "4";
    public String url;
    public String param;
    public String sendTime;
    public String receiveTime;
    public String code;
    public String err;
    public String deviceModel;
    public String systemOS;
    public String network;

    public ActionData() {
        deviceModel = getDeviceModel();
        systemOS = getSystemOS();
        network = getNetworkType();
    }

    private String getNetworkType() {
        Context context = MicBusinessConfigHelper.getInstance().getContext();
        if (context == null) {
            return NETWORK_NONE;
        }
        String strNetworkType = NETWORK_NONE;
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = NETWORK_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = NETWORK_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = NETWORK_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = NETWORK_4G;
                        break;
                    default:
                        strNetworkType = NETWORK_CellularNetwork;
                        break;
                }

            }
        }
        return strNetworkType;
    }

    private String getSystemOS() {
        return "Android " + Build.VERSION.RELEASE;
    }

    private String getDeviceModel() {
        return Build.MODEL;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        try {
            if (!TextUtils.isEmpty(url)) {
                result.put("url", url);
            }
            if (!TextUtils.isEmpty(sendTime)) {
                result.put("sendTime", sendTime);
            }
            if (!TextUtils.isEmpty(receiveTime)) {
                result.put("receiveTime", receiveTime);
            }
            if (!TextUtils.isEmpty(code)) {
                result.put("code", code);
            }
            if (!TextUtils.isEmpty(err)) {
                result.put("err", err);
            }
            if (!TextUtils.isEmpty(deviceModel)) {
                result.put("deviceModel", deviceModel);
            }
            if (!TextUtils.isEmpty(systemOS)) {
                result.put("systemOS", systemOS);
            }
            if (!TextUtils.isEmpty(network)) {
                result.put("network", network);
            }
            if (!TextUtils.isEmpty(param)) {
                result.put("param", param);
            }
        } catch (Exception e) {
        }
        return result;
    }

}
