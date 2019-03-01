package com.vedeng.widget.base;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vedeng.comm.base.utils.LogUtils;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/10/11 14:01
 * @文件描述：修改所有请求中的UserAgent
 * @修改历史：2018/10/11 创建初始版本
 **********************************************************/
public class EditUserAgentInterceptor implements Interceptor {
    private final String USER_AGENT_TAG = "User-Agent";
    private String userAgent;

    public EditUserAgentInterceptor() {

    }

    public EditUserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String userAgent = getUserAgent();
        LogUtils.v("UserAgent: ", userAgent);
        Request request = chain.request()
                .newBuilder()
                .removeHeader(USER_AGENT_TAG)
                .addHeader(USER_AGENT_TAG, getUserAgent())
                .build();
        return chain.proceed(request);
    }

    private String getUserAgent() {
        if (!TextUtils.isEmpty(userAgent)) {
            return userAgent;
        } else {
            return MicBusinessConfigHelper.getInstance().getProductName() + "/" +
                    getVersionName() + "/" + getVersionCode() +
                    " (Android;" + Build.VERSION.RELEASE + ";" + Build.BRAND + ";" + Build.MODEL + ";" + Locale.getDefault().getLanguage() + ")";
        }
    }

    @Nullable
    private PackageInfo getPackageInfo() {
        PackageInfo pi;
        try {
            PackageManager pm = MicBusinessConfigHelper.getInstance().getContext().getPackageManager();
            pi = pm.getPackageInfo(MicBusinessConfigHelper.getInstance().getContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getVersionName() {
        return getPackageInfo() != null ? getPackageInfo().versionName : "";
    }

    private int getVersionCode() {
        return getPackageInfo() != null ? getPackageInfo().versionCode : 0;
    }
}
