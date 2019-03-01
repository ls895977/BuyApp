package com.vedeng.httpclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import okhttp3.Headers;

/**********************************************************
 * @文件名称：DisposeDataListenerImpl.java
 * @文件作者：聂中泽
 * @创建时间：2015年7月29日 下午2:52:48
 * @文件描述：实例化的数据回调监听
 * @修改历史：2015年7月29日创建初始版本
 **********************************************************/
public abstract class DisposeDataListenerImpl {
    private static final String RESPONSE_HANDLE_NOT_INIT = "ResponseHandleListener must not null,please init";
    protected ResponseHandleListenerImpl impl;

    public DisposeDataListenerImpl() {
        impl = new ResponseHandleListenerImpl();
    }

    public DisposeDataListenerImpl(Activity activity) {
        impl = new ResponseHandleListenerImpl(activity);
    }

    private class ResponseHandleListenerImpl implements ResponseHandleListener {
        private Activity activity;

        private ResponseHandleListenerImpl(Activity activity) {
            this.activity = activity;
        }

        private ResponseHandleListenerImpl() {

        }

        @Override
        public void onGetHeaders(Headers headers) {
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onGetHeadersSuccess(headers);
            } else {
                onActivityFinished();
            }
        }

        @Override
        public void onGetSuccess(Object obj) {
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onSuccess(obj);
            } else {
                onActivityFinished();
            }
        }

        @Override
        public void onDataError(String errorCode, String status, String errorMsg) {
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onDataAnomaly(errorCode, status, errorMsg);
            } else {
                onActivityFinished();
            }
        }

        @Override
        public void onNetworkError(String errorMsg) {
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onNetworkAnomaly(errorMsg);
            } else {
                onActivityFinished();
            }
        }

        @Override
        public void onAccountError(String errorCode, String status, String errorMsg) {
            if (null != MicHttpClientConfigHelper.getInstance().getReLoginAction()) {
                // 发送广播通知
                Intent intent = new Intent(MicHttpClientConfigHelper.getInstance().getReLoginAction());
                intent.setComponent(new ComponentName("com.vedeng.goapp", "com.vedeng.httpclient.AccountAnomalyReceiver"));
                intent.putExtra("code", errorCode);
                intent.putExtra("status", status);
                intent.putExtra("reLoginReason", errorMsg.toString());
                MicHttpClientConfigHelper.getInstance().getContext().sendBroadcast(intent);
            }
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onAccountAnomaly(errorCode, status, errorMsg);
            } else {
                onActivityFinished();
            }
        }

        @Override
        public void onServerError(String errorCode, String status, String errorMsg) {
            if (activity == null || (activity != null && !activity.isFinishing())) {
                onServerAnomaly(errorCode, status, errorMsg);
            } else {
                onActivityFinished();
            }
        }
    }

    protected void onGetHeaders(Headers headers) {
        checkResponseHandle();
        impl.onGetHeaders(headers);
    }

    protected void onGetSuccess(Object obj) {
        checkResponseHandle();
        impl.onGetSuccess(obj);
    }

    protected void onDataError(String errorCode, String status, String errorMsg) {
        checkResponseHandle();
        impl.onDataError(errorCode, status, errorMsg);
    }

    protected void onNetworkError(String errorMsg) {
        checkResponseHandle();
        impl.onNetworkError(errorMsg);
    }

    protected void onAccountError(String errorCode, String status, String errorMsg) {
        checkResponseHandle();
        impl.onAccountError(errorCode, status, errorMsg);
    }

    protected void onServerError(String errorCode, String status, String errorMsg) {
        checkResponseHandle();
        impl.onServerError(errorCode, status, errorMsg);
    }

    private void checkResponseHandle() {
        if (impl == null) throw new NullPointerException(RESPONSE_HANDLE_NOT_INIT);
    }

    public void onGetHeadersSuccess(Headers headers) {

    }

    /**
     * 获取到的数据正常
     *
     * @param obj
     */
    public abstract void onSuccess(Object obj);

    /**
     * 网络异常
     *
     * @param errorMsg
     */
    public abstract void onNetworkAnomaly(String errorMsg);

    /**
     * 异常公共方法
     *
     * @param errorCode
     * @param failedMsg
     */
    public void onFailure(String errorCode, String status, String failedMsg) {

    }

    /**
     * 获取到的数据异常
     *
     * @param errorCode
     * @param errorMsg
     */
    public void onDataAnomaly(String errorCode, String status, String errorMsg) {
        onFailure(errorCode, status, errorMsg);
    }

    /**
     * 服务器异常
     *
     * @param errorCode
     * @param errorMsg
     */
    public void onServerAnomaly(String errorCode, String status, String errorMsg) {
        onFailure(errorCode, status, errorMsg);
    }

    /**
     * 账号异常
     *
     * @param errorCode
     * @param errorMsg
     */
    public void onAccountAnomaly(String errorCode, String status, String errorMsg) {
        onFailure(errorCode, status, errorMsg);
    }

    /**
     * 页面已使用并且已被关闭
     */
    public void onActivityFinished() {

    }

    /**
     * 文件上传进度回调
     *
     * @param keyCode       文件上传身份识别标示
     * @param uploadedBytes 已上传的字节
     * @param totalBytes    总字节
     */
    public void onUpLoading(String keyCode, long uploadedBytes, long totalBytes) {

    }
}
