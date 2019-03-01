package com.vedeng.widget.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.vedeng.widget.base.module.InstallApkContent;
import com.vedeng.widget.base.update.UpdateManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**********************************************************
 * @文件名称：CommonActivity.java
 * @文件作者：聂中泽
 * @创建时间：2016/5/17 16:12
 * @文件描述：公共Activity，处理一些公共业务
 * @修改历史：2016/5/17 创建初始版本
 **********************************************************/
public class CommonActivity extends Activity implements View.OnClickListener {
    private static final String SAVE_BUNDLE = "savebundle";
    protected ActivityFinishReceiver finishReceiver = new ActivityFinishReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MicBusinessConfigHelper.getInstance().isAutoSaveBundle()) {
            if (null != savedInstanceState && null != savedInstanceState.getBundle(SAVE_BUNDLE)) {
                getIntent().putExtras(savedInstanceState.getBundle(SAVE_BUNDLE));
                getIntent().putExtra("savedInstanceState", true);
            }
        }
        registerFinishReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (MicBusinessConfigHelper.getInstance().isAutoSaveBundle()) {
            outState.putBundle(SAVE_BUNDLE, getIntent().getExtras());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }

    protected void registerFinishReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MicBusinessConfigHelper.getInstance().getFinishAction());
        registerReceiver(finishReceiver, filter); // 注册
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(InstallApkContent apkContent) {
        UpdateManager.getInstance().doInstallApk(this, apkContent.apkFilePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UpdateManager.getInstance().onInstallApkResult(this, requestCode, resultCode);
    }

    @Override
    public void onClick(View v) {

    }
}
