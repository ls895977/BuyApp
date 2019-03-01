package com.enuos.jimat.activity.money;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.msg.MoneyDetailsAdapter;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.module.MoneyDetailsListItem;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineMoneyDetailsActivity extends BaseActivity {

    @BindView(R.id.mine_money_details_back)
    ImageView mBack;
    @BindView(R.id.mine_money_details_rv)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @BindView(R.id.mine_money_details_result_layout)
    RelativeLayout mMineMoneyResultLayout;
    @BindView(R.id.mine_money_details_back_rl)
    RelativeLayout mMineMoneyDetailsBackRl;

    protected RecyclerView mMineMoneyDetailsRv;
    private int pageIndex = 1;
    private boolean isRefresh = false;
    private MoneyDetailsAdapter adapter;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_money_details);
        ButterKnife.bind(this);

        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setOnRefreshListener(refreshListener);
        mMineMoneyDetailsRv = pullToRefreshRecyclerView.getRefreshableView();
        mMineMoneyDetailsRv.setLayoutManager(new LinearLayoutManager(
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
     * 空布局
     */
    private void setEmptyView(Boolean isEmpty) {
        ConstraintLayout empty = mBaseActivity.findViewById(R.id.item_mine_money_details_empty_all);
        if (isEmpty) {
            empty.setVisibility(View.VISIBLE);
            mMineMoneyResultLayout.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            mMineMoneyResultLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 点击事件
     */
    @OnClick({R.id.mine_money_details_back_rl})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_money_details_back_rl:
                finish();
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
        params.put("pageNum", String.valueOf(pageIndex));
        params.put("pageSize", "10");
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(final ArrayList<MoneyDetailsListItem> moneyDetailsList) {
        adapter = new MoneyDetailsAdapter(mBaseActivity, moneyDetailsList);
        mMineMoneyDetailsRv.setAdapter(adapter);
    }

    /**
     * 内部类
     * 余额明细列表
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.balance_details_url, params[0],
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
                    Log.e("TAG", "余额明细列表的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String jsonArrayString = jsonObjectData.getString("list");
                    int maxCount = Integer.parseInt(jsonObjectData.getString("maxCount"));
                    Gson gson = new Gson();
                    ArrayList<MoneyDetailsListItem> moneyDetailsList = gson.fromJson(jsonArrayString,
                            new TypeToken<List<MoneyDetailsListItem>>() {
                            }.getType());
                    refreshComplete();
                    if (stringData != null) {
                        if (adapter == null || isRefresh) {
                            if (maxCount == 0) {
                                setEmptyView(true);
                            } else {
                                setEmptyView(false);
                                loadPage(moneyDetailsList);
                            }
                        } else {
                            setEmptyView(false);
                            adapter.addDataList(moneyDetailsList);
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
