package com.vedeng.widget.base.http

import com.vedeng.widget.base.MicBusinessConfigHelper

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/11/15 10:49
 * @文件描述：
 * @修改历史：2018/11/15 创建初始版本
 **********************************************************/
open class BaseRequestCenter {
    companion object I {
        fun getVersionCode(): String {
            return MicBusinessConfigHelper.getInstance().versionCode
        }
    }
}