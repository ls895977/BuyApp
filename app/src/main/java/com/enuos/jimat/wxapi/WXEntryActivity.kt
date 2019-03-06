package com.enuos.jimat.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.enuos.jimat.app.MyApplication
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/12/12 14:22
 * @文件描述：
 * @修改历史：2018/12/12 创建初始版本
 **********************************************************/
class WXEntryActivity : Activity(), IWXAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.weiXinApi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        MyApplication.weiXinApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        if (resp?.errCode == BaseResp.ErrCode.ERR_OK) {
            finish()
        } else {
            finish()
        }
    }

    override fun onReq(req: BaseReq?) {

    }
}