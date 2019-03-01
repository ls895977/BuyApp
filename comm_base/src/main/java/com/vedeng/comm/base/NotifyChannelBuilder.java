package com.vedeng.comm.base;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/5/8 10:07
 * @文件描述：消息提醒渠道构造器（SDK26以上需要）
 * @修改历史：2018/5/8 创建初始版本
 **********************************************************/
public class NotifyChannelBuilder {
    private final NotifyChannel channel;
    private final int channelResId;

    private NotifyChannelBuilder(Builder builder) {
        this.channel = builder.channel;
        this.channelResId = builder.channelResId;
    }

    public static class Builder {
        private NotifyChannel channel;
        private int channelResId = -1;

        public Builder setChannel(NotifyChannel channel) {
            this.channel = channel;
            switch (channel) {
                case TM:
                    this.channelResId = R.string.notify_channel_tm;
                    break;
                case RFQ:
                    this.channelResId = R.string.notify_channel_rfq;
                    break;
                case MAIL:
                    this.channelResId = R.string.notify_channel_mail;
                    break;
                case OTHER:
                    this.channelResId = R.string.notify_channel_other;
                    break;
                case UPDATE:
                    this.channelResId = R.string.notify_channel_update;
                    break;
                default:
                    break;
            }
            return this;
        }

        public NotifyChannelBuilder build() {
            return new NotifyChannelBuilder(this);
        }
    }

    NotifyChannel getChannel() {
        return this.channel;
    }

    int getChannelResId() {
        return this.channelResId;
    }
}
