package com.enuos.jimat.activity.goods;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.adapter.BuyNewAdapter;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.module.BuyListItem;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
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
 * @创建时间： 2018/12/9 2:08
 * @文件描述： 全部订单
 * @修改历史： 2018/12/9 创建初始版本
 **********************************************************/
public class DetailsBuyFragment extends BaseFragment {

    @BindView(R.id.details_buy_rv)
    RecyclerView mBuyRv;
    @BindView(R.id.details_history_price_rv_top)
    LinearLayout mDetailsBuyResultLayout;
    private Unbinder unbinder;

    private User                mUser;
    private int                 pageIndex = 1;
    private boolean             isRefresh = false;
    public  BuyNewAdapter       adapter;
    private String              goodsId;
    private LinearLayoutManager mManager;

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.fragment_details_buy, null);
    }

    @Override
    public void initData() {
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                mContext.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        TextView stringDetails = getActivity().findViewById(R.id.goods_details_goods_id);
        TextView stringType = getActivity().findViewById(R.id.goods_details_goods_type);
        goodsId = stringDetails.getText().toString();
        mManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBuyRv.setLayoutManager(mManager);

        // 代售商品无购买记录和价格变动记录 首页和正在降价有
        if (stringType.getText().toString().equals("base")) {
            // 请求网络数据
            if (adapter == null) {
                refresh();
            }
        } else {
            setEmptyView(true);
            isRefresh = false;
        }

        super.initData();
    }

    public void moveFirst() {
        if (mManager != null)
            mManager.scrollToPositionWithOffset(mManager.findFirstVisibleItemPosition(), 0);
    }

    /**
     * 空布局
     */
    private void setEmptyView(Boolean isEmpty) {
        ConstraintLayout empty = getActivity().findViewById(R.id.item_details_buy_empty_all);
        if (isEmpty) {
            empty.setVisibility(View.VISIBLE);
            mDetailsBuyResultLayout.setVisibility(View.GONE);
            mBuyRv.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            mDetailsBuyResultLayout.setVisibility(View.VISIBLE);
            mBuyRv.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        ((GoodsDetailsActivity) getActivity()).mViewPager.setObjectForPosition(rootView, 3);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                mContext.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", goodsId);
        params.put("payState", "");
        params.put("pageNum", String.valueOf(pageIndex));
        params.put("pageSize", "10");
        params.put("token", userToken);
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(final ArrayList<BuyListItem> buyList) {
        adapter = new BuyNewAdapter(mContext, buyList);
        adapter.setMyClickListener(new BuyNewAdapter.MyClickListener() {
            // 选择
            @Override
            public void select(int position) {

            }
        });
        mBuyRv.setAdapter(adapter);
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
                        UrlConfig.base_url + UrlConfig.goods_sales_record_url, params[0],
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
                    Log.e("TAG", "购买记录的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String jsonArrayString = jsonObjectData.getString("list");
                    int maxCount = Integer.parseInt(jsonObjectData.getString("maxCount"));
                    Gson gson = new Gson();
                    ArrayList<BuyListItem> buyList = gson.fromJson(jsonArrayString,
                            new TypeToken<List<BuyListItem>>() {
                            }.getType());
                    if (stringData != null) {
                        if (adapter == null || isRefresh) {
                            if (maxCount == 0) {
                                setEmptyView(true);
                            } else {
                                setEmptyView(false);
                                loadPage(buyList);
                            }
                        } else {
                            setEmptyView(false);
                            adapter.addDataList(buyList);
                        }
                    }
                    isRefresh = false;
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                isRefresh = false;

                // 同一账户多个终端登录
                String msgError = result[1].toString();
                ToastUtils.show(mContext, msgError);
                if (msgError.contains("Your account is being logged") || msgError.contains("account")) {
                    // 退出 APP 本身的账号
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getActivity().getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
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
                    getActivity().finish();
                }
                // 同一账户多个终端登录
            }

        }
    }

}