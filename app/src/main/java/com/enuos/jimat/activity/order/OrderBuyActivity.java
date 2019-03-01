package com.enuos.jimat.activity.order;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.goods.PayFailActivity;
import com.enuos.jimat.activity.goods.PaySuccessActivity;
import com.enuos.jimat.activity.goods.WebPayActivity;
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
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class OrderBuyActivity extends BaseActivity {

    @BindView(R.id.order_buy_back)
    ImageView mBack;
    @BindView(R.id.order_buy_address_name)
    TextView mOrderBuyAddressName;
    @BindView(R.id.order_buy_address_all_name)
    LinearLayout mOrderBuyAddressAllName;
    @BindView(R.id.order_buy_address_phone)
    TextView mOrderBuyAddressPhone;
    @BindView(R.id.order_buy_address_all_phone)
    LinearLayout mOrderBuyAddressAllPhone;
    @BindView(R.id.order_buy_area)
    TextView mOrderBuyArea;
    @BindView(R.id.order_buy_address_all_address)
    LinearLayout mOrderBuyAddressAllAddress;
    @BindView(R.id.order_buy_shop_name)
    TextView mOrderBuyShopName;
    @BindView(R.id.order_buy_goods_pic)
    ImageView mOrderBuyGoodsPic;
    @BindView(R.id.order_buy_goods_name)
    TextView mOrderBuyGoodsName;
    @BindView(R.id.order_buy_goods_price)
    TextView mOrderBuyGoodsPrice;
    @BindView(R.id.order_buy_order_number)
    TextView mOrderBuyOrderNumber;
    @BindView(R.id.order_buy_order_time)
    TextView mOrderBuyOrderTime;
    @BindView(R.id.order_buy_post_price)
    TextView mOrderBuyPostPrice;
    @BindView(R.id.order_buy_mine_coins)
    TextView mOrderBuyMineCoins;
    @BindView(R.id.order_buy_coins_checked)
    CheckBox mOrderBuyCoinsChecked;
    @BindView(R.id.order_buy_others_checked)
    CheckBox mOrderBuyOthersChecked;
    @BindView(R.id.order_buy_order_end_time)
    TextView mOrderBuyOrderEndTime;
    @BindView(R.id.order_buy_pay_price)
    TextView mOrderBuyPayPrice;
    @BindView(R.id.order_buy_btn_pay)
    Button mOrderBuyBtnPay;
    @BindView(R.id.order_buy_back_rl)
    RelativeLayout mOrderBuyBackRl;

    private User mUser;
    private String addressId, coinsMoney;
    private String shopName, goodsPic, goodsName, goodsPrice, orderId, orderTime, totalPrice, orderNo;
    private boolean isCoins = false;
    private boolean isOther = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_buy);
        ButterKnife.bind(this);

        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        shopName = getIntent().getStringExtra("shopName");
        goodsPic = getIntent().getStringExtra("goodsPic");
        goodsName = getIntent().getStringExtra("goodsName");
        goodsPrice = getIntent().getStringExtra("goodsPrice");
        orderId = getIntent().getStringExtra("orderId");
        orderNo = getIntent().getStringExtra("orderNo");
        totalPrice = getIntent().getStringExtra("totalPrice");
        orderTime = getIntent().getStringExtra("orderTime");

        mOrderBuyGoodsName.setText(goodsName);
        mOrderBuyShopName.setText(shopName);
        mOrderBuyGoodsPrice.setText("RM " + goodsPrice);
        mOrderBuyPayPrice.setText("RM " + totalPrice);
        mOrderBuyOrderNumber.setText(orderNo);
        mOrderBuyOrderTime.setText(orderTime);
        Glide.with(mBaseActivity).load(goodsPic).into(mOrderBuyGoodsPic);

        addressId = getIntent().getStringExtra("addressId");
        mOrderBuyAddressName.setText(getIntent().getStringExtra("addressName"));
        mOrderBuyAddressPhone.setText(getIntent().getStringExtra("addressPhone"));
        mOrderBuyArea.setText(getIntent().getStringExtra("addressArea"));
        mOrderBuyOrderTime.setText(getIntent().getStringExtra("orderTime"));
        mOrderBuyPostPrice.setText("RM " + getIntent().getStringExtra("postPrice"));
        // 获取用户余额
        UserMoneyTask taskMoney = new UserMoneyTask();
        HashMap<String, String> paramsMoney = new HashMap<>();
        paramsMoney.put("memberId", mUser.userID);
        paramsMoney.put("token", mUser.token);
        paramsMoney.put("memberMobile", mUser.userAccount);
        taskMoney.execute(paramsMoney);

        // 余额支付
        mOrderBuyCoinsChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOrderBuyOthersChecked.setChecked(false);
                    mOrderBuyCoinsChecked.setClickable(false);
                    mOrderBuyOthersChecked.setClickable(true);
                    isCoins = true;
                    isOther = false;
                }
            }
        });

        // 银行卡支付
        mOrderBuyOthersChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOrderBuyCoinsChecked.setChecked(false);
                    mOrderBuyOthersChecked.setClickable(false);
                    mOrderBuyCoinsChecked.setClickable(true);
                    isCoins = false;
                    isOther = true;

                }
            }
        });
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.order_buy_back_rl, R.id.order_buy_btn_pay})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.order_buy_back_rl:
                finish();
                break;
            // 支付
            case R.id.order_buy_btn_pay:
                if (!isCoins && !isOther) {
                    ToastUtils.show(mBaseActivity, "Please select the payment method");
                    return;
                }
                // 余额支付
                if (isCoins) {
                    if (Double.valueOf(goodsPrice) > Double.valueOf(coinsMoney)) {
                        ToastUtils.show(mBaseActivity, "Balance not enough, please re-charge and try again");
                    } else {
                        // 取出token      params.put("token", userToken);
                        IDataStorage dataStorage = DataStorageFactory.getInstance(
                                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                        User user = dataStorage.load(User.class, "User");
                        String userToken = "";
                        if (user != null && !user.userAccount.equals("")) {
                            userToken = user.token;
                        }

                        HashMap<String, String> params = new HashMap<>();
                        params.put("orderId", orderId);
                        params.put("payType", "1");
                        params.put("token", userToken);
                        PayTask mPayTask = new PayTask();
                        mPayTask.execute(params);
                    }
                } else { // 第三方银行支付
                    // 取出token      params.put("token", userToken);
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = dataStorage.load(User.class, "User");
                    String userToken = "";
                    if (user != null && !user.userAccount.equals("")) {
                        userToken = user.token;
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("orderId", orderId);
                    params.put("payType", "4");
                    params.put("token", userToken);
                    PayThreeTask mPayThreeTask = new PayThreeTask();
                    mPayThreeTask.execute(params);
                }
                break;
        }
    }

    /**
     * 内部类
     * 支付
     */
    private class PayTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.goods_pay_two_url, params[0],
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
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "支付的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    if (jsonObjectData.getString("PAY_STATE").equals("2")) { // 支付成功
                        Intent intent = new Intent(mBaseActivity, PaySuccessActivity.class);
                        intent.putExtra("orderId", jsonObjectData.getString("ID"));
                        intent.putExtra("orderNumber", jsonObjectData.getString("ORDER_CODE"));
                        intent.putExtra("orderTime", jsonObjectData.getString("CREATE_TIME"));
                        intent.putExtra("orderPrice", jsonObjectData.getString("TOTAL_PRICE"));
                        startActivity(intent);
                        finish();
                    } else { // 支付失败
                        Intent intent = new Intent(mBaseActivity, PayFailActivity.class);
                        intent.putExtra("orderNumber", jsonObjectData.getString("ORDER_CODE"));
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
                    finish();
                }
                // 同一账户多个终端登录
            }

        }
    }

    /**
     * 内部类
     * 支付
     */
    private class PayThreeTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.goods_pay_two_url, params[0],
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
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "支付的返回结果：" + data.toString());
                    Intent intent = new Intent(mBaseActivity, WebPayActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("title", "Order Pay");
                    intent.putExtra("url", UrlConfig.bank_pay_head_url
                            + "amount=" + goodsPrice + "&orderid=" + orderId + UrlConfig.bank_pay_tail_url);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
                    finish();
                }
                // 同一账户多个终端登录
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

            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "获取用户余额的返回结果：" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    coinsMoney = jsonObject.getString("MEMBER_BALANCE");
                    mOrderBuyMineCoins.setText("(RM " + coinsMoney + ")");
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
                    finish();
                }
                // 同一账户多个终端登录
            }

        }

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }


}
