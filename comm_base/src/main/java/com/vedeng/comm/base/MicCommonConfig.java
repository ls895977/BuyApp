package com.vedeng.comm.base;

import android.content.Context;

/**********************************************************
 * @文件名称：MicCommonConfig.java
 * @文件作者：聂中泽
 * @创建时间：2017/3/13 16:10
 * @文件描述：公共模块参数构造器
 * @修改历史：2017/3/13 创建初始版本
 **********************************************************/
public class MicCommonConfig {
    final Context context;
    final String propertyFile;

    private MicCommonConfig(Builder builder) {
        this.context = builder.context;
        this.propertyFile = builder.propertyFile;
    }

    public static class Builder {
        private Context context;
        private String propertyFile;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setPropertyFile(String propertyFile) {
            this.propertyFile = propertyFile;
            return this;
        }

        public MicCommonConfig build() {
            return new MicCommonConfig(this);
        }
    }
}
