package com.vedeng.httpclient;

import android.app.Activity;

/**********************************************************
 * @文件名称：DefaultDisposeDataListenerImpl.java
 * @文件作者：聂中泽
 * @创建时间：2016/7/15 11:34
 * @文件描述：默认的实例化的数据回调监听
 * @修改历史：2016/7/15 创建初始版本
 **********************************************************/
public class DefaultDisposeDataListenerImpl extends DisposeDataListenerImpl {
    public DefaultDisposeDataListenerImpl() {
        super();
    }

    public DefaultDisposeDataListenerImpl(Activity activity) {
        super(activity);
    }

    @Override
    public void onSuccess(Object obj) {

    }

    @Override
    public void onNetworkAnomaly(String errorMsg) {

    }
}
