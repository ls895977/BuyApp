package com.enuos.jimat.activity.goods;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.address.ChooseAddressActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.goods.bean.GoodsBuyBean;
import com.enuos.jimat.activity.order.OrderDetailsActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.ClickUtils;
import com.enuos.jimat.utils.event.AddressEvent;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class GoodsBuyActivity extends BaseActivity {

    @BindView(R.id.goods_buy_back)
    ImageView mBack;
    @BindView(R.id.goods_buy_address_name)
    TextView mGoodsBuyAddressName;
    @BindView(R.id.goods_buy_address_phone)
    TextView mGoodsBuyAddressPhone;
    @BindView(R.id.goods_buy_area)
    TextView mGoodsBuyArea;
    @BindView(R.id.goods_buy_address_next)
    TextView mGoodsBuyAddressNext;
    @BindView(R.id.goods_buy_shop_name)
    TextView mGoodsBuyShopName;
    @BindView(R.id.goods_buy_goods_pic)
    ImageView mGoodsBuyGoodsPic;
    @BindView(R.id.goods_buy_goods_name)
    TextView mGoodsBuyGoodsName;
    @BindView(R.id.goods_buy_goods_price)
    TextView mGoodsBuyGoodsPrice;
    @BindView(R.id.goods_buy_order_number)
    TextView mGoodsBuyOrderNumber;
    @BindView(R.id.goods_buy_order_time)
    TextView mGoodsBuyOrderTime;
    @BindView(R.id.goods_buy_coins_checked)
    CheckBox mGoodsBuyCoinsChecked;
    @BindView(R.id.goods_buy_wechat_checked)
    CheckBox mGoodsBuyWechatChecked;
    @BindView(R.id.goods_buy_ali_checked)
    CheckBox mGoodsBuyAliChecked;
    @BindView(R.id.goods_buy_others_checked)
    CheckBox mGoodsBuyOthersChecked;
    @BindView(R.id.goods_buy_order_end_time)
    TextView mGoodsBuyOrderEndTime;
    @BindView(R.id.goods_buy_pay_price)
    TextView mGoodsBuyPayPrice;
    @BindView(R.id.goods_buy_btn_pay)
    Button mGoodsBuyBtnPay;
    @BindView(R.id.goods_buy_address_next_empty)
    ImageView mGoodsBuyAddressNextEmpty;
    @BindView(R.id.goods_buy_address_ll_empty)
    LinearLayout mGoodsBuyAddressLlEmpty;
    @BindView(R.id.goods_buy_address_all_name)
    LinearLayout mGoodsBuyAddressAllName;
    @BindView(R.id.goods_buy_address_all_phone)
    LinearLayout mGoodsBuyAddressAllPhone;
    @BindView(R.id.goods_buy_address_all_address)
    LinearLayout mGoodsBuyAddressAllAddress;
    @BindView(R.id.goods_buy_post_price)
    TextView mGoodsBuyPostPrice;
    @BindView(R.id.goods_buy_mine_coins)
    TextView mGoodsBuyMineCoins;
    @BindView(R.id.goods_buy_back_rl)
    RelativeLayout mGoodsBuyBackRl;

    private User mUser;
    private JSONArray mAdressArray;
    private String addressId, addressName, addressPhone, addressArea, coinsMoney, postPrice, weight;
    private String shopName, goodsPic, goodsName, goodsPrice, orderId, orderTime, payPrice, orderNo, vcode;
    private boolean isCoins = true;
    private boolean isWechat = false;
    private boolean isAli = false;
    private boolean isOther = false;

    /**
     * 5分钟倒计时
     */
    private CountDownTimer mTimer300 = new CountDownTimer(300000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = millisUntilFinished / 1000;
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            mGoodsBuyOrderEndTime.setText("剩" + String.valueOf(minutes) + "分" + String.valueOf(remainingSeconds) + "秒自动结束");
        }

        @Override
        public void onFinish() {
            finish();
        }
    };

    @Subscribe
    public void onAddressEvent(AddressEvent event) {
        if (event.type.equals("address")) {
            mGoodsBuyAddressLlEmpty.setVisibility(View.GONE);
            mGoodsBuyAddressAllName.setVisibility(View.VISIBLE);
            mGoodsBuyAddressAllPhone.setVisibility(View.VISIBLE);
            mGoodsBuyAddressAllAddress.setVisibility(View.VISIBLE);
            addressId = event.id;
            addressName = event.name;
            addressPhone = event.tel;
            addressArea = event.address;
            mGoodsBuyAddressName.setText("Receiver：" + addressName);
            mGoodsBuyAddressPhone.setText("Contact Number：" + addressPhone);
            mGoodsBuyArea.setText("Address：" + addressArea);
            if (event.provice == null || event.provice.equals("")) {
                mGoodsBuyPostPrice.setText("");//邮费
                mGoodsBuyPayPrice.setText("RM " + goodsPrice);
//                mGoodsBuyPayPrice.setText("RM " + String.format("%.2f", Double.valueOf(goodsPrice)));
                payPrice = goodsPrice;
            } else {
                postPrice = getPostPrice(event.provice, Double.valueOf(weight));
//                mGoodsBuyPostPrice.setText("RM " + String.format("%.2f", Double.valueOf(postPrice)));
                mGoodsBuyPostPrice.setText("RM " + postPrice);//邮费
                double postDouble = Double.valueOf(postPrice);
                double goodsDouble = Double.valueOf(goodsPrice);
                payPrice = String.valueOf(postDouble + goodsDouble);

//                mGoodsBuyPayPrice.setText("RM " + String.format("%.2f", Double.valueOf(payPrice)));
                mGoodsBuyPayPrice.setText("RM " + payPrice);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_buy);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);

        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");
        // 进入界面之后先获取信息
        refresh();

        shopName = getIntent().getStringExtra("shopName");
        goodsPic = getIntent().getStringExtra("goodsPic");
        goodsName = getIntent().getStringExtra("goodsName");
        goodsPrice = getIntent().getStringExtra("goodsPrice");
        orderId = getIntent().getStringExtra("orderId");
        orderNo = getIntent().getStringExtra("orderNo");
        orderTime = getIntent().getStringExtra("orderTime");
        weight = getIntent().getStringExtra("weight");
        mGoodsBuyGoodsName.setText(goodsName);
        mGoodsBuyShopName.setText(shopName);
//        mGoodsBuyGoodsPrice.setText("RM " + String.format("%.2f", Double.valueOf(goodsPrice)));
//        mGoodsBuyPayPrice.setText("RM " + String.format("%.2f", Double.valueOf(goodsPrice)));
        mGoodsBuyGoodsPrice.setText("RM " + goodsPrice);
        mGoodsBuyPayPrice.setText("RM " + goodsPrice);
        mGoodsBuyOrderNumber.setText(orderNo);
        mGoodsBuyOrderTime.setText(orderTime);
        Glide.with(mBaseActivity).load(goodsPic).into(mGoodsBuyGoodsPic);

        payPrice = goodsPrice;

        // 获取用户余额
        UserMoneyTask taskMoney = new UserMoneyTask();
        HashMap<String, String> paramsMoney = new HashMap<>();
        paramsMoney.put("memberId", mUser.userID);
        paramsMoney.put("token", mUser.token);
        paramsMoney.put("memberMobile", mUser.userAccount);
        taskMoney.execute(paramsMoney);

        // 5分钟倒计时
//        mTimer300.start();

        // 余额支付
        mGoodsBuyCoinsChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGoodsBuyWechatChecked.setChecked(false);
                    mGoodsBuyAliChecked.setChecked(false);
                    mGoodsBuyOthersChecked.setChecked(false);
                    mGoodsBuyCoinsChecked.setClickable(false);
                    mGoodsBuyOthersChecked.setClickable(true);
                    isCoins = true;
                    isWechat = false;
                    isAli = false;
                    isOther = false;
                }
            }
        });

        // 微信支付
        mGoodsBuyWechatChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGoodsBuyCoinsChecked.setChecked(false);
                    mGoodsBuyAliChecked.setChecked(false);
                    mGoodsBuyOthersChecked.setChecked(false);

                    isCoins = false;
                    isWechat = true;
                    isAli = false;
                    isOther = false;
                }
            }
        });

        // 支付宝支付
        mGoodsBuyAliChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGoodsBuyCoinsChecked.setChecked(false);
                    mGoodsBuyWechatChecked.setChecked(false);
                    mGoodsBuyOthersChecked.setChecked(false);

                    isCoins = false;
                    isWechat = false;
                    isAli = true;
                    isOther = false;
                }
            }
        });

        // 银行卡支付
        mGoodsBuyOthersChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGoodsBuyCoinsChecked.setChecked(false);
                    mGoodsBuyWechatChecked.setChecked(false);
                    mGoodsBuyAliChecked.setChecked(false);
                    mGoodsBuyOthersChecked.setClickable(false);
                    mGoodsBuyCoinsChecked.setClickable(true);
                    isCoins = false;
                    isWechat = false;
                    isAli = false;
                    isOther = true;

                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.goods_buy_back_rl, R.id.goods_buy_address_next, R.id.goods_buy_address_next_empty, R.id.goods_buy_btn_pay})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.goods_buy_back_rl:
                finish();
                break;
            // 有地址 选择新地址
            case R.id.goods_buy_address_next:
                startActivity(new Intent(mBaseActivity, ChooseAddressActivity.class));
                break;
            // 有地址 选择新地址
            case R.id.goods_buy_address_next_empty:
                startActivity(new Intent(mBaseActivity, ChooseAddressActivity.class));
                break;
            // 支付
            case R.id.goods_buy_btn_pay:
                if (!ClickUtils.INSTANCE.isFastDoubleClick()) {
                    if (mGoodsBuyAddressName.getText().toString().equals("")) {
                        ToastUtils.show(mBaseActivity, "Please choose a Shipping Address");
                        return;
                    }

                    if (!isCoins && !isWechat && !isAli && !isOther) {
                        ToastUtils.show(mBaseActivity, "Please select the payment method");
                        return;
                    }

                    // 余额支付
                    if (isCoins) {
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
                        params.put("addressId", addressId);
                        params.put("payType", "1");
                        params.put("token", userToken);
                        PayTask mPayTask = new PayTask();
                        mPayTask.execute(params);
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
                        params.put("addressId", addressId);
                        params.put("payType", "4");
                        params.put("token", userToken);
                        PayThreeTask mPayThreeTask = new PayThreeTask();
                        mPayThreeTask.execute(params);
                    }
                }
                break;
        }
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
        HashMap<String, String> params = new HashMap<>();
        params.put("memberId", mUser.userID);
        params.put("token", mUser.token);
        AddressTask mAddressTask = new AddressTask();
        mAddressTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(JSONObject jsonObject) {
        try {
            String stringData = jsonObject.getString("data");
            JSONObject jsonObjectData = new JSONObject(stringData);
            String jsonArrayString = jsonObjectData.getString("list");
            mAdressArray = new JSONArray(jsonArrayString.replaceAll("\t", ""));
            mGoodsBuyAddressName.setText("Receiver：" + mAdressArray.getJSONObject(0).getString("TAKE_NAME"));
            mGoodsBuyAddressPhone.setText("Contact Number：" + mAdressArray.getJSONObject(0).getString("TAKE_MOBILE"));
//            String area = mAdressArray.getJSONObject(0).getString("TAKE_PROVINCE")
//                    + mAdressArray.getJSONObject(0).getString("TAKE_CITY")
//                    + mAdressArray.getJSONObject(0).getString("TAKE_AREA")
//                    + mAdressArray.getJSONObject(0).getString("TAKE_ADDRESS");
            String province = mAdressArray.getJSONObject(0).getString("TAKE_PROVINCE");
            String area = province + " " + mAdressArray.getJSONObject(0).getString("TAKE_ADDRESS");
            mGoodsBuyArea.setText("Address：" + area);
            addressId = mAdressArray.getJSONObject(0).getString("ID");

            // 计算邮费
            if (province == null || province.equals("")) {
                mGoodsBuyPostPrice.setText("");

//                mGoodsBuyPayPrice.setText("RM " + String.format("%.2f", Double.valueOf(goodsPrice)));
                mGoodsBuyPayPrice.setText("RM " + goodsPrice);
                payPrice = goodsPrice;
            } else {
                postPrice = getPostPrice(province, Double.valueOf(weight));
//                mGoodsBuyPostPrice.setText("RM " + String.format("%.2f", Double.valueOf(postPrice)));

                mGoodsBuyPostPrice.setText("RM " + postPrice);

                double postDouble = Double.valueOf(postPrice);
                double goodsDouble = Double.valueOf(goodsPrice);
                payPrice = String.valueOf(postDouble + goodsDouble);
//                mGoodsBuyPayPrice.setText("RM " + String.format("%.2f", Double.valueOf(payPrice)));

                mGoodsBuyPayPrice.setText("RM " + payPrice);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部类
     * 收货地址列表
     */
    private class AddressTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.address_url, params[0],
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
                    Log.e("TAG", "收货地址列表的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String jsonArrayString = jsonObjectData.getString("maxCount");
                    if (jsonArrayString.equals("0")) {
                        mGoodsBuyAddressLlEmpty.setVisibility(View.VISIBLE);
                        mGoodsBuyAddressAllName.setVisibility(View.GONE);
                        mGoodsBuyAddressAllPhone.setVisibility(View.GONE);
                        mGoodsBuyAddressAllAddress.setVisibility(View.GONE);
                    } else {
                        mGoodsBuyAddressLlEmpty.setVisibility(View.GONE);
                        mGoodsBuyAddressAllName.setVisibility(View.VISIBLE);
                        mGoodsBuyAddressAllPhone.setVisibility(View.VISIBLE);
                        mGoodsBuyAddressAllAddress.setVisibility(View.VISIBLE);
                        loadPage(data);
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
    private class PayTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.goods_pay_url, params[0],
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
                        if (Double.valueOf(goodsPrice) > Double.valueOf(coinsMoney)) {
                            ToastUtils.show(mBaseActivity, "Balance not enough, please re-charge and try again");
                        }
                        Intent intent = new Intent(mBaseActivity, PayFailActivity.class);
                        intent.putExtra("orderNumber", jsonObjectData.getString("ORDER_CODE"));
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mBaseActivity, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
                        UrlConfig.base_url + UrlConfig.goods_pay_url, params[0],
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
                    Gson gson = new Gson();
                    JSONObject data = (JSONObject) result[2];
                    GoodsBuyBean buyBean = gson.fromJson(data.toString(), GoodsBuyBean.class);
                    Intent intent = new Intent(mBaseActivity, WebPayActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("title", "Order Pay");
//                  intent.putExtra("url", UrlConfig.bank_pay_head_url
//                            + "amount=" + payPrice + "&orderid=" + orderId + UrlConfig.bank_pay_tail_url);
                    intent.putExtra("url", "https://www.onlinepayment.com.my/MOLPay/pay/jimat/index.php?" + "amount=" + format1(Double.valueOf(payPrice)) +
                            "&orderid=" + orderId + "&vcode=" + buyBean.getData().getVcode());
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
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
                    mGoodsBuyMineCoins.setText("(RM " + coinsMoney + ")");
                } catch (Exception e) {
                    //Toast.makeText(SettingsActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
//                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
            }

        }

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }


    /**
     * 计算邮费
     */
    private String getPostPrice(String area, double weight) {
        Log.e("aa", "----area--" + area + "-----weight--" + weight);
        String strPrice;
        if (weight == 0.0) {
            strPrice = "0.00";
        } else {
            int intWeight = (int) weight + 1;
            Log.e("aa", "----intWeight--" + intWeight);
            switch (area) {
                case "Johor":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Kedah":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Kelantan":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Kuala Lumpur":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Labuan":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Melaka":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Negeri Sembilan":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Pahang":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Perak":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Perlis":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Pulau Pinang":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Putrajaya":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Sabah"://东马
                    intWeight--;
//                    if (intWeight == 0) {
//                        strPrice = "10.00";
//                    } else if (intWeight == 1) {
//                        strPrice = "10.00";
//                    } else if (intWeight == 2) {
//                        strPrice = "13.00";
//                    } else if (intWeight == 3) {
//                        strPrice = "17.50";
//                    }
                    if (weight == 0) {
                        strPrice = "10.00";
                    } else if (weight <= 1) {
                        strPrice = "10.00";
                    } else if (weight <= 2) {
                        strPrice = "13.00";
                    } else if (weight <= 3) {
                        strPrice = "17.50";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(17.50 + (intWeight - 3) * 4.50);
                    }
                    break;
                case "Sarawak"://东马
                    intWeight--;
//                    if (intWeight == 0) {
//                        strPrice = "10.00";
//                    } else if (intWeight == 1) {
//                        strPrice = "10.00";
//                    } else if (intWeight == 2) {
//                        strPrice = "13.00";
//                    } else if (intWeight == 3) {
//                        strPrice = "17.50";
//                    }
                    if (weight == 0) {
                        strPrice = "10.00";
                    } else if (weight <= 1) {
                        strPrice = "10.00";
                    } else if (weight <= 2) {
                        strPrice = "13.00";
                    } else if (weight <= 3) {
                        strPrice = "17.50";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(17.50 + (intWeight - 3) * 4.50);
                    }
                    break;
                case "Selangor":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                case "Terengganu":
                    intWeight--;
//                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
//                        strPrice = "7.00";
//                    }
                    if (weight < 2) {
                        strPrice = "7.00";
                    } else {
                        intWeight++;
                        if (weight % 1 == 0) {
                            intWeight--;
                        }
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
                default:
                    if (intWeight == 0 || intWeight == 1 || intWeight == 2) {
                        strPrice = "7.00";
                    } else {
                        strPrice = String.valueOf(7.00 + (intWeight - 2) * 1.50);
                    }
                    break;
            }
        }
        return strPrice;
    }

    public static String format1(double value) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }
}