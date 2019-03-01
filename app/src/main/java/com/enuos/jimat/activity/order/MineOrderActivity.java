package com.enuos.jimat.activity.order;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.adapter.PagerSlideAdapter;
import com.enuos.jimat.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineOrderActivity extends BaseActivity {

    @BindView(R.id.mine_order_back)
    ImageView mBack;
    @BindView(R.id.mine_order_text_all)
    TextView mMineOrderTextAll;
    @BindView(R.id.mine_order_linear_all)
    LinearLayout mMineOrderLinearAll;
    @BindView(R.id.mine_order_text_not_pay)
    TextView mMineOrderTextNotPay;
    @BindView(R.id.mine_order_linear_not_pay)
    LinearLayout mMineOrderLinearNotPay;
    @BindView(R.id.mine_order_text_not_send)
    TextView mMineOrderTextNotSend;
    @BindView(R.id.mine_order_linear_not_send)
    LinearLayout mMineOrderLinearNotSend;
    @BindView(R.id.mine_order_text_not_receive)
    TextView mMineOrderTextNotReceive;
    @BindView(R.id.mine_order_linear_not_receive)
    LinearLayout mMineOrderLinearNotReceive;
    @BindView(R.id.mine_order_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.mine_order_tab_line)
    ImageView mTabLine;
    @BindView(R.id.mine_order_back_rl)
    RelativeLayout mMineOrderBackRl;

    private int page = 0;
    private int screenWidth;
    private List<BaseFragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order);
        ButterKnife.bind(this);

        initData(); // 初始化数据
        initWidth(); // 初始化滑动横条的宽度
        setListener(); // 设置监听器
    }

    /**
     * 初始化 Fragment 数据以及设置颜色
     */
    private void initData() {
        mFragmentList.add(new OrderAllFragment());
        mFragmentList.add(new OrderNotPayFragment());
//        mFragmentList.add(new OrderNotSendFragment());
//        mFragmentList.add(new OrderNotReceiveFragment());
        PagerSlideAdapter adapter = new PagerSlideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(page);
        switch (page) {
            case 0:
                mMineOrderTextAll.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                break;
            case 1:
                mMineOrderTextNotPay.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                break;
//            case 2:
//                mMineOrderTextNotSend.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                break;
//            case 3:
//                mMineOrderTextNotReceive.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                break;
        }
    }

    /**
     * 设置滑动监听器
     */
    private void setListener() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * This method will be invoked when the current page is scrolled, either as part
             * of a programmatically initiated smooth scroll or a user initiated touch scroll.
             *
             * @param position Position index of the first page currently being displayed.
             *                 Page position+1 will be visible if positionOffset is nonzero.
             * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
             * @param positionOffsetPixels Value in pixels indicating the offset from position.
             *                             这个参数的使用是为了在滑动页面时有文字下方横条的滑动效果
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin = screenWidth / 2 * position + positionOffsetPixels / 2;
                mTabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                // 在每次切换页面时重置 TextView 的颜色
                resetTextView();
                switch (position) {
                    case 0:
                        mMineOrderTextAll.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                        break;
                    case 1:
                        mMineOrderTextNotPay.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                        break;
//                    case 2:
//                        mMineOrderTextNotSend.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                        break;
//                    case 3:
//                        mMineOrderTextNotReceive.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 重置 Text 颜色
     */
    private void resetTextView() {
        mMineOrderTextAll.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_9A9A9A));
        mMineOrderTextNotPay.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_9A9A9A));
//        mMineOrderTextNotSend.setTextColor(Color.BLACK);
//        mMineOrderTextNotReceive.setTextColor(Color.BLACK);
    }

    /**
     * 初始化滑动横条的宽度
     */
    private void initWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width = screenWidth / 2;
        mTabLine.setLayoutParams(lp);
    }

    /**
     * LinearLayout 点击事件
     */
    @OnClick({R.id.mine_order_linear_all, R.id.mine_order_linear_not_pay})
    public void onLinearClick(LinearLayout linearLayout) {
        switch (linearLayout.getId()) {
            case R.id.mine_order_linear_all:
                mViewPager.setCurrentItem(0);
                resetTextView();
                mMineOrderTextAll.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                break;
            case R.id.mine_order_linear_not_pay:
                mViewPager.setCurrentItem(1);
                resetTextView();
                mMineOrderTextNotPay.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainRed));
                break;
//            case R.id.mine_order_linear_not_send:
//                mViewPager.setCurrentItem(2);
//                resetTextView();
//                mMineOrderTextNotSend.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                break;
//            case R.id.mine_order_linear_not_receive:
//                mViewPager.setCurrentItem(3);
//                resetTextView();
//                mMineOrderTextNotReceive.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
//                break;
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_order_back_rl})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_order_back_rl:
                finish();
                break;
        }
    }
}
