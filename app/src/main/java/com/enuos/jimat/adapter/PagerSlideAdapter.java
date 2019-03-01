package com.enuos.jimat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.enuos.jimat.fragment.BaseFragment;

import java.util.List;

/**********************************************************
 * @文件作者： niezhongze
 * @创建时间： 2018/11/23 20:18
 * @文件描述：
 * @修改历史： 2018/11/23 创建初始版本
 **********************************************************/
public class PagerSlideAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragmentList;

    public PagerSlideAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
