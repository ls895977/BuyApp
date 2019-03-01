package com.enuos.jimat.activity.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.account.newInfo.RegisterNewActivity;
import com.enuos.jimat.activity.address.MineAddressActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.mine.MineInfoActivity;
import com.enuos.jimat.activity.mine.MineSetActivity;
import com.enuos.jimat.activity.mine.history.MineHistoryActivity;
import com.enuos.jimat.activity.money.MineMoneyActivity;
import com.enuos.jimat.activity.msg.MineMsgActivity;
import com.enuos.jimat.activity.order.MineOrderActivity;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

import static com.enuos.jimat.utils.MyUtils.callNumber;

public class MineActivity extends BaseActivity {

    @BindView(R.id.mine_head_icon)
    ImageView mMineHeadIcon;
    @BindView(R.id.mine_user_account)
    TextView mMineUserAccount;
    @BindView(R.id.mine_user_email)
    TextView mMineUserEmail;
    @BindView(R.id.mine_text_money)
    TextView mMineTextMoney;
    @BindView(R.id.mine_linear_money)
    LinearLayout mMineLinearMoney;
    @BindView(R.id.mine_linear_info)
    LinearLayout mMineLinearInfo;
    @BindView(R.id.mine_linear_address)
    LinearLayout mMineLinearAddress;
    @BindView(R.id.mine_linear_order)
    LinearLayout mMineLinearOrder;
    @BindView(R.id.mine_linear_msg)
    LinearLayout mMineLinearMsg;
    @BindView(R.id.mine_linear_service)
    LinearLayout mMineLinearService;
    @BindView(R.id.mine_text_phone)
    TextView mMineTextPhone;
    @BindView(R.id.mine_linear_phone)
    LinearLayout mMineLinearPhone;
    @BindView(R.id.mine_linear_set)
    LinearLayout mMineLinearSet;
    @BindView(R.id.mine_swipe_refresh)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.mine_linear_history)
    LinearLayout mMineLinearHistory;
    @BindView(R.id.mine_user_go_login)
    TextView mMineUserGoLogin;
    @BindView(R.id.mine_user_go_view)
    View mMineUserGoView;
    @BindView(R.id.mine_user_go_register)
    TextView mMineUserGoRegister;

    private SweetAlertDialog mProgressDialog;
    private String money;
    private String account, headImage, email, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);
        // 进入界面后先获取个人信息
        if (isLogin()) {
            mMineUserAccount.setVisibility(View.VISIBLE);
            mMineLinearMoney.setVisibility(View.VISIBLE);
            mMineUserGoLogin.setVisibility(View.GONE);
            mMineUserGoView.setVisibility(View.GONE);
            mMineUserGoRegister.setVisibility(View.GONE);
            mSwipe.setEnabled(true);
            setSwipe();
            doRefresh();
        } else {
            mSwipe.setEnabled(false);
            mMineUserAccount.setVisibility(View.GONE);
            mMineLinearMoney.setVisibility(View.GONE);
            mMineUserGoLogin.setVisibility(View.VISIBLE);
            mMineUserGoView.setVisibility(View.VISIBLE);
            mMineUserGoRegister.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_LOGIN)) {
            doRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitEvent(String message) {
        if (message.equals(EventConfig.EVENT_EXIT)) {
            mSwipe.setEnabled(false);
            mMineUserAccount.setVisibility(View.GONE);
            mMineLinearMoney.setVisibility(View.GONE);
            mMineUserGoLogin.setVisibility(View.VISIBLE);
            mMineUserGoView.setVisibility(View.VISIBLE);
            mMineUserGoRegister.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断是否登录
     */
    public boolean isLogin() {
        // 取出信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        if (user != null && !user.userAccount.equals("") ) {
           return true;
        }
        return false;
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
        // 获取用户个人信息
        UserMsgTask task = new UserMsgTask();
        HashMap<String, String> params = new HashMap<>();
        params.put("memberId", userID);
        params.put("memberMobile", userAccount);
        task.execute(params);
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
     * 加载页面所有的视图元素
     */
    private void loadPage(JSONObject getJsonObject) {
        try {
            IDataStorage dataStorage = DataStorageFactory.getInstance(
                    getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
            account = dataStorage.load(User.class, "User").userAccount;
            // 隐藏手机号中间4位
            String phoneToast = account.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            mMineUserAccount.setText(phoneToast);

            name = getJsonObject.getString("TRUE_NAME");

            email = getJsonObject.getString("MEMBER_EMAIL");
            if (email.equals("null") || email.equals("")) {
                mMineUserEmail.setText("未绑定邮箱");
            } else {
                mMineUserEmail.setText(email);
            }

            headImage = getJsonObject.getString("MEMBER_AVATAR");
            if (headImage.equals("null") || headImage.equals("")) {
                Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineHeadIcon);
            } else {
                Glide.with(mBaseActivity).load(headImage).into(mMineHeadIcon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户个人信息内部类
     */
    private class UserMsgTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.user_info_url, params[0],
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
                    Log.e("TAG", "获取用户个人信息的返回结果：" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    loadPage(jsonObject);
                    mSwipe.setRefreshing(false);
                } catch (Exception e) {
                    //Toast.makeText(SettingsActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
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
                    money = jsonObject.getString("MEMBER_BALANCE");
                    if (money.equals("null") || money.equals("")) {
                        mMineTextMoney.setText("0元");
                    } else {
                        mMineTextMoney.setText(money + "元");
                    }
                    mSwipe.setRefreshing(false);
                } catch (Exception e) {
                    //Toast.makeText(SettingsActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
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

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_linear_money, R.id.mine_linear_info, R.id.mine_linear_address,
            R.id.mine_linear_order, R.id.mine_linear_msg, R.id.mine_linear_history,
            R.id.mine_linear_service, R.id.mine_linear_phone, R.id.mine_linear_set,
            R.id.mine_user_go_login, R.id.mine_user_go_register})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 余额
            case R.id.mine_linear_money:
                if (isLogin()) {
                    Intent intent = new Intent(mBaseActivity, MineMoneyActivity.class);
                    intent.putExtra("money", money);
                    startActivity(intent);
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 个人资料
            case R.id.mine_linear_info:
                if (isLogin()) {
                    Intent intentInfo = new Intent(mBaseActivity, MineInfoActivity.class);
                    intentInfo.putExtra("account", account);
                    intentInfo.putExtra("headImage", headImage);
                    intentInfo.putExtra("email", email);
                    intentInfo.putExtra("name", name);
                    startActivity(intentInfo);
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 收货地址
            case R.id.mine_linear_address:
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineAddressActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 我的订单
            case R.id.mine_linear_order:
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineOrderActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 我的消息
            case R.id.mine_linear_msg:
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineMsgActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 历史产品
            case R.id.mine_linear_history:
                startActivity(new Intent(mBaseActivity, MineHistoryActivity.class));
                break;
            // 我的客服
            case R.id.mine_linear_service:
                if (isLogin()) {
                    Intent intent = new IntentBuilder(mBaseActivity)
                            .setServiceIMNumber("kefuchannelimid_505678")
                            .build();
                    startActivity(intent);
//                    startActivity(new Intent(mBaseActivity, MineServiceActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 客服电话
            case R.id.mine_linear_phone:
                final String phone = mMineTextPhone.getText().toString();
                final SweetAlertDialog phoneDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("确认")
                        .setCancelText("取消");
                phoneDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        phoneDialog.dismiss();
                        if (!phone.equals("") && !phone.equals("null")) {
                            startActivity(callNumber(phone.replace("-", "")));
                        }
                    }
                });
                phoneDialog.setTitleText("确定要拨打客服电话吗？");
                phoneDialog.setContentText("客服电话：" + phone);
                phoneDialog.show();
                break;
            // 设置
            case R.id.mine_linear_set:
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineSetActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                    intent.putExtra("from", "mine");
                    startActivity(intent);
                }
                break;
            // 去登录
            case R.id.mine_user_go_login:
                Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                intent.putExtra("from", "mine");
                startActivity(intent);
                break;
            // 去注册
            case R.id.mine_user_go_register:
                startActivity(new Intent(mBaseActivity, RegisterNewActivity.class));
                break;
        }
    }

}
