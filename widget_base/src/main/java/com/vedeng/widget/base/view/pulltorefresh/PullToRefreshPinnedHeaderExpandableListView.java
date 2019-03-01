package com.vedeng.widget.base.view.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.vedeng.widget.base.view.PinnedHeaderExpandableListView;
import com.vedeng.widget.base.view.pulltorefresh.internal.EmptyViewMethodAccessor;

/**
 * ********************************************************
 * @文件名称：PullToRefreshPinnedHeaderExpandableListView.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月29日 上午9:44:57
 * @文件描述：
 * @修改历史：2016年1月29日创建初始版本
 *********************************************************
 */
public class PullToRefreshPinnedHeaderExpandableListView extends
		PullToRefreshAdapterViewBase<PinnedHeaderExpandableListView>
{

	public PullToRefreshPinnedHeaderExpandableListView(Context context)
	{
		super(context);
	}

	public PullToRefreshPinnedHeaderExpandableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullToRefreshPinnedHeaderExpandableListView(Context context, Mode mode)
	{
		super(context, mode);
	}

	public PullToRefreshPinnedHeaderExpandableListView(Context context, Mode mode, AnimationStyle animStyle)
	{
		super(context, mode, animStyle);
	}

	@Override
	public Orientation getPullToRefreshScrollDirection()
	{
		// TODO Auto-generated method stub
		return Orientation.VERTICAL;
	}

	@Override
	protected PinnedHeaderExpandableListView createRefreshableView(Context context, AttributeSet attrs)
	{
		final PinnedHeaderExpandableListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
		{
			lv = new InternalPinnedHeaderExpandableListViewSDK9(context, attrs);
		}
		else
		{
			lv = new InternalPinnedHeaderExpandableListView(context, attrs);
		}

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	class InternalPinnedHeaderExpandableListView extends PinnedHeaderExpandableListView implements
			EmptyViewMethodAccessor
	{

		public InternalPinnedHeaderExpandableListView(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView)
		{
			PullToRefreshPinnedHeaderExpandableListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView)
		{
			super.setEmptyView(emptyView);
		}
	}

	@TargetApi(9)
	final class InternalPinnedHeaderExpandableListViewSDK9 extends InternalPinnedHeaderExpandableListView
	{

		public InternalPinnedHeaderExpandableListViewSDK9(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
		{

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshPinnedHeaderExpandableListView.this, deltaX, scrollX, deltaY,
					scrollY, isTouchEvent);

			return returnValue;
		}
	}

}
