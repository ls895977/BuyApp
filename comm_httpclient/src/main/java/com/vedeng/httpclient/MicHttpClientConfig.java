package com.vedeng.httpclient;

import com.vedeng.httpclient.cookie.CookiesManager;
import com.vedeng.httpclient.retrofit2.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/1/30 9:43
 * @文件描述：网络请求参数构造器
 * @修改历史：2018/1/30 创建初始版本
 **********************************************************/
public class MicHttpClientConfig {
    private static final long DEFAULT_TIMEOUT = 30;

    private final long connectTimeout;
    private final long readTimeout;
    private final long writeTimeout;
    private final String requestHost;

    public static final String MIC_HOST = "https://app.made-in-china.com/";
//    public static final String MIC_HOST_SSL = "https://app.made-in-china.com/";

    private MicHttpClientConfig(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.requestHost = builder.requestHost;
    }

    public static class Builder {
        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private String requestHost;

        public Builder() {
            MicHttpClientConfigHelper.getInstance().checkConfiguration();
            connectTimeout = DEFAULT_TIMEOUT;
            readTimeout = DEFAULT_TIMEOUT;
            writeTimeout = DEFAULT_TIMEOUT;
            requestHost = MicHttpClientConfigHelper.getInstance().isOpenTestMode() ? MicHttpClientConfigHelper.getInstance().getTestModeHostUrl() : MIC_HOST;
        }

        /**
         * 设置链接请求超时时间，默认单位（秒）
         *
         * @param timeoutSeconds
         */
        public Builder setConnectTimeout(long timeoutSeconds) {
            this.connectTimeout = timeoutSeconds;
            return this;
        }

        /**
         * 设置读取字节流超时时间，默认单位（秒）
         *
         * @param timeoutSeconds
         */
        public Builder setReadTimeout(long timeoutSeconds) {
            this.readTimeout = timeoutSeconds;
            return this;
        }

        /**
         * 设置上传字节流超时时间，默认单位（秒）
         *
         * @param timeoutSeconds
         */
        public Builder setWriteTimeout(long timeoutSeconds) {
            this.writeTimeout = timeoutSeconds;
            return this;
        }

        /**
         * 设置所有超时时间，默认单位（秒）
         *
         * @param timeoutSeconds
         * @return
         */
        public Builder setTimeout(long timeoutSeconds) {
            this.connectTimeout = timeoutSeconds;
            this.readTimeout = timeoutSeconds;
            this.writeTimeout = timeoutSeconds;
            return this;
        }

        /**
         * 设置请求域名
         *
         * @param requestHost
         * @return
         */
        public Builder setRequestHost(String requestHost) {
            this.requestHost = MicHttpClientConfigHelper.getInstance().isOpenTestMode() ? MicHttpClientConfigHelper.getInstance().getTestModeHostUrl() : requestHost;
            return this;
        }

        public Retrofit buildClient() {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor
                    .setLevel(MicHttpClientConfigHelper.getInstance().isEnableLog() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS).retryOnConnectionFailure(true)
                    .readTimeout(readTimeout, TimeUnit.SECONDS).writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor);
            //okHttpClientBuilder.addNetworkInterceptor(new AddCookiesInterceptor());
            //okHttpClientBuilder.addNetworkInterceptor(new ReceivedCookiesInterceptor());
            if (MicHttpClientConfigHelper.getInstance().getInterceptors() != null) {
                for (int i = 0; i < MicHttpClientConfigHelper.getInstance().getInterceptors().length; i++) {
                    okHttpClientBuilder.addInterceptor(MicHttpClientConfigHelper.getInstance().getInterceptors()[i]);
                }
            }
            okHttpClientBuilder.cookieJar(CookiesManager.getInstance());
            OkHttpClient okHttpClient = okHttpClientBuilder.build();
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(requestHost).client(okHttpClient);
            return retrofitBuilder.build();
        }

        public Retrofit buildEmptyClient() {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            OkHttpClient okHttpClient = okHttpClientBuilder.build();
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(requestHost).client(okHttpClient);
            return retrofitBuilder.build();
        }
    }
}
