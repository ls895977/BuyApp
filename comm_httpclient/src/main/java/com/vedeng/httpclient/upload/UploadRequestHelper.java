package com.vedeng.httpclient.upload;


import com.vedeng.httpclient.DisposeDataListenerImpl;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**********************************************************
 * @文件名称：UploadRequestHelper.java
 * @文件作者：聂中泽
 * @创建时间：2016年4月11日 上午11:44:01
 * @文件描述：文件上传时的接口请求值封装
 * @修改历史：2016年4月11日创建初始版本
 **********************************************************/
public class UploadRequestHelper {
    /**
     * 构建文件上传字符串参数请求体
     *
     * @param fieldValue
     * @return
     */
    public static RequestBody create(String fieldValue) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), fieldValue);
    }

    /**
     * 构建文件上传文件参数请求体
     *
     * @param keyCode   请求身份识别标示（更新进度时用来识别请求）
     * @param paramKey  请求的参数名称
     * @param fileValue 文件
     * @param listener  回调监听
     * @return
     */
    public static MultipartBody.Part create(String keyCode, String paramKey, File fileValue, DisposeDataListenerImpl listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileValue);
        UploadRequestBody body = new UploadRequestBody(keyCode, fileValue, requestBody, listener);
        return MultipartBody.Part.createFormData(paramKey, fileValue.getName(), body);
    }

}
