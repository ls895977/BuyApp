package com.enuos.jimat.activity.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.goods.GoodsBuyActivity;
import com.enuos.jimat.activity.goods.GoodsDetailsActivity;
import com.enuos.jimat.adapter.HomeAdapter;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.home_rv)
    RecyclerView mHomeRv;
    @BindView(R.id.home_swipe_refresh)
    SwipeRefreshLayout mSwipe;

    private JSONArray mJSONArray;
    private User mUser;
    private String goodsId, shopName, goodsPic, goodsName, goodsPrice, clientTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 进入界面之后先获取信息
        mSwipe.setEnabled(true);
        setSwipe();
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_LOGIN)) {
            refresh();
        }
    }

    /**
     * 设置刷新
     */
    private void setSwipe() {
        mSwipe.setColorSchemeColors(ContextCompat.getColor(mBaseActivity, R.color.blue_btn_bg_color));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    /**
     * 判断是否登录
     */
    public boolean isLogin() {
        // 取出信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        if (user != null && !user.userAccount.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
        mSwipe.setRefreshing(true);

        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("pageNum", "");
        params.put("pageSize", "");
        params.put("token", userToken);
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
        mSwipe.setRefreshing(false);
    }

    /**
     * 加载界面
     */
    private void loadPage(JSONObject jsonObject) {
        try {
            String stringData = jsonObject.getString("data");
            JSONObject jsonObjectData = new JSONObject(stringData);
            String jsonArrayString = jsonObjectData.getString("list");
            mJSONArray = new JSONArray(jsonArrayString.replaceAll("\t", ""));
            HomeAdapter adapter = new HomeAdapter(mBaseActivity, mJSONArray);
            adapter.setMyClickListener(new HomeAdapter.MyClickListener() {
                // 产品详情的点击事件
                @Override
                public void details(int position) {
                    try {
                        JSONObject jsonObject = mJSONArray.getJSONObject(position);
                        String goodsId = jsonObject.getString("ID");
                        Intent intentInfo = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                        intentInfo.putExtra("goodsId", goodsId);
                        startActivity(intentInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 设置提醒的点击事件
                @Override
                public void clock(int position) {
                    try {
                        JSONObject jsonObject = mJSONArray.getJSONObject(position);
                        String addressId = jsonObject.getString("ID");
                        ToastUtils.show(mBaseActivity, "点击了提醒");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 点击购买的点击事件
                @Override
                public void buy(int position) {
                    try {
                        if (isLogin()) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                            // 获取当前时间 2018-12-25 12:12:12
                            Date date = new Date(System.currentTimeMillis());
                            String dateNow = simpleDateFormat.format(date);
                            String strYearNow = dateNow.substring(0, 4); // 现在的年份
                            String strMonthNow = dateNow.substring(5, 7); // 现在的月份
                            String strDayNow = dateNow.substring(8, 10); // 现在的天
                            String strHourNow = dateNow.substring(11, 13); // 现在的小时
                            String strMinuteNow = dateNow.substring(14, 16); // 现在的分钟
                            String strSecondNow = dateNow.substring(17, 19); // 现在的秒
                            clientTime = strYearNow + "-" + strMonthNow + "-" + strDayNow
                                    + " " + strHourNow + ":" + strMinuteNow + ":" + strSecondNow;

                            JSONObject jsonObject = mJSONArray.getJSONObject(position);
                            goodsId = jsonObject.getString("ID");
                            shopName = jsonObject.getString("COMPANY_NAME");
                            goodsPic = jsonObject.getString("IMAGE_URL");
                            goodsName = jsonObject.getString("GOODS_NAME");

                            String startPrice = jsonObject.getString("GOODS_START_PRICE");
                            String downType = jsonObject.getString("GOODS_DOWN_TYPE");
                            String downValue = jsonObject.getString("GOODS_DOWN_VALUE");
                            if (downType.equals("1")) { // 按照金额降价
                                double newPrice = Double.valueOf(startPrice) - Double.valueOf(downValue);
                                goodsPrice = String.valueOf(newPrice);
                            } else {
                                double newPrice = Double.valueOf(startPrice) - (Double.valueOf(startPrice) * Double.valueOf(downValue));
                                goodsPrice = String.valueOf(newPrice);
                            }

                            // 获取 User 信息
                            IDataStorage dataStorage = DataStorageFactory.getInstance(
                                    getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                            mUser = dataStorage.load(User.class, "User");
                            HashMap<String, String> params = new HashMap<>();
                            params.put("goodsId", goodsId);
                            params.put("memberId", mUser.userID);
                            params.put("clientTime", clientTime);
                            params.put("clientPrice", goodsPrice);
                            OrderTask mOrderTask = new OrderTask();
                            mOrderTask.execute(params);
                        } else {
                            startActivity(new Intent(mBaseActivity, LoginNewActivity.class));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mHomeRv.setAdapter(adapter);
            mHomeRv.setLayoutManager(new LinearLayoutManager(
                    mBaseActivity, LinearLayoutManager.VERTICAL, false));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部类
     * 收货地址列表
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.goods_list_url, params[0],
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
                    Log.e("TAG", "首页的返回结果：" + data.toString());
                    loadPage(data);
                    mSwipe.setRefreshing(false);
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
     * 生成订单
     */
    private class OrderTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.order_add_url, params[0],
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
                    Log.e("TAG", "生成订单的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String orderId = jsonObjectData.getString("ID");
                    Intent intent = new Intent(mBaseActivity, GoodsBuyActivity.class);
                    intent.putExtra("shopName", shopName);
                    intent.putExtra("goodsPic", goodsPic);
                    intent.putExtra("goodsName", goodsName);
                    intent.putExtra("goodsPrice", goodsPrice);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("orderTime", clientTime);
                    startActivity(intent);
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

}
