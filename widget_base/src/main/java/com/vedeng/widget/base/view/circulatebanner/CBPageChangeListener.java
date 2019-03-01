package com.vedeng.widget.base.view.circulatebanner;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：CBPageChangeListener.java
 * @文件作者：xiongjiangwei
 * @创建时间：2015年9月11日 上午11:39:04
 * @文件描述：翻页指示器适配器
 * @修改历史：2015年9月11日创建初始版本
 **********************************************************/
public class CBPageChangeListener implements ViewPager.OnPageChangeListener
{
	private ArrayList<ImageView> pointViews;
	private int[] page_indicatorId;

	public CBPageChangeListener(ArrayList<ImageView> pointViews, int page_indicatorId[])
	{
		this.pointViews = pointViews;
		this.page_indicatorId = page_indicatorId;
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{

	}

	@Override
	public void onPageSelected(int index)
	{
		for (int i = 0; i < pointViews.size(); i++)
		{
			pointViews.get(index).setImageResource(page_indicatorId[1]);
			if (index != i)
			{
				pointViews.get(i).setImageResource(page_indicatorId[0]);
			}
		}
	}
}
