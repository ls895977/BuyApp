package com.vedeng.widget.base.view.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.vedeng.widget.base.R;

/**********************************************************
 * @文件名称：PullToRefreshRecyclerView.java
 * @文件作者：聂中泽
 * @创建时间：2018年10月17日 9:12
 * @文件描述：增加recyclerview 支持，来源于官方库
 * @修改历史：2018年10月17日创建初始版本
 **********************************************************/
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    private final int SCROLL_UP = -1;
    private final int SCROLL_DOWN = 1;

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView = new RecyclerView(context, attrs);
        recyclerView.setId(R.id.recyclerview);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0)
            return true;
        return !mRefreshableView.canScrollVertically(SCROLL_UP);

    }

    @Override
    protected boolean isReadyForPullEnd() {
        return !mRefreshableView.canScrollVertically(SCROLL_DOWN);
    }
}
