package com.vedeng.widget.base.listener;

/**********************************************************
 * @文件名称：IDownloadListener.java
 * @文件作者：聂中泽
 * @创建时间：2014年5月16日 上午10:19:37
 * @文件描述：下载过程中各种状态的接口
 * @修改历史：2014年5月16日创建初始版本
 **********************************************************/
public interface IDownloadListener {
    /**
     * 开始请求的回调
     */
    void onStarted();

    /**
     * 请求成功，下载前的准备回调
     *
     * @param contentLength 文件长度
     * @param downloadUrl   下载地址
     */
    void onPrepared(long contentLength, String downloadUrl);

    /**
     * 正在下载，更新进度的回调
     *
     * @param progress    当前下载进度
     * @param downloadUrl 下载地址
     */
    void onProgressChanged(int progress, String downloadUrl);

    /**
     * 下载过程中暂停的回调
     *
     * @param completeSize
     * @param downloadUrl
     */
    void onPaused(int progress, int completeSize, String downloadUrl);

    /**
     * 下载完成的回调
     */
    void onFinished(int completeSize, String downloadUrl);

    /**
     * 下载失败的回调
     */
    void onFailure();
}
