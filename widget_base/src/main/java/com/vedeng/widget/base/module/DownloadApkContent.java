package com.vedeng.widget.base.module;

import android.os.Parcel;
import android.os.Parcelable;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/21 14:39
 * @文件描述：启动下载Apk服务需要的参数模型
 * @修改历史：2018/5/21 创建初始版本
 **********************************************************/
public class DownloadApkContent implements Parcelable {
    /**
     * apk下载地址
     */
    public String downloadUrl;
    /**
     * apk文件名称（eg:MIC_for_Buyer_V4.00.00.apk）
     */
    public String fileName;
    /**
     * 文件全路径，非第一次下载，断点续传用
     */
    public String filePath;
    /**
     * apk大小
     */
    public long contentLength;
    /**
     * 初始下载字节位置，非第一次下载，断点续传用
     */
    public int startPos;

    private DownloadApkContent() {

    }

    public DownloadApkContent(String downloadUrl, String fileName, long contentLength) {
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        this.contentLength = contentLength;
    }

    public DownloadApkContent(String downloadUrl, String filePath, long contentLength, int startPos) {
        this.downloadUrl = downloadUrl;
        this.filePath = filePath;
        this.contentLength = contentLength;
        this.startPos = startPos;
    }

    protected DownloadApkContent(Parcel in) {
        downloadUrl = in.readString();
        fileName = in.readString();
        filePath = in.readString();
        contentLength = in.readLong();
        startPos = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(downloadUrl);
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeLong(contentLength);
        dest.writeInt(startPos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadApkContent> CREATOR = new Creator<DownloadApkContent>() {
        @Override
        public DownloadApkContent createFromParcel(Parcel in) {
            return new DownloadApkContent(in);
        }

        @Override
        public DownloadApkContent[] newArray(int size) {
            return new DownloadApkContent[size];
        }
    };
}
