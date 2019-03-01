package com.enuos.jimat.activity.address;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.module.AddressListItem;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vedeng.widget.base.view.pulltorefresh.PullToRefreshBase;
import com.vedeng.widget.base.view.pulltorefresh.PullToRefreshRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineAddressActivity extends BaseActivity {

    @BindView(R.id.mine_address_back)
    ImageView nBack;
    @BindView(R.id.mine_address_btn_add)
    TextView mMineAddressBtnAdd;
    @BindView(R.id.address_rv)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @BindView(R.id.mine_address_result_layout)
    RelativeLayout mMineAddressResultLayout;
    @BindView(R.id.mine_address_back_rl)
    RelativeLayout mMineAddressBackRl;

    protected RecyclerView mMineAddressRv;
    private int pageIndex = 1;
    private boolean isRefresh = false;
    private AddressAdapter adapter;
    private User mUser;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_ADDRESS)) {
            isRefresh = true;
            pageIndex = 1;
            refresh();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_address);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setOnRefreshListener(refreshListener);
        mMineAddressRv = pullToRefreshRecyclerView.getRefreshableView();
        mMineAddressRv.setLayoutManager(new LinearLayoutManager(
                mBaseActivity, LinearLayoutManager.VERTICAL, false));

        // 请求网络数据
        if (adapter == null) {
            refresh();
        }
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            pageIndex = 1;
            isRefresh = true;
            refresh();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            pageIndex++;
            refresh();
        }
    };

    private void refreshComplete() {
        if (pullToRefreshRecyclerView != null) {
            pullToRefreshRecyclerView.onRefreshComplete();
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_address_back_rl, R.id.mine_address_btn_add})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_address_back_rl:
                finish();
                break;
            // 添加新地址
            case R.id.mine_address_btn_add:
                startActivity(new Intent(mBaseActivity, AddressAddActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 空布局
     */
    private void setEmptyView(Boolean isEmpty) {
        ConstraintLayout empty = mBaseActivity.findViewById(R.id.item_address_empty_all);
        if (isEmpty) {
            empty.setVisibility(View.VISIBLE);
            mMineAddressResultLayout.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            mMineAddressResultLayout.setVisibility(View.VISIBLE);
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
        params.put("pageNum", String.valueOf(pageIndex));
        params.put("pageSize", "10");
        AddressTask mAddressTask = new AddressTask();
        mAddressTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(final ArrayList<AddressListItem> addressList) {
        adapter = new AddressAdapter(mBaseActivity, addressList);
        adapter.setMyClickListener(new AddressAdapter.MyClickListener() {
            // 选择
            @Override
            public void select(int position) {
                    /*try {
                        JSONObject address = mAdressArray.getJSONObject(position);
                        String addressId = address.getString("ID");
                        String addressName = address.getString("TAKE_NAME");
                        String addressTel = address.getString("TAKE_MOBILE");
                        String addressArea = address.getString("TAKE_PROVINCE") +
                                address.getString("TAKE_CITY") +
                                address.getString("TAKE_AREA") +
                                address.getString("TAKE_ADDRESS");
                        EventBus.getDefault().post(new AddressEvent("address", addressId, addressName, addressTel, addressArea));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
            }

            // 修改
            @Override
            public void edit(int position) {
                String addressArea = addressList.get(position).TAKE_PROVINCE;
//                        addressList.get(position).TAKE_CITY +
//                        addressList.get(position).TAKE_AREA;
                // 跳转
                Intent intentInfo = new Intent(mBaseActivity, AddressEditActivity.class);
                intentInfo.putExtra("addressId", addressList.get(position).ID);
                intentInfo.putExtra("addressName", addressList.get(position).TAKE_NAME);
                intentInfo.putExtra("addressTel", addressList.get(position).TAKE_MOBILE);
                intentInfo.putExtra("addressArea", addressArea);
                intentInfo.putExtra("addressAreaDetails", addressList.get(position).TAKE_ADDRESS);
                intentInfo.putExtra("isDefault", addressList.get(position).IS_DEFAULT);
                intentInfo.putExtra("province", addressList.get(position).TAKE_PROVINCE);
//                intentInfo.putExtra("city", addressList.get(position).TAKE_CITY);
//                intentInfo.putExtra("area", addressList.get(position).TAKE_AREA);
                startActivity(intentInfo);
            }
        });
        mMineAddressRv.setAdapter(adapter);
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
                    String jsonArrayString = jsonObjectData.getString("list");
                    int maxCount = Integer.parseInt(jsonObjectData.getString("maxCount"));
                    Gson gson = new Gson();
                    ArrayList<AddressListItem> addressList = gson.fromJson(jsonArrayString,
                            new TypeToken<List<AddressListItem>>() {
                            }.getType());
                    refreshComplete();
                    if (stringData != null) {
                        if (adapter == null || isRefresh) {
                            if (maxCount == 0) {
                                setEmptyView(true);
                            } else {
                                setEmptyView(false);
                                loadPage(addressList);
                            }
                        } else {
                            setEmptyView(false);
                            adapter.addDataList(addressList);
                        }
                    }
                    isRefresh = false;
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                isRefresh = false;
                refreshComplete();

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
    }
}
