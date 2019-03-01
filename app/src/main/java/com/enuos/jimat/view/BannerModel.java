package com.enuos.jimat.view;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2019/2/24 23:57
 * @文件描述：
 * @修改历史： 2019/2/24 创建初始版本
 **********************************************************/
public class BannerModel {
    private String bannerName;
    private String bannerUrl;
    private String videoPic;
    private int playTime;//播放时长
    private int urlType;//类型：0图片、1视频

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

}
