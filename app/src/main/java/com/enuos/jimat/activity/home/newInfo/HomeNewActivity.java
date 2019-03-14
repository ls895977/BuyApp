package com.enuos.jimat.activity.home.newInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.goods.GoodsBuyActivity;
import com.enuos.jimat.activity.goods.GoodsDetailsActivity;
import com.enuos.jimat.activity.home.HomeDescFragment;
import com.enuos.jimat.activity.home.HomeSaleFragment;
import com.enuos.jimat.adapter.PagerSlideAdapter;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.ClickUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.enuos.jimat.view.BannerModel;
import com.enuos.jimat.view.BannerViewAdapter;
import com.enuos.jimat.view.WrapContentHeightViewPager;
import com.example.myvideoplayer.JCVideoPlayer;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

import static com.enuos.jimat.utils.MyUtils.secondsToTime;

public class HomeNewActivity extends BaseActivity {
    @BindView(R.id.home_new_banner)
    Banner mBanner;
    @BindView(R.id.home_new_banner_goods_name)
    TextView mHomeNewBannerGoodsName;
    @BindView(R.id.home_new_banner_goods_price)
    TextView mHomeNewBannerGoodsPrice;
    @BindView(R.id.home_new_banner_goods_price_old)
    TextView mHomeNewBannerGoodsPriceOld;
    @BindView(R.id.home_new_banner_goods_number)
    TextView mHomeNewBannerGoodsNumber;
    @BindView(R.id.home_new_banner_goods_btn_buy)
    Button mHomeNewBannerGoodsBtnBuy;
    @BindView(R.id.home_new_view_pager)
   public WrapContentHeightViewPager mViewPager;
    @BindView(R.id.home_new_tab_line)
    ImageView mTabLine;
    @BindView(R.id.home_time_hour)
    TextView mHomeTimeHour;
    @BindView(R.id.home_time_minute)
    TextView mHomeTimeMinute;
    @BindView(R.id.home_time_second)
    TextView mHomeTimeSecond;
    @BindView(R.id.home_new_text_sal)
    TextView mHomeNewTextSal;
    @BindView(R.id.home_new_linear_sal)
    LinearLayout mHomeNewLinearSal;
    @BindView(R.id.home_viewPager)
    ViewPager viewPager;
    @BindView(R.id.home_banner_rl)
    RelativeLayout mHomeBannerRl;
    @BindView(R.id.home_banner_ll)
    LinearLayout mHomeBannerLl;
    @BindView(R.id.goods_details_transparent_home)
    ImageView mGoodsDetailsTransparentHome;
    //    @BindView(R.id.home_nestedScrollView)
//    NestedScrollView mHomeNestedScrollView;
//    @BindView(R.id.home_new_swipe_refresh)
//    SmartRefreshLayout mSwipe;
    @BindView(R.id.toolbar_tab)
    TabLayout mTablayout;
    @BindView(R.id.goods_details_nested_scroll)
    NestedScrollView mJudgeNested;
    private User mUser;
    private String goodsId, shopName, goodsPic, goodsName, videoUrl, img,
            goodsPrice, clientTime, startPrice, downType, downValue, isDelete, weight;
    private long timerTotal;
    private String nowPriceServer;

    private int page = 0;
    private int screenWidth;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private CountDownTimer mTimerOne, mTimerTwo;
    private int countTimes = 0;
    private String homeTime = "0";
    List<String> intentImage;
    private static final int UPTATE_VIEWPAGER = 0;
    private List<BannerModel> list;
    private BannerViewAdapter mAdapter;
    private int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private Timer timer;
    private TimerTask timerTask;
    private long period = 5000;//轮播图展示时长,默认5秒
    private int bannerPosition;

