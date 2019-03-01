package com.vedeng.httpclient.upload;

import android.os.Handler;
import android.os.Looper;

import com.vedeng.httpclient.DisposeDataListenerImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**********************************************************
 * @文件名称：UploadRequestBody.java
 * @文件作者：聂中泽
 * @创建时间：2016/10/13 11:16
 * @文件描述：自定义带进度回调的文件上传请求体
 * @修改历史：2016/10/13 创建初始版本
 **********************************************************/
public final class UploadRequestBody extends RequestBody {
    /**
     * 请求身份识别标示（更新进度时用来识别请求）
     */
    private final String keyCode;
    /**
     * 实际的待包装请求体
     */
    private RequestBody requestBody;
    /**
     * 进度回调接口
     */
    private DisposeDataListenerImpl listener;
    /**
     * 传入原始文件
     */
    private File file;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    /**
     * 构造函数，赋值
     *
     * @param requestBody 待包装的请求体
     * @param listener    回调接口
     */
    public UploadRequestBody(String keyCode, File file, RequestBody requestBody, DisposeDataListenerImpl listener) {
        this.keyCode = keyCode;
        this.file = file;
        this.requestBody = requestBody;
        this.listener = listener;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写进行写入
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(file);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, contentLength()));

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            listener.onUpLoading(keyCode, mUploaded, mTotal);
        }
    }
}
