package com.vedeng.widget.base.view.circulatebanner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**********************************************************
 * @文件名称：ViewPagerScroller.java
 * @文件作者：xiongjiangwei
 * @创建时间：2015年9月11日 上午11:39:46
 * @文件描述：自定义ViewPager滑动间隔时间
 * @修改历史：2015年9月11日创建初始版本
 **********************************************************/
public class ViewPagerScroller extends Scroller
{
	private int mScrollDuration = 1200;

	public ViewPagerScroller(Context context)
	{
		super(context);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator)
	{
		super(context, interpolator);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel)
	{
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration)
	{
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy)
	{
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	public int getScrollDuration()
	{
		return mScrollDuration;
	}

	public void setScrollDuration(int scrollDuration)
	{
		this.mScrollDuration = scrollDuration;
	}

}