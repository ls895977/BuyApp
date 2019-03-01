package com.enuos.jimat.activity.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.WebActivity;
import com.enuos.jimat.adapter.PagerSlideAdapter;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.fragment.login.LoginMsgFragment;
import com.enuos.jimat.fragment.login.LoginPswFragment;
import com.enuos.jimat.utils.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_back)
    ImageView mBack;
    @BindView(R.id.login_text_psw)
    TextView mLoginTextPsw;
    @BindView(R.id.login_linear_psw)
    LinearLayout mLoginLinearPsw;
    @BindView(R.id.login_text_msg)
    TextView mLoginTextMsg;
    @BindView(R.id.login_linear_msg)
    LinearLayout mLoginLinearMsg;
    @BindView(R.id.login_tab_line)
    ImageView mTabLine;
    @BindView(R.id.login_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.login_text_contact)
    TextView mLoginTextContact;
    @BindView(R.id.login_btn_wexin)
    ImageButton mLoginBtnWexin;
    @BindView(R.id.login_btn_qq)
    ImageButton mLoginBtnQq;
    @BindView(R.id.login_btn_facebook)
    ImageButton mLoginBtnFacebook;

    private int page = 0;
    private int screenWidth;
    private List<BaseFragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        page = intent.getIntExtra("page", 0);

        initData(); // 初始化数据
        initWidth(); // 初始化滑动横条的宽度
        setListener(); // 设置监听器

    }

    /**
     * 初始化 Fragment 数据以及设置颜色
     */
    private void initData() {
        mFragmentList.add(new LoginPswFragment());
        mFragmentList.add(new LoginMsgFragment());
        PagerSlideAdapter adapter = new PagerSlideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(page);
        switch (page) {
            case 0:
                mLoginTextPsw.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                break;
            case 1:
                mLoginTextMsg.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                break;
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
                        mLoginTextPsw.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                        break;
                    case 1:
                        mLoginTextMsg.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                        break;
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
        mLoginTextPsw.setTextColor(Color.BLACK);
        mLoginTextMsg.setTextColor(Color.BLACK);
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
    @OnClick({R.id.login_linear_psw, R.id.login_linear_msg})
    public void onLinearClick(LinearLayout linearLayout) {
        switch (linearLayout.getId()) {
            case R.id.login_linear_psw:
                mViewPager.setCurrentItem(0);
                resetTextView();
                mLoginTextPsw.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                break;
            case R.id.login_linear_msg:
                mViewPager.setCurrentItem(1);
                resetTextView();
                mLoginTextMsg.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.mainBlue));
                break;
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.login_back, R.id.login_btn_wexin, R.id.login_btn_qq,
            R.id.login_btn_facebook, R.id.login_text_contact})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.login_back:
                finish();
                break;
            // 微信登录
            case R.id.login_btn_wexin:
                ToastUtils.show(mBaseActivity, "敬请期待");
                break;
            // QQ登录
            case R.id.login_btn_qq:
                ToastUtils.show(mBaseActivity, "敬请期待");
                break;
            // Facebook登录
            case R.id.login_btn_facebook:
                ToastUtils.show(mBaseActivity, "敬请期待");
                break;
            // 隐私政策
            case R.id.login_text_contact:
                Intent intent = new Intent(mBaseActivity, WebActivity.class);
                intent.putExtra("title", "隐私政策");
                startActivity(intent);
                break;
        }
    }

}
