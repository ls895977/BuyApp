package com.vedeng.httpclient;

import android.content.Context;
import android.content.SharedPreferences;

import com.vedeng.comm.base.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * @文件名称：PersistentCookies.java
 * @文件作者：聂中泽
 * @创建时间：2016年4月8日 下午3:05:53
 * @文件描述：Cookies持久化工具
 * @修改历史：2016年4月8日创建初始版本
 **********************************************************/
public class PersistentCookies {
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE = "cookie";
    private static final String COOKIE_SPLIT_TAG = "_@@_";

    /**
     * 保存Cookies
     *
     * @param cookies
     */
    public static void saveCookies(List<String> cookies) {
        if (cookies == null || cookies.size() == 0)
            return;
        final StringBuffer cookieBuffer = new StringBuffer();
        int cookiesSize = cookies.size();
        for (int i = 0; i < cookiesSize; i++) {
            cookieBuffer.append(cookies.get(i));
            if (i != cookiesSize - 1) {
                cookieBuffer.append(COOKIE_SPLIT_TAG);
            }
        }
        SharedPreferences sharedPreferences = MicHttpClientConfigHelper.getInstance().getContext()
                .getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COOKIE, cookieBuffer.toString());
        editor.commit();
    }

    /**
     * 获得持久化的Cookies
     *
     * @return
     */
    public static String getCookies() {
        SharedPreferences sharedPreferences = MicHttpClientConfigHelper.getInstance().getContext()
                .getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(COOKIE, "");
    }

    /**
     * 获取无特殊分隔符Cookie
     *
     * @return
     */
    public static String getCookiesWithoutTag() {
        final String originalCookie = getCookies();
        return originalCookie.replace(COOKIE_SPLIT_TAG, ";");
    }

    /**
     * 获取持久化的Cookie数组
     *
     * @return
     */
    public static ArrayList<String> getCookieList() {
        if (Utils.isEmpty(getCookies())) {
            return new ArrayList<>();
        }
        String[] cookies = getCookies().split(COOKIE_SPLIT_TAG);
        ArrayList<String> cookieList = new ArrayList<>();
        for (int i = 0; i < cookies.length; i++) {
            cookieList.add(cookies[i]);
        }
        return cookieList;
    }

    /**
     * 删除持久化的Cookies
     */
    public static void clearCookies() {
        SharedPreferences sharedPreferences = MicHttpClientConfigHelper.getInstance().getContext()
                .getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(COOKIE);
        editor.commit();
    }

}