    // 定时轮播图片，需要在主线程里面修改 UI
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (JCVideoPlayer.isOnPlaying)
                        break;
                    else {
                        if (msg.arg1 != 0) {
                            viewPager.setCurrentItem(msg.arg1);
                        } else {
                            //false 当从末页调到首页时，不显示翻页动画效果，
                            viewPager.setCurrentItem(msg.arg1, false);
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        ButterKnife.bind(this);
        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 进入界面之后先获取信息
        setSwipe();
        refresh();
        list = new ArrayList<>();
        intentImage = new ArrayList<>();
        // 沉浸式
        //        if (Build.VERSION.SDK_INT >= 21) {
        //            View decorView = getWindow().getDecorView();
        //            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        //            decorView.setSystemUiVisibility(option);
        //            getWindow().setStatusBarColor(Color.TRANSPARENT);
        //        }

//        mHomeNestedScrollView.setFillViewport(true);
        autoBanner();
    }

    /**
     * 广告轮播图测试数据
     */
    public void initDataA() {
        if (videoUrl == null || videoUrl.equals("null") || videoUrl.isEmpty()) { // 无视频
            for (int i = 0; i < intentImage.size(); i++) {
                BannerModel listBean = new BannerModel();
                listBean.setBannerName("");
                listBean.setBannerUrl(intentImage.get(i));
                listBean.setVideoPic("");
                listBean.setPlayTime(3000);
                listBean.setUrlType(0); //图片类型 图片
                list.add(listBean);
            }
        } else { // 有视频
            for (int i = 0; i < intentImage.size(); i++) {
                BannerModel listBean = new BannerModel();
                if (i == 0) {
                    listBean.setBannerName("");
                    listBean.setBannerUrl(videoUrl);
                    listBean.setVideoPic(img);
                    listBean.setPlayTime(3000);
                    listBean.setUrlType(1);//图片类型 视频
                    list.add(listBean);
                } else {
                    listBean.setBannerName("");
                    listBean.setBannerUrl(intentImage.get(i));
                    listBean.setVideoPic("");
                    listBean.setPlayTime(3000);
                    listBean.setUrlType(0); //图片类型 图片
                    list.add(listBean);
                }
            }
        }
        period = list.get(0).getPlayTime();
        //        mAdapter.setListBean(list);
        mAdapter = new BannerViewAdapter(this, list, goodsId, homeTime);
        viewPager.setAdapter(mAdapter);
    }

    private void autoBanner() {
        mAdapter = new BannerViewAdapter(this, list, goodsId, homeTime);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                bannerPosition = position;
                JCVideoPlayer.releaseAllVideos();
            }
            @Override
            public void onPageSelected(int position) {
                autoCurrIndex = position;//动态设定轮播图每一页的停留时间
                period = list.get(position).getPlayTime();
                if (timer != null) { //每次改变都需要重新创建定时器
                    timer.cancel();
                    timer = null;
                    timer = new Timer();
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                        createTimerTask();
                    } else
                        createTimerTask();
                    timer.schedule(timerTask, period, period);
                } else {
                    timer = new Timer();
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                        createTimerTask();
                    } else
                        createTimerTask();
                    timer.schedule(timerTask, period, period);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createTimerTask();//创建定时器
        mAdapter.setOnClick(new BannerViewAdapter.setOnClick() {
            @Override
            public void click(View v) {
                Intent intentInfo = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                intentInfo.putExtra("goodsId", goodsId);
                intentInfo.putExtra("goodsType", "base");
                intentInfo.putExtra("type", "home");
                intentInfo.putExtra("value", homeTime);
                startActivity(intentInfo);
            }
        });

        timer = new Timer();
        timer.schedule(timerTask, 5000, period);
    }


