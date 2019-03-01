package com.vedeng.widget.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vedeng.widget.base.module.InstallApkContent;
import com.vedeng.widget.base.update.UpdateManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**********************************************************
 * @文件名称：CommonAppCompatActivity.java
 * @文件作者：聂中泽
 * @创建时间：2017/4/20 9:10
 * @文件描述：公用的扩展兼容Activity
 * @修改历史：2017/4/20 创建初始版本
 **********************************************************/
public class CommonAppCompatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SAVE_BUNDLE = "savebundle";
    protected ActivityFinishReceiver finishReceiver = new ActivityFinishReceiver();
    protected Activity mBaseActivity;
    public CommonAppCompatActivity(){
        mBaseActivity = this;
    }

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
