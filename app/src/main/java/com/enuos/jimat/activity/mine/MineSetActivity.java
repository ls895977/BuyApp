package com.enuos.jimat.activity.mine;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.WebActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.event.EventConfig;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineSetActivity extends BaseActivity {

    @BindView(R.id.mine_set_back)
    ImageView mBack;
    @BindView(R.id.set_linear_protocol)
    LinearLayout mSetLinearProtocol;
    @BindView(R.id.set_linear_secret)
    LinearLayout mSetLinearSecret;
    @BindView(R.id.set_btn_exit)
    Button mSetBtnExit;
    @BindView(R.id.mine_set_back_rl)
    RelativeLayout mMineSetBackRl;
    @BindView(R.id.set_linear_about)
    LinearLayout mSetLinearAbout;
    @BindView(R.id.set_linear_encourage)
    LinearLayout mSetLinearEncourage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_set);
        ButterKnife.bind(this);
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_set_back_rl, R.id.set_linear_protocol,
            R.id.set_linear_secret, R.id.set_btn_exit, R.id.set_linear_encourage})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_set_back_rl:
                finish();
                break;
            // 服务协议
            case R.id.set_linear_protocol:
                Intent intent = new Intent(mBaseActivity, WebActivity.class);
                intent.putExtra("title", "Terms of Use");
                intent.putExtra("url", "http://47.254.192.108:8080/jimatInterface/fwxy.html");
                startActivity(intent);
                break;
            // 隐私权政策
            case R.id.set_linear_secret:
                Intent intentSecret = new Intent(mBaseActivity, WebActivity.class);
                intentSecret.putExtra("title", "Privacy Policy");
                intentSecret.putExtra("url", "http://47.254.192.108:8080/jimatInterface/yszc.html");
                startActivity(intentSecret);
                break;
            // 退出登录
            case R.id.set_btn_exit:
                final SweetAlertDialog eDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("Confirm")
                        .setCancelText("Cancel");
                eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        eDialog.dismiss();
                        // 退出 APP 本身的账号
                        IDataStorage dataStorage = DataStorageFactory.getInstance(
                                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                        User user = new User();
                        user.userAccount = "";
                        user.isLogin = "false";
                        user.token = "";
                        dataStorage.storeOrUpdate(user, "User");
                        EventBus.getDefault().post(EventConfig.EVENT_EXIT);
                        // 退出环信账号
                        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                // 登录环信服务器退出登录
                                EMClient.getInstance().logout(false, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        // 关闭 DBHelper
                                        // Model.getInstance().getDbManager().close();
                                        Log.e("789", "环信账号退出成功");
                                    }

                                    @Override
                                    public void onError(int i, final String s) {
                                    }

                                    @Override
                                    public void onProgress(int i, String s) {
                                    }
                                });
                            }
                        });
                        MineSetActivity.this.finish();
                    }
                });
                eDialog.setTitleText("Are you sure you want to quit?");
                eDialog.show();
                break;
            // 去应用商品评价
            case R.id.set_linear_encourage:
                try {
                    Intent intentGoogle = new Intent(Intent.ACTION_VIEW);
                    intentGoogle.setData(Uri.parse("market://details?id=" + getPackageName()));
                    intentGoogle.setPackage("com.android.vending"); // 谷歌应用市场
                    if (intentGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(intentGoogle);
                    } else { // 没有应用市场，通过浏览器跳转
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                        if (intent2.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent2);
                        } else {
                            //没有Google Play 也没有浏览器
                        }
                    }
                } catch (ActivityNotFoundException activityNotFoundException1) {
//                    Log.e(AppRater.class.getSimpleName(), "GoogleMarket Intent not found");
                }
                break;
        }
    }

}