    public void createTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (autoCurrIndex == list.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.onBack()) {
            return;
        }
        super.onBackPressed();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 初始化 Fragment 数据以及设置颜色
     */
    PagerSlideAdapter adapter;
    private void initData() {
        mFragmentList.clear();
        mFragmentList.add(new HomeDescFragment());
        mFragmentList.add(new HomeSaleFragment());
        adapter = new PagerSlideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.resetHeight(0);
        mViewPager.setCurrentItem(page);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (mTablayout));
        mTablayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }
    /**
     * 设置滑动监听器
     */
    private void setListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("aa","-----------"+position);
                mViewPager.resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_LOGIN)) {
            refresh();
        }
    }

    /**
     * 判断是否登录
     */
    public boolean isLogin() {
        // 取出信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        if (user != null && !user.userAccount.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 设置刷新
     */
    private void setSwipe() {
        //设置 Header 为 Material风格
//        mSwipe.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false));
//        //设置 Footer 为 球脉冲
//        mSwipe.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
//
//        mSwipe.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
//            @Override
//            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//            }
//
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                refreshLayout.finishLoadMore();
//            }
//
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                refreshLayout.finishRefresh();
//                initData(); // 初始化数据
////                initWidth(); // 初始化滑动横条的宽度
//                resetTextView();
////                mHomeNewTextDesc.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_D02D2E));
//            }
//        });
//        mSwipe.setColorSchemeColors(ContextCompat.getColor(mBaseActivity, R.color.blue_btn_bg_color));
//        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                initData(); // 初始化数据
//                initWidth(); // 初始化滑动横条的宽度
//                resetTextView();
//                mHomeNewTextDesc.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.color_D02D2E));
//                refresh();
//            }
//        });
    }

    public void onResume() {
        refresh();
        super.onResume();
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
//        mSwipe.setRefreshing(true);

        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("pageNum", "1");
        params.put("pageSize", "20");
        params.put("token", userToken);
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
//        mSwipe.setRefreshing(false);
        countTimes++;
        Log.e("789", "countTimes: " + String.valueOf(countTimes));
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.home_banner_rl, R.id.home_new_banner_goods_btn_buy, R.id.home_new_banner_goods_name,
            R.id.home_new_linear_desc, R.id.home_new_linear_sale, R.id.home_viewPager})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //            // 进入详情
            case R.id.home_banner_rl:
            case R.id.home_viewPager:
                if (bannerPosition == 0 || bannerPosition == intentImage.size()) { // 第一张图片
                    if (videoUrl == null || videoUrl.equals("null")) { // 无视频
                        Intent intentInfo = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                        intentInfo.putExtra("goodsId", goodsId);
                        intentInfo.putExtra("goodsType", "base");
                        intentInfo.putExtra("type", "home");
                        intentInfo.putExtra("value", homeTime);
                        startActivity(intentInfo);
                    } else { // 有视频

                    }
                } else {
                    Intent intentInfo = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                    intentInfo.putExtra("goodsId", goodsId);
                    intentInfo.putExtra("goodsType", "base");
                    intentInfo.putExtra("type", "home");
                    intentInfo.putExtra("value", homeTime);
                    startActivity(intentInfo);
                }
                break;
            // 进入详情
            case R.id.home_new_banner_goods_name:
                Intent intentInfo = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                intentInfo.putExtra("goodsId", goodsId);
                intentInfo.putExtra("goodsType", "base");
                intentInfo.putExtra("type", "home");
                intentInfo.putExtra("value", homeTime);
                startActivity(intentInfo);
                break;
            // 正在降价
            case R.id.home_new_linear_desc:
                mViewPager.setCurrentItem(0);
                break;
            // 待售商品
            case R.id.home_new_linear_sale:
                mViewPager.setCurrentItem(1);
                break;
            // 立即购买
            case R.id.home_new_banner_goods_btn_buy:
                if (!ClickUtils.INSTANCE.isFastDoubleClick()) {
                    mHomeNewBannerGoodsBtnBuy.setClickable(true);
                    if (isLogin()) {
                        if (!isDelete.equals("1")) {
                            mHomeNewBannerGoodsBtnBuy.setClickable(true);
                            ToastUtils.show(mBaseActivity, "Item has expired. Please choose again.");
                        } else {
                            // 先刷新
                        /*HashMap<String, String> paramsA = new HashMap<>();
                        paramsA.put("pageNum", "1");
                        paramsA.put("pageSize", "10");
                        DoPostTask mDoPostTask = new DoPostTask();
                        mDoPostTask.execute(paramsA);*/
                            // 获取当前时间 2018-12-25 12:12:12
                            mHomeNewBannerGoodsBtnBuy.setClickable(false);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String dateNow = simpleDateFormat.format(date);
                            String strYearNow = dateNow.substring(0, 4); // 现在的年份
                            String strMonthNow = dateNow.substring(5, 7); // 现在的月份
                            String strDayNow = dateNow.substring(8, 10); // 现在的天
                            String strHourNow = dateNow.substring(11, 13); // 现在的小时
                            String strMinuteNow = dateNow.substring(14, 16); // 现在的分钟
                            String strSecondNow = dateNow.substring(17, 19); // 现在的秒
                            clientTime = strYearNow + "-" + strMonthNow + "-" + strDayNow
                                    + " " + strHourNow + ":" + strMinuteNow + ":" + strSecondNow;

                            // 获取 User 信息
                            IDataStorage dataStorage = DataStorageFactory.getInstance(
                                    getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                            mUser = dataStorage.load(User.class, "User");
                            HashMap<String, String> params = new HashMap<>();
                            params.put("goodsId", goodsId);
                            params.put("memberId", mUser.userID);
                            params.put("token", mUser.token);
                            params.put("clientTime", clientTime);
                            params.put("clientPrice", nowPriceServer);
                            OrderTask mOrderTask = new OrderTask();
                            mOrderTask.execute(params);
                        }
                    } else {
                        ToastUtils.show(mBaseActivity, "Please Login");
                        Intent intent = new Intent(mBaseActivity, LoginNewActivity.class);
                        intent.putExtra("from", "home");
                        intent.putExtra("goodsId", "");
                        intent.putExtra("goodsType", "");
                        intent.putExtra("homeTime", "0");
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    /**
     * 加载界面
     */
    private void loadPage(JSONObject jsonObject) {
        try {
            String stringData = jsonObject.getString("data");
            JSONObject jsonObjectData = new JSONObject(stringData);
            String jsonArrayString = jsonObjectData.getString("goodsFirst");
            final JSONObject jsonObjectFirst = new JSONObject(jsonArrayString);
            isDelete = jsonObjectFirst.getString("IS_DELETE"); // 是否下架

            img = jsonObjectFirst.getString("IMAGE_URL");
            videoUrl = jsonObjectFirst.getString("VIDEO_URL");
            goodsId = jsonObjectFirst.getString("ID");
            if (img == null || img.equals("null"))
                img = "";
            if (videoUrl == null || videoUrl.equals("null"))
                videoUrl = "";
            /**
             * 设置轮播图 goodsImgList
             */
            JSONArray imgJsonArray = new JSONArray(jsonObjectFirst.getString("goodsImgList"));
            int maxImage = imgJsonArray.length();
            String postStr[] = new String[maxImage];
            for (int i = 0; i < maxImage; i++) {
                postStr[i] = imgJsonArray.getJSONObject(i).getString("IMG_URL");
            }

            int imageSize = 0;

            for (int i = 0; i < maxImage; i++) {
                if (!imgJsonArray.getJSONObject(i).getString("IMG_URL").equals("null")) {
                    imageSize++;
                }
            }
            final String intentImageArray[] = new String[imageSize];

            for (int i = 0; i < maxImage; i++) {
                intentImageArray[i] = imgJsonArray.getJSONObject(i).getString("IMG_URL");
            }
            if (!img.isEmpty() && !videoUrl.isEmpty()) {
                String imgarray[] = new String[intentImageArray.length + 1];
                imgarray[0] = img;
                System.arraycopy(intentImageArray, 0, imgarray, 1, intentImageArray.length);
                intentImage.clear();
                intentImage.addAll(Arrays.asList(imgarray));
            } else {
                intentImage.clear();
                intentImage.addAll(Arrays.asList(intentImageArray));
            }
            list.clear();
            initDataA();

            String startSalePrice = jsonObjectFirst.getString("GOODS_START_PRICE"); // 原价
            mHomeNewBannerGoodsName.setText(jsonObjectFirst.getString("GOODS_NAME"));
            mHomeNewBannerGoodsPriceOld.setText("RM " + startSalePrice);
            mHomeNewBannerGoodsPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mHomeNewBannerGoodsNumber.setText(" " + jsonObjectFirst.getString("GOODS_RESIDUE_STOCK") + " ");
            // 商品价格
            mHomeNewBannerGoodsPrice.setText(jsonObjectFirst.getString("GOODS_START_PRICE"));

            // 获取当前时间戳
            //            long timeStampSec = System.currentTimeMillis() / 1000; // 单位毫秒
            //            long systemTime = Long.parseLong(String.format("%010d", timeStampSec));
            String systemTime = jsonObjectFirst.getString("SYS_TIME"); // 系统时间
            String startTime = jsonObjectFirst.getString("MARKET_START_TIME"); // 起售时间 单位秒
            String descTime = jsonObjectFirst.getString("GOODS_DOWN_TIME"); // 降价时间
            String descType = jsonObjectFirst.getString("GOODS_DOWN_TYPE"); // 降价类型
            String descValue = jsonObjectFirst.getString("GOODS_DOWN_VALUE"); // 降价数值 或 降价比例
            String miniPrice = jsonObjectFirst.getString("GOODS_MINI_PRICE"); // 最低价
            Log.e("789", "systemTime11111: " + systemTime);
            Log.e("789", "startTime1111: " + startTime);
            Log.e("789", "descTime11111: " + descTime);
            Log.e("789", "descType111: " + descType);
            Log.e("789", "descValue1111: " + descValue);
            Log.e("789", "miniPrice111: " + miniPrice);
            // 系统时间大于服务器开始降价时间 即 已经处于降价
            if (Long.parseLong(systemTime) > Long.parseLong(startTime)) { // 正在降价
                // 按金额降价：原价-（系统时间-起售时间）/降价时间*降价金额《最低价=最低价
                if (descType.equals("1")) {
                    double secondsAll = (Long.parseLong(systemTime) - Long.parseLong(startTime)); // 距离降价开始还有多少秒
                    Log.e("789", "secondsAll: " + String.valueOf(secondsAll));
                    double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期
                    Log.e("789", "descTimes: " + String.valueOf(descTimes));
                    BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
                    double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double descPrice = descTimesReal * Double.valueOf(descValue); // 降价周期 * 单位降价金额
                    Log.e("789", "descTimesReal: " + String.valueOf(descTimesReal));
                    Log.e("789", "descPrice: " + String.valueOf(descPrice));

                    int descTimesRealComplete = (int) descTimesReal; // 降价周期取整 表示降了多少次
                    //                    int descTimesRealComplete = (int) descTimes; // 降价周期取整 表示降了多少次
                    Log.e("789", "descTimesRealComplete: " + String.valueOf(descTimesRealComplete));
                    double descPriceComplete = descTimesRealComplete * Double.valueOf(descValue);
                    // 得到降价后的具体金额A 开始价格 - 降价周期*(降价比例*原价)
                    Log.e("789", "descPriceComplete: " + String.valueOf(descPriceComplete));


                    // 得到降价后的具体金额A 开始价格 - (降价周期 * 单位降价金额)
                    double nowPrice = Double.valueOf(startSalePrice) - descPrice;
                    Log.e("789", "nowPrice: " + String.valueOf(nowPrice));
                    if (nowPrice < Double.valueOf(miniPrice) || nowPrice < 0) { // 如果小于等于最低价 现在的价就是计算得到的价A
                        BigDecimal bigDecimalPay = new BigDecimal(miniPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        //                        mHomeNewBannerGoodsPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位

                        mHomeTimeHour.setText(" " + "00" + " ");
                        mHomeTimeMinute.setText(" " + "00" + " ");
                        mHomeTimeSecond.setText(" " + "00" + " ");
                        mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                        Log.e("789", "nowPrice: " + String.valueOf(nowPriceServer));

                    } else { // 如果大于最低价 现在的价就是最低价
                        BigDecimal bigDecimalPay = new BigDecimal(nowPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        //                        mHomeNewBannerGoodsPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位

                        mHomeNewBannerGoodsPrice.setText(String.format("%.2f", Double.valueOf(startSalePrice) - descPriceComplete)); // 四舍五入 保留2位

                        // 倒计时
                        // 取降价周期的小数点后两位
                        //                        String timesString = descTimesReal + "";
                        String timesString = descTimes + "";
                        Log.e("789", "timesString: " + timesString);
                        String timeCounterString = timesString.substring(timesString.indexOf("."), timesString.length());
                        Log.e("789", "timeCounterString: " + timeCounterString);
                        if (timeCounterString.equals("00")) { // 数值达到降价时间区间的首尾极限
                            timerTotal = Long.parseLong(descTime);
                        } else { // 数值在降价时间区间内
                            // 得到比如【1, 110】中的某个具体数值
                            double timesCounter = (Double.valueOf(timeCounterString) * Double.valueOf(descTime)); // 单位秒
                            Log.e("789", "timesCounter: " + String.valueOf(timesCounter));
                            String timesValue = String.valueOf((int) (Double.valueOf(descTime) - timesCounter)) + "000"; // 单位转换
                            Log.e("789", "timesValue: " + timesValue);
                            timerTotal = Long.parseLong(timesValue); // 单位毫秒
                        }
                        Log.e("789", "timerTotal: " + timerTotal);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (countTimes > 1) {
                                    mTimerOne.cancel();

                                    // 倒计时开始
                                    mTimerOne = new CountDownTimer(timerTotal, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            String allTime = secondsToTime(millisUntilFinished / 1000);
                                            homeTime = String.valueOf(millisUntilFinished);
                                            mHomeTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                            mHomeTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                            mHomeTimeSecond.setText(" " + allTime.substring(6, 8) + " ");

//                                            if (mSwipe.isRefreshing()) {
//                                                mTimerOne.cancel();
//                                                Log.e("789", "1111: ");
//                                            }

                                        }

                                        @Override
                                        public void onFinish() {
                                            homeTime = "0";
                                            mHomeTimeHour.setText(" " + "00" + " ");
                                            mHomeTimeMinute.setText(" " + "00" + " ");
                                            mHomeTimeSecond.setText(" " + "00" + " ");
                                            //                            refresh();
                                            mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                                        }
                                    };
                                    mTimerOne.start();
                                } else {

                                    // 倒计时开始
                                    mTimerOne = new CountDownTimer(timerTotal, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            String allTime = secondsToTime(millisUntilFinished / 1000);
                                            homeTime = String.valueOf(millisUntilFinished);
                                            mHomeTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                            mHomeTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                            mHomeTimeSecond.setText(" " + allTime.substring(6, 8) + " ");

//                                            if (mSwipe.isRefreshing()) {
//                                                mTimerOne.cancel();
//                                                Log.e("789", "1111: ");
//                                            }

                                        }

                                        @Override
                                        public void onFinish() {
                                            homeTime = "0";
                                            mHomeTimeHour.setText(" " + "00" + " ");
                                            mHomeTimeMinute.setText(" " + "00" + " ");
                                            mHomeTimeSecond.setText(" " + "00" + " ");
                                            //                            refresh();
                                            mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                                        }
                                    };
                                    mTimerOne.start();
                                }

                            }
                        });

                    }

                } else {
                    // 按比率降价：原价-（系统时间-起售时间）/降价时间*（原价*降价金额）《最低价=最低价
                    double secondsAll = (Long.parseLong(systemTime) - Long.parseLong(startTime)); // 距离降价开始还有多少秒
                    Log.e("789", "secondsAll: " + String.valueOf(secondsAll));
                    double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期

                    Log.e("789", "descTimes: " + String.valueOf(descTimes));
                    BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
                    double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    // 降价周期*(降价比例*原价)
                    double descPrice = descTimesReal * ((Double.valueOf(startSalePrice)) * (Double.valueOf(descValue)));


                    // 降价周期*(降价比例*原价)
                    int descTimesRealComplete = (int) descTimesReal; // 降价周期取整 表示降了多少次
                    Log.e("789", "descTimesRealComplete: " + String.valueOf(descTimesRealComplete));
                    double descPriceComplete = descTimesRealComplete * ((Double.valueOf(startSalePrice)) * (Double.valueOf(descValue)));
                    // 得到降价后的具体金额A 开始价格 - 降价周期*(降价比例*原价)
                    Log.e("789", "descPriceComplete: " + String.valueOf(descPriceComplete));


                    Log.e("789", "descTimesReal: " + String.valueOf(descTimesReal));
                    Log.e("789", "descPrice: " + String.valueOf(descPrice));
                    double nowPrice = Double.valueOf(startSalePrice) - descPrice;
                    Log.e("789", "nowPrice: " + String.valueOf(nowPrice));
                    if (nowPrice < Double.valueOf(miniPrice) || nowPrice < 0) { // 如果小于等于最低价 现在的价就是计算得到的价A
                        BigDecimal bigDecimalPay = new BigDecimal(miniPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        //                        mHomeNewBannerGoodsPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        homeTime = "0";
                        mHomeTimeHour.setText(" " + "00" + " ");
                        mHomeTimeMinute.setText(" " + "00" + " ");
                        mHomeTimeSecond.setText(" " + "00" + " ");
                        mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                        Log.e("789", "nowPrice: " + String.valueOf(nowPriceServer));
                    } else { // 如果大于最低价 现在的价就是最低价
                        BigDecimal bigDecimalPay = new BigDecimal(nowPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        //                        mHomeNewBannerGoodsPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位

                        mHomeNewBannerGoodsPrice.setText(String.format("%.2f", Double.valueOf(startSalePrice) - descPriceComplete)); // 四舍五入 保留2位

                        // 倒计时
                        // 取降价周期的小数点后两位
                        String timesString = descTimesReal + "";
                        String timeCounterString = timesString.substring(timesString.indexOf("."), timesString.length());
                        if (timeCounterString.equals("00")) { // 数值达到降价时间区间的首尾极限
                            timerTotal = Long.parseLong(descTime);
                        } else { // 数值在降价时间区间内
                            // 得到比如【1, 110】中的某个具体数值
                            double timesCounter = (Double.valueOf(timeCounterString) * Double.valueOf(descTime)); // 单位秒
                            String timesValue = String.valueOf((int) (Double.valueOf(descTime) - timesCounter)) + "000"; // 单位转换
                            timerTotal = Long.parseLong(timesValue); // 单位毫秒
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (countTimes > 1) {
                                    mTimerTwo.cancel();

                                    // 倒计时开始
                                    mTimerTwo = new CountDownTimer(timerTotal, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            String allTime = secondsToTime(millisUntilFinished / 1000);
                                            homeTime = String.valueOf(millisUntilFinished);
                                            mHomeTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                            mHomeTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                            mHomeTimeSecond.setText(" " + allTime.substring(6, 8) + " ");

//                                            if (mSwipe.isRefreshing()) {
//                                                mTimerTwo.onFinish();
//                                                Log.e("789", "2222: ");
//                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            homeTime = "0";
                                            mHomeTimeHour.setText(" " + "00" + " ");
                                            mHomeTimeMinute.setText(" " + "00" + " ");
                                            mHomeTimeSecond.setText(" " + "00" + " ");
                                            //                            refresh();
                                            mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                                        }
                                    };
                                    mTimerTwo.start();
                                } else {
                                    // 倒计时开始
                                    mTimerTwo = new CountDownTimer(timerTotal, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            String allTime = secondsToTime(millisUntilFinished / 1000);
                                            homeTime = String.valueOf(millisUntilFinished);
                                            mHomeTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                            mHomeTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                            mHomeTimeSecond.setText(" " + allTime.substring(6, 8) + " ");

//                                            if (mSwipe.isRefreshing()) {
//                                                mTimerTwo.onFinish();
//                                                Log.e("789", "2222: ");
//                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            homeTime = "0";
                                            mHomeTimeHour.setText(" " + "00" + " ");
                                            mHomeTimeMinute.setText(" " + "00" + " ");
                                            mHomeTimeSecond.setText(" " + "00" + " ");
                                            //                            refresh();
                                            mHomeNewBannerGoodsPrice.setText(nowPriceServer); // 现价
                                        }
                                    };
                                    mTimerTwo.start();
                                }
                            }
                        });
                    }
                }
            } else {  // 系统时间小于服务器开始降价时间 即 还没有处于降价
                // 倒计时
                /*double secondsAll = (Long.parseLong(startTime) - systemTime) / 1000; // 距离降价开始还有多少秒
                double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期
                BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
                double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                // 取降价周期的小数点后两位
                String timesString = descTimesReal + "";
                String timeCounterString = timesString.substring(timesString.indexOf("."), timesString.length());
                if (timeCounterString.equals("00")) { // 数值达到降价时间区间的首尾极限
                    timerTotal = Long.parseLong(descTime);
                } else { // 数值在降价时间区间内
                    // 得到比如【1, 110】中的某个具体数值
                    double timesCounter = (Double.valueOf(timeCounterString) * Double.valueOf(descTime)); // 单位秒
                    String timesValue = String.valueOf((int) (Double.valueOf(descTime) - timesCounter)) + "000"; // 单位转换
                    timerTotal = Long.parseLong(timesValue); // 单位毫秒
                }
                // 倒计时开始
                CountDownTimer mTimer = new CountDownTimer(timerTotal, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String allTime = secondsToTime(millisUntilFinished / 1000);
                        mHomeTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                        mHomeTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                        mHomeTimeSecond.setText(" " + allTime.substring(6, 8) + " ");
                    }

                    @Override
                    public void onFinish() {
                        mHomeTimeHour.setText(" " + "00" + " ");
                        mHomeTimeMinute.setText(" " + "00" + " ");
                        mHomeTimeSecond.setText(" " + "00" + " ");
                        refresh();
                    }
                };
                mTimer.start();*/
                homeTime = "0";
                mHomeTimeHour.setText(" " + "00" + " ");
                mHomeTimeMinute.setText(" " + "00" + " ");
                mHomeTimeSecond.setText(" " + "00" + " ");
            }

            shopName = jsonObjectFirst.getString("COMPANY_NAME");
            goodsPic = jsonObjectFirst.getString("IMAGE_URL");
            goodsName = jsonObjectFirst.getString("GOODS_NAME");

            startPrice = jsonObjectFirst.getString("GOODS_START_PRICE");
            downType = jsonObjectFirst.getString("GOODS_DOWN_TYPE");
            downValue = jsonObjectFirst.getString("GOODS_DOWN_VALUE");

            weight = jsonObjectFirst.getString("GOODS_WEIGHT");

            //            int second = Integer.parseInt(jsonObjectFirst.getString("GOODS_DOWN_TIME"));
            //            mHomeNewBannerGoodsDescTime.setText(secondsToTime(second));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        initData(); // 初始化数据
        setListener(); // 设置监听器
    }

    /**
     * 内部类
     * 收货地址列表
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.home_new_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "首页的返回结果：" + data.toString());
                    loadPage(data);
//                    mSwipe.setRefreshing(false);
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 同一账户多个终端登录
                String msgError = result[1].toString();
                ToastUtils.show(mBaseActivity, msgError);
                if (msgError.contains("Your account is being logged") || msgError.contains("account")) {
                    // 退出 APP 本身的账号
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = new User();
                    user.userAccount = "";
                    user.isLogin = "false";
                    user.token = "";
                    dataStorage.storeOrUpdate(user, "User");
                    EventBus.getDefault().post(EventConfig.EVENT_EXIT);
                    // 退出环信账号
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            // 登录环信服务器退出登录
                            EMClient.getInstance().logout(false, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    // 关闭 DBHelper
                                    // Model.getInstance().getDbManager().close();
                                    Log.e("789", "环信账号退出成功");
                                }

                                @Override
                                public void onError(int i, final String s) {
                                }

                                @Override
                                public void onProgress(int i, String s) {
                                }
                            });
                        }
                    });
                    finish();
                }
                // 同一账户多个终端登录
            }

        }
    }

    /**
     * Glide 图片加载类
     */
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    /**
     * 内部类
     * 生成订单
     */
    private class OrderTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.order_add_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "生成订单的返回结果：" + data.toString());
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    String orderId = jsonObjectData.getString("ID");
                    String orderNo = jsonObjectData.getString("ORDER_CODE");
                    final String goodsPrice = mHomeNewBannerGoodsPrice.getText().toString();
                    mHomeNewBannerGoodsBtnBuy.setClickable(true);
                    Intent intent = new Intent(mBaseActivity, GoodsBuyActivity.class);
                    intent.putExtra("shopName", shopName);
                    intent.putExtra("goodsPic", goodsPic);
                    intent.putExtra("goodsName", goodsName);
                    //                    intent.putExtra("goodsPrice", nowPriceServer);
                    intent.putExtra("goodsPrice", goodsPrice);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("orderNo", orderNo);
                    intent.putExtra("orderTime", clientTime);
                    intent.putExtra("weight", weight);
                    startActivity(intent);
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 同一账户多个终端登录
                String msgError = result[1].toString();
                ToastUtils.show(mBaseActivity, msgError);
                if (msgError.contains("Your account is being logged") || msgError.contains("account")) {
                    // 退出 APP 本身的账号
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = new User();
                    user.userAccount = "";
                    user.isLogin = "false";
                    user.token = "";
                    dataStorage.storeOrUpdate(user, "User");
                    EventBus.getDefault().post(EventConfig.EVENT_EXIT);
                    // 退出环信账号
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            // 登录环信服务器退出登录
                            EMClient.getInstance().logout(false, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    // 关闭 DBHelper
                                    // Model.getInstance().getDbManager().close();
                                    Log.e("789", "环信账号退出成功");
                                }

                                @Override
                                public void onError(int i, final String s) {
                                }

                                @Override
                                public void onProgress(int i, String s) {
                                }
                            });
                        }
                    });
                    finish();
                }
                // 同一账户多个终端登录
            }

        }
    }

}
