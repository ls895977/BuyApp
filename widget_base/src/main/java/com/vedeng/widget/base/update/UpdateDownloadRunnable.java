package com.vedeng.widget.base.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.db.MobDBManager;
import com.vedeng.widget.base.listener.IDownloadListener;
import com.vedeng.widget.base.module.Download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**********************************************************
 * @文件名称：UpdateDownloadRunnable.java
 * @文件作者：聂中泽
 * @创建时间：2014年10月28日 下午3:13:08
 * @文件描述：检查更新下载安装包线程
 * @修改历史：2014年10月28日创建初始版本
 **********************************************************/
public class UpdateDownloadRunnable implements Runnable {
    private int startPos = 0;
    private String downloadUrl;
    private String localFilePath;
    private IDownloadListener downloadListener;
    private DownloadResponseHandler downloadHandler;
    private boolean isDownloading = false;
    private long contentLength;

    public UpdateDownloadRunnable(String downloadUrl, String localFilePath, long contentLength,
                                  IDownloadListener downloadListener) {
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.downloadListener = downloadListener;
        this.contentLength = contentLength;
        downloadHandler = new DownloadResponseHandler();
        isDownloading = true;
    }

    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                Download download = MobDBManager.getInstance().selectDownload(downloadUrl);
                if (download == null) {
                    startPos = 0;
                } else {
                    startPos = download.startPos + download.completeSize;
                }
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                // 设置范围，格式为Range：bytes x-y;
                connection.setRequestProperty("Range", "bytes=" + startPos + "-");
                connection.setRequestProperty("Connection", "Keep-Alive");
                if (!Thread.currentThread().isInterrupted()) {
                    if (downloadHandler != null) {
                        downloadHandler.sendResponseMessage(connection.getInputStream());
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            if (downloadHandler != null) {
                downloadHandler.sendFailureMessage();
            }
        } catch (InterruptedException e) {
            if (downloadHandler != null) {
                downloadHandler.sendFailureMessage();
            }
        }
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void stopDownloading() {
        isDownloading = false;
    }

    public class DownloadResponseHandler {
        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILURE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        private Handler handler;

        public DownloadResponseHandler() {
            if (Looper.myLooper() != null) {
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        handleSelfMessage(msg);
                    }
                };
            }
        }

        private void sendPausedMessage() {
            sendMessage(obtainMessage(PAUSED_MESSAGE, null));
        }

        void sendProgressChangedMessage(int progress) {
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[]
                    {progress}));
        }

        void sendStartMessage() {
            sendMessage(obtainMessage(START_MESSAGE, null));
        }

        void sendFailureMessage() {
            sendMessage(obtainMessage(FAILURE_MESSAGE, null));
        }

        void sendFinishMessage() {
            // 延迟0.5秒发送下载完成的消息（实时发送的话通知栏存在无法更新的情况）
            sendMessageDelayed(obtainMessage(FINISH_MESSAGE, null), 500);
        }

        //
        // Pre-processing of messages (in original calling thread, typically the UI thread)
        //
        void handlePausedMessage() {
            downloadListener.onPaused(progress, mCompleteSize, downloadUrl);
        }

        void handleProgressChangedMessage(int progress) {
            downloadListener.onProgressChanged(progress, downloadUrl);
        }

        void handleFailureMessage() {
            downloadListener.onFailure();
        }

        void handleStartMessage() {
            downloadListener.onStarted();
        }

        void handleFinishMessage() {
            downloadListener.onFinished(mCompleteSize, downloadUrl);
        }

        // Methods which emulate android's Handler and Message methods
        void handleSelfMessage(Message msg) {
            switch (msg.what) {
                case START_MESSAGE:
                    handleStartMessage();
                    break;
                case FAILURE_MESSAGE:
                    handleFailureMessage();
                    break;
                case PROGRESS_CHANGED:
                    Object[] response = (Object[]) msg.obj;
                    handleProgressChangedMessage((Integer) response[0]);
                    break;
                case PAUSED_MESSAGE:
                    handlePausedMessage();
                    break;
                case FINISH_MESSAGE:
                    handleFinishMessage();
                    break;
            }
        }

        void sendMessage(Message msg) {
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                handleSelfMessage(msg);
            }
        }

        void sendMessageDelayed(Message msg, long delayMillis) {
            if (handler != null) {
                handler.sendMessageDelayed(msg, delayMillis);
            } else {
                handleSelfMessage(msg);
            }
        }

        Message obtainMessage(int responseMessage, Object response) {
            Message msg = null;
            if (handler != null) {
                msg = this.handler.obtainMessage(responseMessage, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        private int mCompleteSize = 0;
        private int progress = 0;
        private static final int PROGRESS_CHANGED = 5;
        private static final int PAUSED_MESSAGE = 7;

        void sendResponseMessage(InputStream is) {
            RandomAccessFile randomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[4096];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                randomAccessFile.seek(startPos);
                boolean isPaused = false;
                sendStartMessage();
                while ((length = is.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, length);
                        mCompleteSize += length;
                        MobDBManager.getInstance().updateDownloadProgress(downloadUrl, startPos, mCompleteSize);
                        if ((startPos + mCompleteSize) <= contentLength) {
                            progress = (int) (Float.parseFloat(Utils
                                    .getTwoPointFloatStr((float) (startPos + mCompleteSize) / (contentLength))) * 100);
                            if (limit % 30 == 0 || progress == 100) {
                                sendProgressChangedMessage(progress);
                            }
                        }
                        limit++;
                    } else {
                        isPaused = true;
                        sendPausedMessage();
                        break;
                    }
                }
                stopDownloading();
                if (!isPaused) {
                    MobDBManager.getInstance().deleteDownload(downloadUrl);
                    sendFinishMessage();
                }
            } catch (IOException e) {
                sendPausedMessage();
                stopDownloading();
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    stopDownloading();
                    e.printStackTrace();
                }
            }
        }
    }
}
