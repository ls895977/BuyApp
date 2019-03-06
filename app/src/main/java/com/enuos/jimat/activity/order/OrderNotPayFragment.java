package com.enuos.jimat.activity.order;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.enuos.jimat.R;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.User;
import com.enuos.jimat.module.OrderListItem;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vedeng.widget.base.view.pulltorefresh.PullToRefreshBase;
import com.vedeng.widget.base.view.pulltorefresh.PullToRefreshRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/9 2:18
 * @文件描述： 待付款订单
 * @修改历史： 2018/12/9 创建初始版本
 **********************************************************/
public class OrderNotPayFragment extends BaseFragment {

    @BindView(R.id.order_not_pay_rv)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @BindView(R.id.order_not_pay_result_layout)
    RelativeLayout mOrderNotPayResultLayout;
    private Unbinder unbinder;

    private User mUser;
    protected RecyclerView mMineOrderNotPayRv;
    private int pageIndex = 1;
    private boolean isRefresh = false;
    private OrderNotPayAdapter adapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_ORDER)) {
            refresh();
        }
    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.order_not_pay, null);
    }

    @Override
    public void initData() {
        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                mContext.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setOnRefreshListener(refreshListener);
        mMineOrderNotPayRv = pullToRefreshRecyclerView.getRefreshableView();
        mMineOrderNotPayRv.setLayoutManager(new LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL, false));

        // 请求网络数据
        if (adapter == null) {
            refresh();
        }
        super.initData();
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
        ConstraintLayout empty = getActivity().findViewById(R.id.item_order_pay_empty_all);
        if (isEmpty) {
            empty.setVisibility(View.VISIBLE);
            mOrderNotPayResultLayout.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            mOrderNotPayResultLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
        HashMap<String, String> params = new HashMap<>();
        params.put("memberId", mUser.userID);
        params.put("token", mUser.token);
        params.put("payState", "1");
        params.put("pageNum", String.valueOf(pageIndex));
        params.put("pageSize", "10");
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(final ArrayList<OrderListItem> orderList) {
        adapter = new OrderNotPayAdapter(mContext, orderList);
        adapter.setMyClickListener(new OrderNotPayAdapter.MyClickListener() {
            // 选择
            @Override
            public void select(int position) {
                Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderList.get(position).ID);
                startActivity(intent);
            }
        });
        mMineOrderNotPayRv.setAdapter(adapter);
    }

    /**
     * 内部类
     * 订单列表
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mContext,
                        UrlConfig.base_url + UrlConfig.order_list_url, params[0],
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
                    Log.e("TAG", "订单列表的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String jsonArrayString = jsonObjectData.getString("list");
                    JSONArray mJSONArray = new JSONArray(jsonArrayString);
                    // 所有的订单数量
                    int maxCount = Integer.parseInt(jsonObjectData.getString("maxCount"));
                    Gson gson = new Gson();
                    ArrayList<OrderListItem> orderList = gson.fromJson(jsonArrayString,
                            new TypeToken<List<OrderListItem>>() {
                            }.getType());
                    refreshComplete();
                    if (stringData != null) {
                        if (adapter == null || isRefresh) {
                            // 待付款订单 PAY_STATE="1"
                            if (maxCount == 0) {
                                setEmptyView(true);
                            } else {
                                setEmptyView(false);
                                loadPage(orderList);
                            }
                        } else {
                            setEmptyView(false);
                            adapter.addDataList(orderList);
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
                ToastUtils.show(mContext, msgError);
//                if (msgError.contains("Your account is being logged") || msgError.contains("account")) {
//                    // 退出 APP 本身的账号
//                    IDataStorage dataStorage = DataStorageFactory.getInstance(
//                            getActivity(), DataStorageFactory.TYPE_DATABASE);
//                    User user = new User();
//                    user.userAccount = "";
//                    user.isLogin = "false";
//                    user.token = "";
//                    dataStorage.storeOrUpdate(user, "User");
//                    EventBus.getDefault().post(EventConfig.EVENT_EXIT);
//                    // 退出环信账号
//                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 登录环信服务器退出登录
//                            EMClient.getInstance().logout(false, new EMCallBack() {
//                                @Override
//                                public void onSuccess() {
//                                    // 关闭 DBHelper
//                                    // Model.getInstance().getDbManager().close();
//                                    Log.e("789", "环信账号退出成功");
//                                }
//
//                                @Override
//                                public void onError(int i, final String s) {
//                                }
//
//                                @Override
//                                public void onProgress(int i, String s) {
//                                }
//                            });
//                        }
//                    });
//                    ToastUtils.show( getActivity(), "Please Login");
//                    Intent intentG = new Intent( getActivity(), LoginNewActivity.class);
//                    intentG.putExtra("from", "mine");
//                    intentG.putExtra("goodsId", "");
//                    intentG.putExtra("goodsType", "");
//                    intentG.putExtra("homeTime", "0");
//                    startActivity(intentG);
//                    getActivity().finish();
//                }
                // 同一账户多个终端登录
            }

        }
    }

}