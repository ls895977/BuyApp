package com.enuos.jimat.utils.http;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nzz on 2017/7/23.
 * Http 联网工具类
 */
public class HttpUtils {

    private static final CacheControl FORCE_NETWORK = new CacheControl.Builder().noCache().build();
    private static final CacheControl FORCE_CACHE = new CacheControl.Builder()
            .onlyIfCached()
            .maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS)
            .build();

    // 编码问题？？？
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    // 缓存控制
    public static final String TYPE_FORCE_CACHE = "TYPE_FORCE_CACHE";
    public static final String TYPE_FORCE_NETWORK = "TYPE_FORCE_NETWORK";
    public static final String TYPE_CACHE_CONTROL = "TYPE_CACHE_CONTROL";

    /**
     * 此静态方法为一般post方法，因在AsyncTask中调用
     */
    public static Object[] postHttp(Context context, String url, HashMap<String, String> params, String cachetype, int cacheseconds) {

        try {
            // 缓存文件夹
            File cacheFile = new File(context.getExternalCacheDir().toString(), "cache");
            // 缓存大小为50M
            int cacheSize = 50 * 1024 * 1024;
            // 创建缓存对象
            final Cache cache = new Cache(cacheFile, cacheSize);

            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();

            // 中文乱码处理
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet())
                formBodyBuilder.add(entry.getKey(), entry.getValue());
//            RequestBody formBody = formBodyBuilder.build();

            StringBuffer sb = new StringBuffer();
            // 设置表单参数
            for (String key: params.keySet()) {
                sb.append(key + "=" + params.get(key) + "&");
            }
            Log.e("TAG", ""+sb.toString());
            RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, sb.toString());

            CacheControl cacheControl = null;
            if (cachetype.equals(TYPE_CACHE_CONTROL)) {
                cacheControl = new CacheControl.Builder()
                        .maxAge(cacheseconds, TimeUnit.SECONDS).build();
            }
            if (cachetype.equals(TYPE_FORCE_CACHE)) {
                cacheControl = FORCE_CACHE;
            }
            if (cachetype.equals(TYPE_FORCE_NETWORK)) {
                cacheControl = FORCE_NETWORK;
            }

            Request request = new Request.Builder()
                    .cacheControl(cacheControl)
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("errorcode").equals("2000")) {
                return new Object[] {true, "请求成功", jsonObject};
            } else {
                return new Object[] {false, jsonObject.getString("errormsg"), null};
            }
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[] {false, "接口异常", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
//                return new Object[] {false, "无法访问服务器", null};
                return new Object[] {true, "Unable to connect to the network", null};
            } else {
                return new Object[] {false, "Unable to connect to the network", null};
            }
        }
    }

    /**
     * 上传图片
     */
    public static Object[] postHttpJPG(Context context, String url, HashMap<String, String> params, HashMap<String, File> pngs) {

        try {
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : params.entrySet())
                multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
            for (Map.Entry<String, File> entry : pngs.entrySet()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), entry.getValue());
                multipartBody.addFormDataPart(entry.getKey(), entry.getKey() + ".jpg", fileBody);
            }
            RequestBody requestBody = multipartBody.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("errorcode") == 2000) {
                return new Object[] {true, "请求成功", jsonObject};
            } else {
                return new Object[] {false, jsonObject.getString("errormsg"), null};
            }
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[]{false, "JSON解析失败", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
                return new Object[]{false, "无法访问服务器", null};
            } else {
                return new Object[]{false, "当前无法访问网络", null};
            }
        }
    }

    /**
     * 上传图片 LinkedHashMap 用于APP商家端上传商品 套餐服务 图文案例
     */
    public static Object[] postHttpJPGLinked(Context context, String url,  LinkedHashMap<String, String> params, LinkedHashMap<String, File> pngs) {

        try {
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : params.entrySet())
                multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
            for (Map.Entry<String, File> entry : pngs.entrySet()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), entry.getValue());
                multipartBody.addFormDataPart(entry.getKey(), entry.getKey() + ".jpg", fileBody);
            }
            RequestBody requestBody = multipartBody.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("errorcode") == 2000) {
                return new Object[] {true, "请求成功", jsonObject};
            } else {
                return new Object[] {false, jsonObject.getString("errormsg"), null};
            }
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[]{false, "接口异常", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
                return new Object[]{false, "Unable to connect to the network", null};
            } else {
                return new Object[]{false, "Unable to connect to the network", null};
            }
        }
    }

    /**
     * 上传视频 .mp4格式 LinkedHashMap 用于APP商家端上传视频秀
     */
    public static Object[] postHttpVideoLinked(Context context, String url,  LinkedHashMap<String, String> params, LinkedHashMap<String, File> video) {

        try {
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : params.entrySet())
                multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
            for (Map.Entry<String, File> entry : video.entrySet()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("video/mp4"), entry.getValue());
                multipartBody.addFormDataPart(entry.getKey(), entry.getKey() + ".mp4", fileBody);
            }
            RequestBody requestBody = multipartBody.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("errorcode") == 2000) {
                return new Object[] {true, "请求成功", jsonObject};
            } else {
                return new Object[] {false, jsonObject.getString("errormsg"), null};
            }
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[]{false, "JSON解析失败", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
                return new Object[]{false, "无法访问服务器", null};
            } else {
                return new Object[]{false, "当前无法访问网络", null};
            }
        }
    }

}
