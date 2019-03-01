package com.vedeng.widget.base.view.circulatebanner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vedeng.widget.base.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * @文件名称：CirculateBanner.java
 * @文件作者：xiongjiangwei
 * @创建时间：2015年9月11日 上午11:40:44
 * @文件描述：页面翻转控件，极方便的广告栏（支持无限循环，自动翻页，翻页特效）
 * @修改历史：2015年9月11日创建初始版本
 **********************************************************/
public class CirculateBanner<T> extends LinearLayout
{
	@SuppressWarnings("rawtypes")
	private CBViewHolderCreator holderCreator;
	@SuppressWarnings("rawtypes")
	private CBPageAdapter pageAdapter;
	private List<T> mDatas;
	private int[] page_indicatorId;
	private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
	private CBPageChangeListener pageChangeListener;
	private CBLoopViewPager viewPager;
	private ViewGroup loPageTurningPoint;
	private long autoTurningTime;
	private boolean turning;
	private boolean canTurn = false;
	private boolean manualPageable = true;

	public enum PageIndicatorAlign
	{
		ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
	}

	public enum Transformer
	{
		/**
		 * 默认效果
		 */
		DefaultTransformer("DefaultTransformer"),
		/**
		 * 水平向右滑动效果（默认效果）
		 */
		AccordionTransformer("AccordionTransformer"), BackgroundToForegroundTransformer(
				"BackgroundToForegroundTransformer"), CubeInTransformer("CubeInTransformer"), CubeOutTransformer(
				"CubeOutTransformer"), DepthPageTransformer("DepthPageTransformer"), FlipHorizontalTransformer(
				"FlipHorizontalTransformer"), FlipVerticalTransformer("FlipVerticalTransformer"), ForegroundToBackgroundTransformer(
				"ForegroundToBackgroundTransformer"), RotateDownTransformer("RotateDownTransformer"), RotateUpTransformer(
				"RotateUpTransformer"), StackTransformer("StackTransformer"), TabletTransformer("TabletTransformer"), ZoomInTransformer(
				"ZoomInTransformer"), ZoomOutSlideTransformer("ZoomOutSlideTransformer"), ZoomOutTranformer(
				"ZoomOutTranformer");

		private final String className;

		private Transformer(String className)
		{
			this.className = className;
		}

		public String getClassName()
		{
			return className;
		}
	}

	private Handler timeHandler = new Handler();
	private Runnable adSwitchTask = new Runnable()
	{
		@Override
		public void run()
		{
			if (viewPager != null && turning)
			{
				int page = viewPager.getCurrentItem() + 1;
				viewPager.setCurrentItem(page);
				timeHandler.postDelayed(adSwitchTask, autoTurningTime);
			}
		}
	};

	public CirculateBanner(Context context)
	{
		this(context, null);
	}

	public CirculateBanner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ViewPager getViewPager()
	{
		return viewPager;
	}

	private void init(Context context)
	{
		View hView = LayoutInflater.from(context).inflate(R.layout.circulate_viewpager, this, true);
		viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
		loPageTurningPoint = (ViewGroup) hView.findViewById(R.id.loPageTurningPoint);
		initViewPagerScroll();
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public CirculateBanner<T> setPageAdapter(CBViewHolderCreator holderCreator, List<T> datas)
	{
		this.mDatas = datas;
		this.holderCreator = holderCreator;
		pageAdapter = new CBPageAdapter(this.holderCreator, mDatas);
		viewPager.setAdapter(pageAdapter);
		viewPager.setBoundaryCaching(true);
		if (page_indicatorId != null)
			setPageIndicator(page_indicatorId);
		return this;
	}

	/**
	 * 设置底部指示器是否可见
	 *
	 * @param visible
	 */
	public CirculateBanner<T> setPointViewVisible(boolean visible)
	{
		loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
		return this;
	}

	/**
	 * 底部指示器资源图片
	 *
	 * @param page_indicatorId
	 */
	public CirculateBanner<T> setPageIndicator(int[] page_indicatorId)
	{
		loPageTurningPoint.removeAllViews();
		mPointViews.clear();
		this.page_indicatorId = page_indicatorId;
		if (mDatas == null)
			return this;
		for (int count = 0; count < mDatas.size(); count++)
		{
			ImageView pointView = new ImageView(getContext());
			pointView.setPadding(5, 0, 5, 0);
			if (mPointViews.isEmpty())
				pointView.setImageResource(page_indicatorId[1]);
			else pointView.setImageResource(page_indicatorId[0]);
			mPointViews.add(pointView);
			loPageTurningPoint.addView(pointView);
		}
		pageChangeListener = new CBPageChangeListener(mPointViews, page_indicatorId);
		viewPager.setOnPageChangeListener(pageChangeListener);

		return this;
	}

	/**
	 * 指示器的方向
	 * @param align  三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
	 * @return
	 */
	public CirculateBanner<T> setPageIndicatorAlign(PageIndicatorAlign align)
	{
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
		loPageTurningPoint.setLayoutParams(layoutParams);
		return this;
	}

	/***
	 * 是否开启了翻页
	 * @return
	 */
	public boolean isTurning()
	{
		return turning;
	}

	/***
	 * 开始翻页
	 * @param autoTurningTime 自动翻页时间
	 * @return
	 */
	public CirculateBanner<T> startTurning(long autoTurningTime)
	{
		// 如果是正在翻页的话先停掉
		if (turning)
		{
			stopTurning();
		}
		// 设置可以翻页并开启翻页
		canTurn = true;
		this.autoTurningTime = autoTurningTime;
		turning = true;
		timeHandler.postDelayed(adSwitchTask, autoTurningTime);
		return this;
	}

	public void stopTurning()
	{
		turning = false;
		timeHandler.removeCallbacks(adSwitchTask);
	}

	/**
	 * 自定义翻页动画效果
	 *
	 * @param transformer
	 * @return
	 */
	public CirculateBanner<T> setPageTransformer(PageTransformer transformer)
	{
		viewPager.setPageTransformer(true, transformer);
		return this;
	}

	/**
	 * 自定义翻页动画效果，使用已存在的效果
	 *
	 * @param transformer
	 * @return
	 */
	public CirculateBanner<T> setPageTransformer(Transformer transformer)
	{
		try
		{
			viewPager.setPageTransformer(
					true,
					(PageTransformer) Class.forName(
							"com.vedeng.widget.base.view.circulatebanner.transforms." + transformer.getClassName())
							.newInstance());
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 设置ViewPager的滑动速度
	 * */
	private void initViewPagerScroll()
	{
		try
		{
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			ViewPagerScroller scroller = new ViewPagerScroller(viewPager.getContext());
			// scroller.setScrollDuration(1500);
			mScroller.set(viewPager, scroller);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isManualPageable()
	{
		return manualPageable;
	}

	public void setManualPageable(boolean manualPageable)
	{
		this.manualPageable = manualPageable;
	}

	// 触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{

		if (ev.getAction() == MotionEvent.ACTION_UP)
		{
			// 开始翻页
			if (canTurn)
				startTurning(autoTurningTime);
		}
		else if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			// 停止翻页
			if (canTurn)
				stopTurning();
		}
		if (manualPageable)
			return super.dispatchTouchEvent(ev);
		else return true;
	}

}
