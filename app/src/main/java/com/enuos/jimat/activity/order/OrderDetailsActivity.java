package com.enuos.jimat.activity.order;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class OrderDetailsActivity extends BaseActivity {

    @BindView(R.id.order_details_back)
    ImageView mBack;
    @BindView(R.id.order_details_status_pic)
    ImageView mOrderDetailsStatusPic;
    @BindView(R.id.order_details_status_text)
    TextView mOrderDetailsStatusText;
    @BindView(R.id.order_details_address_name)
    TextView mOrderDetailsAddressName;
    @BindView(R.id.order_details_address_phone)
    TextView mOrderDetailsAddressPhone;
    @BindView(R.id.order_details_area)
    TextView mOrderDetailsArea;
    @BindView(R.id.order_details_shop_name)
    TextView mOrderDetailsShopName;
    @BindView(R.id.order_details_goods_pic)
    ImageView mOrderDetailsGoodsPic;
    @BindView(R.id.order_details_goods_name)
    TextView mOrderDetailsGoodsName;
    @BindView(R.id.order_details_goods_price)
    TextView mOrderDetailsGoodsPrice;
    @BindView(R.id.order_details_order_number)
    TextView mOrderDetailsOrderNumber;
    @BindView(R.id.order_details_order_time)
    TextView mOrderDetailsOrderTime;
    @BindView(R.id.order_details_order_pay_time)
    TextView mOrderDetailsOrderPayTime;
    @BindView(R.id.order_details_order_pay_time_linear)
    LinearLayout mOrderDetailsOrderPayTimeLinear;
    @BindView(R.id.order_details_order_pay_time_view)
    View mOrderDetailsOrderPayTimeView;
    @BindView(R.id.order_details_order_post_price)
    TextView mOrderDetailsOrderPostPrice;
    @BindView(R.id.order_details_order_post_company)
    TextView mOrderDetailsOrderPostCompany;
    @BindView(R.id.order_details_order_post_company_linear)
    LinearLayout mOrderDetailsOrderPostCompanyLinear;
    @BindView(R.id.order_details_order_post_company_view)
    View mOrderDetailsOrderPostCompanyView;
    @BindView(R.id.order_details_order_post_number)
    TextView mOrderDetailsOrderPostNumber;
    @BindView(R.id.order_details_order_post_number_linear)
    LinearLayout mOrderDetailsOrderPostNumberLinear;
    @BindView(R.id.order_details_order_post_number_view)
    View mOrderDetailsOrderPostNumberView;
    @BindView(R.id.order_details_order_total_price)
    TextView mOrderDetailsOrderTotalPrice;
    @BindView(R.id.order_details_btn_pay)
    Button mOrderDetailsBtnPay;
    @BindView(R.id.order_details_back_rl)
    RelativeLayout mOrderDetailsBackRl;

    private SweetAlertDialog mProgressDialog;
    private String shopName, goodsPic, goodsName, goodsPrice, orderId, orderNo,
            addressId, addressName, addressPhone, addressArea, orderTime, postPrice, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);
        orderId = getIntent().getStringExtra("orderId");
        doRefresh();
    }

    /**
     * 请求网络
     */
    private void doRefresh() {
        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        DoPostTask task = new DoPostTask();
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("token", userToken);
        task.execute(params);
    }

    /**
     * 加载页面所有的视图元素
     */
    private void loadPage(JSONObject getJsonObject) {
        try {
            if (getJsonObject.getString("PAY_STATE").equals("1")) {
                // 顶部订单状态
                mOrderDetailsStatusText.setText("Pending Payment");
                mOrderDetailsStatusText.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_FB9C33));
                Glide.with(mBaseActivity).load(R.drawable.order_pay_not).into(mOrderDetailsStatusPic);
                // UI隐藏/显示
                mOrderDetailsOrderPayTimeLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPayTimeView.setVisibility(View.GONE);
                mOrderDetailsOrderPostCompanyLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPostCompanyView.setVisibility(View.GONE);
                mOrderDetailsOrderPostNumberLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPostNumberView.setVisibility(View.GONE);
                mOrderDetailsBtnPay.setVisibility(View.VISIBLE);

            } else if (getJsonObject.getString("PAY_STATE").equals("2")) {
                // 顶部订单状态
                mOrderDetailsStatusText.setText("Transaction Completed");
                mOrderDetailsStatusText.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_00B403));
                Glide.with(mBaseActivity).load(R.drawable.order_pay_success).into(mOrderDetailsStatusPic);
                // UI隐藏/显示
                mOrderDetailsOrderPayTimeLinear.setVisibility(View.VISIBLE);
                mOrderDetailsOrderPayTimeView.setVisibility(View.VISIBLE);
                mOrderDetailsOrderPostCompanyLinear.setVisibility(View.VISIBLE);
                mOrderDetailsOrderPostCompanyView.setVisibility(View.VISIBLE);
                mOrderDetailsOrderPostNumberLinear.setVisibility(View.VISIBLE);
                mOrderDetailsOrderPostNumberView.setVisibility(View.VISIBLE);
                mOrderDetailsBtnPay.setVisibility(View.GONE);
                // UI隐藏/显示
                if (getJsonObject.getString("PAY_TIME") == null ||
                        getJsonObject.getString("PAY_TIME").equals("null")) {
                    mOrderDetailsOrderPayTime.setText("null");
                } else {
                    mOrderDetailsOrderPayTime.setText(getJsonObject.getString("PAY_TIME"));
                }

                if (getJsonObject.getString("LOGISTICS_COMPANY_NAME") == null ||
                        getJsonObject.getString("LOGISTICS_COMPANY_NAME").equals("null")) {
                    mOrderDetailsOrderPostCompany.setText("null");
                } else {
                    mOrderDetailsOrderPostCompany.setText(getJsonObject.getString("LOGISTICS_COMPANY_NAME"));
                }

                if (getJsonObject.getString("LOGISTICS_CODE") == null ||
                        getJsonObject.getString("LOGISTICS_CODE").equals("null")) {
                    mOrderDetailsOrderPostNumber.setText("null");
                } else {
                    mOrderDetailsOrderPostNumber.setText(getJsonObject.getString("LOGISTICS_CODE"));
                }

            } else {
                // 顶部订单状态
                mOrderDetailsStatusText.setText("Transaction Failed");
                mOrderDetailsStatusText.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_D02D2E));
                Glide.with(mBaseActivity).load(R.drawable.order_pay_fail).into(mOrderDetailsStatusPic);
                // UI隐藏/显示
                mOrderDetailsOrderPayTimeLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPayTimeView.setVisibility(View.GONE);
                mOrderDetailsOrderPostCompanyLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPostCompanyView.setVisibility(View.GONE);
                mOrderDetailsOrderPostNumberLinear.setVisibility(View.GONE);
                mOrderDetailsOrderPostNumberView.setVisibility(View.GONE);
                mOrderDetailsBtnPay.setVisibility(View.GONE);
            }

            addressId = getJsonObject.getString("TAKE_ID");
            addressName = getJsonObject.getString("TAKE_NAME");
            addressPhone = getJsonObject.getString("TAKE_MOBILE");
            orderNo = getJsonObject.getString("ORDER_CODE");
            // 收货地址
            addressArea = getJsonObject.getString("TAKE_PROVINCE") + " "
                    + getJsonObject.getString("TAKE_ADDRESS");
            mOrderDetailsAddressName.setText("Receiver  :  " + addressName);
            mOrderDetailsAddressPhone.setText("Contact Number  :  " + addressPhone);
            mOrderDetailsArea.setText("Address:  " + addressArea);
            // 商品信息
            shopName = getJsonObject.getString("COMPANY_NAME");
            mOrderDetailsShopName.setText(shopName);
            goodsPic = getJsonObject.getString("GOODS_IMG");
            if (goodsPic != null || goodsPic.equals("null")) {
                Glide.with(mBaseActivity).load(goodsPic.replaceAll("\'", "\"")).into(mOrderDetailsGoodsPic);
            }
            goodsName = getJsonObject.getString("GOODS_NAME");
            goodsPrice = getJsonObject.getString("GOODS_PRICE");
            mOrderDetailsGoodsName.setText(goodsName);
            mOrderDetailsGoodsPrice.setText("RM " + String.format("%.2f", Double.valueOf(goodsPrice)));
            // 订单公共信息
            if (getJsonObject.getString("ORDER_CODE") == null ||
                    getJsonObject.getString("ORDER_CODE").equals("null")) {
                mOrderDetailsOrderNumber.setText("null");
            } else {
                mOrderDetailsOrderNumber.setText(getJsonObject.getString("ORDER_CODE"));
            }

            orderTime = getJsonObject.getString("CREATE_TIME");
            if (orderTime == null || orderTime.equals("null")) {
                orderTime = "null";
                mOrderDetailsOrderTime.setText(orderTime);
            } else {
                mOrderDetailsOrderTime.setText(orderTime);
            }

            postPrice = getJsonObject.getString("AREA_PRICE");
            if (postPrice == null || postPrice.equals("null")) {
                postPrice = "null";
                mOrderDetailsOrderPostPrice.setText("RM " + postPrice);
            } else {
                mOrderDetailsOrderPostPrice.setText("RM " + String.format("%.2f", Double.valueOf(postPrice)));
            }

            totalPrice = getJsonObject.getString("TOTAL_PRICE");
            Log.e("TAG", "totalPrice: " + totalPrice);
            if (totalPrice == null || totalPrice.equals("null")) {
                mOrderDetailsOrderTotalPrice.setText("null");
            } else {
                mOrderDetailsOrderTotalPrice.setText("RM " + String.format("%.2f", Double.valueOf(totalPrice)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取订单详情内部类
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.order_details_url, params[0],
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
                    Log.e("TAG", "获取订单详情的返回结果：" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    loadPage(jsonObject);
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
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.order_details_back_rl, R.id.order_details_btn_pay})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.order_details_back_rl:
                finish();
                break;
            // 支付
            case R.id.order_details_btn_pay:
                Intent intent = new Intent(mBaseActivity, OrderBuyActivity.class);
                intent.putExtra("shopName", shopName);
                intent.putExtra("goodsPic", goodsPic);
                intent.putExtra("goodsName", goodsName);
                intent.putExtra("goodsPrice", goodsPrice);
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderNo", orderNo);
                intent.putExtra("addressId", addressId);
                intent.putExtra("addressName", addressName);
                intent.putExtra("addressPhone", addressPhone);
                intent.putExtra("addressArea", addressArea);
                intent.putExtra("orderTime", orderTime);
                intent.putExtra("postPrice", postPrice);
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);
                break;
        }
    }

}
