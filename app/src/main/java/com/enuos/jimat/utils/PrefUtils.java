package com.enuos.jimat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference工具类
 * config配置文件信息
 **/
public class PrefUtils {

	private static final String PREF_NAME = "config";
	private static SharedPreferences sp;

	public static boolean getBoolean(Context ctx, String key, boolean defaultValue) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defaultValue);
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		if(sp == null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).apply();
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		if(sp == null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return sp.getString(key, defaultValue);
	}

	public static void setString(Context ctx, String key, String value) {
		if(sp == null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).apply();
	}

	public static void clear(Context ctx) {
		if(sp == null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().clear().apply();
	}
}
