package com.enuos.jimat.activity.home.adapter;

import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enuos.jimat.activity.home.bean.HomeItem;

import java.util.List;

public class HomePageAdapter extends BaseQuickAdapter<HomeItem, BaseViewHolder> {
    public HomePageAdapter(int layoutResId, List<HomeItem> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, HomeItem item) {
        Log.e("aa","------------------");
    }
}
