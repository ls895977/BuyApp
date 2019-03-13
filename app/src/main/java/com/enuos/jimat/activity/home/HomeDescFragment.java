package com.enuos.jimat.activity.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.goods.GoodsDetailsActivity;
import com.enuos.jimat.adapter.HomeDescAdapter;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
public class HomeDescFragment extends BaseFragment {

    @BindView(R.id.home_desc_rv)
    RecyclerView homeDescRv;
    private Unbinder unbinder;

    private User mUser;
    private JSONArray mJSONArray;

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.fragment_home_desc, null);
    }

    @Override
    public void initData() {
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                mContext.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        // 刷新数据
        refresh();

        super.initData();
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
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
        params.put("pageNum", "1");
        params.put("pageSize", "20");
        params.put("token", userToken);
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(JSONObject jsonObject) {
        try {
            JSONObject jsonObjectData = new JSONObject(jsonObject.getString("data"));
            String jsonArrayString = jsonObjectData.getString("goodsSaleList");

            JSONObject jsonObjectDesc = new JSONObject(jsonArrayString);
            mJSONArray = new JSONArray(jsonObjectDesc.getString("list"));

            HomeDescAdapter adapter = new HomeDescAdapter(mContext, mJSONArray);
            adapter.setMyClickListener(new HomeDescAdapter.MyClickListener() {
                // 选择
                @Override
                public void details(int position) {
                    try {
                        JSONObject jsonObject = mJSONArray.getJSONObject(position);
                        Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                        intent.putExtra("goodsId", jsonObject.getString("ID"));
                        intent.putExtra("goodsType", "base");
                        intent.putExtra("type", "data");
                        intent.putExtra("value", "0");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            homeDescRv.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            linearLayoutManager.setAutoMeasureEnabled(true);
            homeDescRv.setLayoutManager(linearLayoutManager);
            homeDescRv.setNestedScrollingEnabled(false);
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
                return HttpUtils.postHttp(mContext,
                        UrlConfig.base_url + UrlConfig.home_new_url, params[0],
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
            Log.e("aa","-------获得数据--111-"+result[2]);
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "首页降价的返回结果：" + data.toString());
                    loadPage(data);
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mContext, result[1].toString());
            }

        }
    }

}