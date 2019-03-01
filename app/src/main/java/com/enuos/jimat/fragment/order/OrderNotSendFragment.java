package com.enuos.jimat.fragment.order;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enuos.jimat.R;
import com.enuos.jimat.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/9 2:08
 * @文件描述： 待发货订单
 * @修改历史： 2018/12/9 创建初始版本
 **********************************************************/
public class OrderNotSendFragment extends BaseFragment {

    @BindView(R.id.order_not_send_rv)
    RecyclerView mRecyclerView;
    private Unbinder unbinder;

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.order_not_send, null);
    }

    @Override
    public void initData() {
        super.initData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}