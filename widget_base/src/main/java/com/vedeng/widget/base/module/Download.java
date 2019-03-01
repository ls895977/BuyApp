package com.vedeng.widget.base.module;

/**********************************************************
 * @文件名称：Download.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月16日 下午2:53:42
 * @文件描述：下载数据模型
 * @修改历史：2016年2月16日创建初始版本
 **********************************************************/
public class Download {
    /**
     * 下载id
     */
    public String id;
    /**
     * 下载地址
     */
    public String url;
    /**
     * 一次下载完成的字节数
     */
    public int completeSize;
    /**
     * 下载开始位置
     */
    public int startPos;
    /**
     * 下载的文件字节长度
     */
    public int contentLength;
    /**
     * 下载到本地文件路径
     */
    public String localFilePath;
    /**
     * 下载时间
     */
    public String time;
}
