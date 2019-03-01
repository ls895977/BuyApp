package com.vedeng.comm.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**********************************************************
 * @文件名称：SharedPreferenceManager.java
 * @文件作者：聂中泽
 * @创建时间：2015年6月12日 上午10:57:09
 * @文件描述：简单数据存储管理器
 * @修改历史：2015年6月12日创建初始版本
 **********************************************************/
public class SharedPreferenceManager {
    private static SharedPreferences sp = null;

    private static SharedPreferenceManager spManager = null;

    private static Editor editor = null;

    private SharedPreferenceManager() {
        sp = MicCommonConfigHelper.getInstance().getContext().getSharedPreferences(
                MicCommonConfigHelper.getInstance().getSharedPreferenceDBName(), Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance() {
        if (spManager == null || sp == null) {
            spManager = new SharedPreferenceManager();
        }
        return spManager;
    }

    public void putInt(String key, int value) {
        if (editor == null)
            editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putString(String key, String value) {
        if (editor == null)
            editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        if (editor == null)
            editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        if (editor == null)
            editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        if (editor == null)
            editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public boolean isKeyExist(String key) {
        return sp.contains(key);
    }

    public void remove(String key) {
        if (editor == null)
            editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

}