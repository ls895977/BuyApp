package com.enuos.jimat.activity.money;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineMoneyActivity extends BaseActivity {

    @BindView(R.id.mine_money_back)
    ImageView mBack;
    @BindView(R.id.mine_money_text_details)
    TextView mMineMoneyTextDetails;
    @BindView(R.id.mine_money_number)
    TextView mMineMoneyNumber;
    @BindView(R.id.mine_money_ll_upload)
    LinearLayout mMineMoneyLlUpload;
    @BindView(R.id.mine_money_ll_upload_record)
    LinearLayout mMineMoneyLlUploadRecord;
    @BindView(R.id.mine_money_swipe_refresh)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.mine_money_back_rl)
    RelativeLayout mMineMoneyBackRl;

    private SweetAlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_money);
        ButterKnife.bind(this);

        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);

        setSwipe();
        doRefresh();

    }

    @Override
    protected void onResume() {
        doRefresh();
        super.onResume();
    }

    /**
     * 设置刷新
     */
    private void setSwipe() {
        mSwipe.setColorSchemeColors(ContextCompat.getColor(mBaseActivity, R.color.blue_btn_bg_color));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    /**
     * 获得个人信息
     */
    private void doRefresh() {
        mSwipe.setRefreshing(true);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        String userID = dataStorage.load(User.class, "User").userID;
        String userAccount = dataStorage.load(User.class, "User").userAccount;
        String token = dataStorage.load(User.class, "User").token;
        // 获取用户余额
        UserMoneyTask taskMoney = new UserMoneyTask();
        HashMap<String, String> paramsMoney = new HashMap<>();
        paramsMoney.put("memberId", userID);
        paramsMoney.put("token", token);
        paramsMoney.put("memberMobile", userAccount);
        taskMoney.execute(paramsMoney);
        mSwipe.setRefreshing(false);
    }


    /**
     * 点击事件
     */
    @OnClick({R.id.mine_money_back_rl, R.id.mine_money_text_details,
            R.id.mine_money_ll_upload, R.id.mine_money_ll_upload_record})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_money_back_rl:
                finish();
                break;
            // 余额明细
            case R.id.mine_money_text_details:
                startActivity(new Intent(mBaseActivity, MineMoneyDetailsActivity.class));
                break;
            // 上传汇款凭证
            case R.id.mine_money_ll_upload:
                startActivity(new Intent(mBaseActivity, MoneyUploadActivity.class));
                break;
            // 汇款凭证记录
            case R.id.mine_money_ll_upload_record:
                startActivity(new Intent(mBaseActivity, UploadRecordActivity.class));
                break;
        }
    }

    /**
     * 获取用户余额内部类
     */
    private class UserMoneyTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.balance_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object[] result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "获取用户余额的返回结果：" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    String money = jsonObject.getString("MEMBER_BALANCE");
                    if (money == null || money.equals("")) {
                        mMineMoneyNumber.setText("0");
                    } else {
                        int moneyLength = money.length();
                        if (moneyLength > 6) { // 999.99  9999.99
                            String newMoney = money.substring(0, moneyLength - 6) + "," + money.substring(moneyLength - 6, moneyLength);
                            mMineMoneyNumber.setText(newMoney);
                        } else {
                            mMineMoneyNumber.setText(money);
                        }
                    }
                    mSwipe.setRefreshing(false);
                } catch (Exception e) {
                    //Toast.makeText(SettingsActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                // 同一账户多个终端登录
                String msgError = result[1].toString();
                ToastUtils.show(mBaseActivity, msgError);
                if (msgError.contains("Your account is being logged") || msgError.contains("account")) {
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
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentG = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentG.putExtra("from", "mine");
                    intentG.putExtra("goodsId", "");
                    intentG.putExtra("goodsType", "");
                    intentG.putExtra("homeTime", "0");
                    startActivity(intentG);
                    finish();
                }
                // 同一账户多个终端登录
            }
        }

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

}
