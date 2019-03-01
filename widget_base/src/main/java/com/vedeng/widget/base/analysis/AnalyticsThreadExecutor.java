package com.vedeng.widget.base.analysis;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/6/26 15:38
 * @文件描述：数据采集线程池
 * @修改历史：2018/6/26 创建初始版本
 **********************************************************/
class AnalyticsThreadExecutor {
    private volatile static AnalyticsThreadExecutor instance;
    private ThreadPoolExecutor collectThreadPool;
    private ThreadPoolExecutor uploadThreadPool;

    private AnalyticsThreadExecutor() {
        collectThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        uploadThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    }

    public static AnalyticsThreadExecutor newInstance() {
        if (instance == null) {
            synchronized (AnalyticsThreadExecutor.class) {
                if (instance == null) {
                    instance = new AnalyticsThreadExecutor();
                }
            }
        }
        return instance;
    }

    void submitCollectRunnable(Runnable runnable) {
        Future<?> request = collectThreadPool.submit(runnable);
        new WeakReference<Future<?>>(request);
    }

    void submitUploadRunnable(Runnable runnable) {
        Future<?> request = uploadThreadPool.submit(runnable);
        new WeakReference<Future<?>>(request);
    }

    boolean isUploadThreadFull() {
        return uploadThreadPool.getActiveCount() == uploadThreadPool.getMaximumPoolSize();
    }

    void shutdown() {
        collectThreadPool.shutdown();
        uploadThreadPool.shutdownNow();
    }
}
