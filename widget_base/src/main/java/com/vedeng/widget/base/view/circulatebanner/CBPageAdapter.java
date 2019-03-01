package com.vedeng.widget.base.view.circulatebanner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.vedeng.widget.base.R;
import com.vedeng.widget.base.view.circulatebanner.salvage.RecyclingPagerAdapter;

import java.util.List;

public class CBPageAdapter<T> extends RecyclingPagerAdapter
{
	protected List<T> mDatas;
	protected CBViewHolderCreator<T> holderCreator;

	public CBPageAdapter(CBViewHolderCreator<T> holderCreator, List<T> datas)
	{
		this.holderCreator = holderCreator;
		this.mDatas = datas;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View view, ViewGroup container)
	{
		Holder<T> holder = null;
		if (view == null)
		{
			holder = (Holder<T>) holderCreator.createHolder();
			view = holder.createView(container.getContext());
			view.setTag(R.id.CBPageAdapter_View, holder);
		}
		else
		{
			holder = (Holder<T>) view.getTag(R.id.CBPageAdapter_View);
		}
		if (mDatas != null && !mDatas.isEmpty())
			holder.UpdateUI(container.getContext(), position, mDatas.get(position));
		return view;
	}

	@Override
	public int getCount()
	{
		if (mDatas == null)
			return 0;
		return mDatas.size();
	}

	public interface Holder<T>
	{
		View createView(Context context);

		void UpdateUI(Context context, int position, T data);
	}
}
