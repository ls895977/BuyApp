package com.vedeng.widget.base.http

import com.vedeng.httpclient.DisposeDataListenerImpl
import com.vedeng.httpclient.modle.HttpResponseCodeDefine

/**********************************************************
 * @文件名称：DisposeDataListenerImpl.kt
 * @文件作者：聂中泽
 * @创建时间：2017/11/9 13:57
 * @文件描述：DisposeDataListenerImpl
 * @修改历史：2017/11/9 创建初始版本
 **********************************************************/
class DisposeDataListenerImpl(
        private var networkAnomaly: (p0: String?) -> Unit = emptyFun,
        private var success: (p0: Any?) -> Unit = emptyFun,
        private var fail: (errorCode: String?, failedMsg: String?) -> Unit = { _, _ -> },
        private var uploading: (keyCode: String?, uploadedBytes: Long, totalBytes: Long) -> Unit = { _, _, _ -> })
    : DisposeDataListenerImpl() {
    companion object {
        private var emptyFun: (p0: Any?) -> Unit = {}
    }

    override fun onNetworkAnomaly(p0: String?) {
        networkAnomaly(p0)
        if (networkAnomaly == emptyFun)
            onFailure(HttpResponseCodeDefine.UNKNOWN.toString(),
                    HttpResponseCodeDefine.UNKNOWN.toString(), p0)
    }

    override fun onSuccess(p0: Any?) {
        success(p0)
    }

    override fun onFailure(errorCode: String?, status: String?, failedMsg: String?) {
        super.onFailure(errorCode, status, failedMsg)
        fail(errorCode, failedMsg)
    }

    override fun onUpLoading(keyCode: String?, uploadedBytes: Long, totalBytes: Long) {
        super.onUpLoading(keyCode, uploadedBytes, totalBytes)
        uploading(keyCode, uploadedBytes, totalBytes)
    }
}