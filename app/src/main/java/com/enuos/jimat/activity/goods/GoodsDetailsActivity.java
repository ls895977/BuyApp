package com.enuos.jimat.activity.goods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.ChatActivity;
import com.enuos.jimat.activity.common.ShowBannerActivity;
import com.enuos.jimat.adapter.PagerSlideAdapter;
import com.enuos.jimat.app.MyApplication;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.BmpToByteArray;
import com.enuos.jimat.utils.ClickUtils;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.easeui.MyConnectionListener;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.enuos.jimat.view.BannerModel;
import com.enuos.jimat.view.BannerViewAdapter;
import com.enuos.jimat.view.SharePopupWindow;
import com.enuos.jimat.view.WrapContentHeightViewPager;
import com.example.myvideoplayer.JCVideoPlayer;
import com.example.myvideoplayer.JCVideoPlayerStandard;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMClient;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

import static com.enuos.jimat.utils.MyUtils.secondsToTime;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

public class GoodsDetailsActivity extends BaseActivity {

    @BindView(R.id.goods_details_back)
    ImageView mBack;
    @BindView(R.id.goods_details_banner)
    Banner mGoodsDetailsBanner;
    @BindView(R.id.goods_details_new_price)
    TextView mGoodsDetailsNewPrice;
    @BindView(R.id.goods_details_old_price)
    TextView mGoodsDetailsOldPrice;
    @BindView(R.id.goods_details_progess_text)
    TextView mGoodsDetailsProgessText;
    @BindView(R.id.goods_details_progess)
    ProgressBar mGoodsDetailsProgess;
    @BindView(R.id.goods_details_name)
    TextView mGoodsDetailsName;
    @BindView(R.id.goods_details_shop_name)
    TextView mGoodsDetailsShopName;
    @BindView(R.id.goods_details_btn_buy)
    Button mGoodsDetailsBtnBuy;
    @BindView(R.id.goods_details_go_msg)
    ImageView mGoodsDetailsGoMsg;


    @BindView(R.id.goods_details_view_pager)
    public WrapContentHeightViewPager mViewPager;
    @BindView(R.id.goods_details_details_default)
    TextView mGoodsDetailsDetailsDefault;
    @BindView(R.id.goods_details_goods_id)
    TextView mGoodsDetailsGoodsId;
    @BindView(R.id.good_time_hour)
    TextView mGoodTimeHour;
    @BindView(R.id.good_time_minute)
    TextView mGoodTimeMinute;
    @BindView(R.id.good_time_second)
    TextView mGoodTimeSecond;
    @BindView(R.id.goods_details_buy_linear)
    LinearLayout mGoodsDetailsBuyLinear;
    @BindView(R.id.goods_details_goods_type)
    TextView mGoodsDetailsGoodsType;
    @BindView(R.id.jc_video)
    JCVideoPlayerStandard jcVideo;
    @BindView(R.id.goods_details_share)
    ImageView mGoodsDetailsShare;
    @BindView(R.id.imgchange)
    ImageView imgchange;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.banner_rl)
    RelativeLayout mBannerRl;
    @BindView(R.id.goods_details_transparent)
    ImageView mGoodsDetailsTransparent;
    @BindView(R.id.toolbar_tab)
    TabLayout mTablayout;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.lin1)
    LinearLayout mLin;
    @BindView(R.id.goods_details_btn_intop)
    ImageView mInTop;

    private SweetAlertDialog mProgressDialog;
    private String goodsId, goodsType, type, value, homeTime, videoUrl, img;
    private String shopName, goodsPic, goodsName, goodsPrice, clientTime, isDelete, weight;
    private User mUser;
    private String nowPriceServer;

    private long timerTotal;

    private int page = 0;
    private int screenWidth;
    private List<BaseFragment> mFragmentList = new ArrayList<>();

    private SharePopupWindow mSharePopupWindow;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_new_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        list = new ArrayList<>();
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        intentImage = new ArrayList<>();
        mUser = dataStorage.load(User.class, "User");

        goodsId = getIntent().getStringExtra("goodsId");
        goodsType = getIntent().getStringExtra("goodsType");
        type = getIntent().getStringExtra("type");
        value = getIntent().getStringExtra("value");
        Log.e("aa", "---------value====" + value);
        homeTime = value;

        mGoodsDetailsGoodsId.setText(goodsId);
        mGoodsDetailsGoodsType.setText(goodsType);

        if (goodsType.equals("sale")) {
            mGoodsDetailsBtnBuy.setBackground(getResources().getDrawable(R.drawable.btn_home_buy_gray_selector));
            mGoodsDetailsBtnBuy.setClickable(false);
        } else {
            mGoodsDetailsBtnBuy.setBackground(getResources().getDrawable(R.drawable.btn_home_buy_selector));
            mGoodsDetailsBtnBuy.setClickable(true);
        }

        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);

        // 沉浸式
        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -mLin.getHeight() / 2) {
                    mInTop.setVisibility(View.VISIBLE);
                } else {
                    mInTop.setVisibility(View.GONE);
                }
            }
        });
        doRefresh();
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
                    listBean.setPlayTime(5000);
                    listBean.setUrlType(0); //图片类型 图片
                    list.add(listBean);
                }
            }
        }


        period = list.get(0).getPlayTime();
        autoBanner();

    }

    private void autoBanner() {
        viewPager.setOffscreenPageLimit(0);
        mAdapter = new BannerViewAdapter(this, list);
        mAdapter.setOnClick(new BannerViewAdapter.setOnClick() {
            @Override
            public void click(View view) {
                if (intentImageArray.length == 0) {
                    return;
                }
                if (list.get(bannerPosition).getUrlType() == 0) {
                    Intent intent = new Intent(mBaseActivity, ShowBannerActivity.class);
                    intent.putExtra("intentImageArray", intentImageArray);
                    startActivity(intent);
                } else {
//                    try {
//                        Log.e("aa", list.get(bannerPosition).getBannerUrl() + "------------" + list.get(bannerPosition).getUrlType());
//                        jcVideo.setVisibility(View.VISIBLE);
//                        mGoodsDetailsBanner.setVisibility(View.GONE);
//                        if (!videoUrl.equals("") || videoUrl != null) {
//                            if (!img.equals("") || img != null) {
//                                Glide.with(mBaseActivity).load(img).into(jcVideo.thumbImageView);
//                            }
//                            jcVideo.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
//                        }
//                    } catch (Exception e) {
//                    }
                }

            }
        });
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                bannerPosition = position;
                JCVideoPlayer.releaseAllVideos();
            }

            @Override
            public void onPageSelected(int position) {
                autoCurrIndex = position;//动态设定轮播图每一页的停留时间
                period = list.get(position).getPlayTime();
                if (timer != null) {//每次改变都需要重新创建定时器
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
                viewPager.setOffscreenPageLimit(2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createTimerTask();//创建定时器

        timer = new Timer();
        timer.schedule(timerTask, 5000, period);

    }

    public void createTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (list == null || list.isEmpty() || autoCurrIndex == list.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {//每次改变都需要重新创建定时器
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
        if (JCVideoPlayer.backPress()) {
            return;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化 Fragment 数据以及设置颜色
     */
    private void initData() {
        mFragmentList.add(new DetailsDetailsFragment());
        mFragmentList.add(new DetailsPriceFragment());
        mFragmentList.add(new DetailsBuyFragment());
        PagerSlideAdapter adapter = new PagerSlideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(page);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (mTablayout));
        mTablayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    /**
     * 设置滑动监听器
     */
    private void setListener() {
        //一键返回顶部
        mInTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentList != null && mFragmentList.size() == 3) {
                    ((DetailsDetailsFragment) mFragmentList.get(0)).moveFirst();
                    ((DetailsPriceFragment) mFragmentList.get(1)).moveFirst();
                    ((DetailsBuyFragment) mFragmentList.get(2)).moveFirst();
                }
                mAppBarLayout.setExpanded(true);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                mViewPager.resetHeight(position);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 获取产品详情
     */
    private void doRefresh() {
        // 获取产品详情
        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        DoPostTask task = new DoPostTask();
        HashMap<String, String> params = new HashMap<>();
        Log.e("aa", "-------memberId---" + "------goodsId--" + goodsId + "-------token--" + userToken);
        params.put("memberId", "");
        params.put("goodsId", goodsId);
        params.put("token", userToken);
        task.execute(params);
    }

    CountDownTimer mTimer;
    /**
     * 加载页面所有的视图元素
     */
    public String intentImageArray[];

    private void loadPage(JSONObject getJsonObject) {
        try {
            goodsName = getJsonObject.getString("GOODS_NAME");
            shopName = getJsonObject.getString("COMPANY_NAME");
            isDelete = getJsonObject.getString("IS_DELETE");

            mGoodsDetailsName.setText(goodsName);
            mGoodsDetailsShopName.setText(shopName);

            String totalNum = getJsonObject.getString("GOODS_TOTAL_STOCK");
            String saleNum = getJsonObject.getString("GOODS_SOLD_STOCK");
            if (saleNum.equals("0")) {
                mGoodsDetailsProgess.setProgress(0);
                mGoodsDetailsProgessText.setText("0" + "/" + totalNum);
            } else {
                mGoodsDetailsProgess.setMax(Integer.parseInt(totalNum));
                mGoodsDetailsProgess.setProgress(Integer.parseInt(saleNum));
                mGoodsDetailsProgessText.setText(" " + saleNum + "/" + totalNum);
            }

            goodsPic = getJsonObject.getString("IMAGE_URL");
            weight = getJsonObject.getString("GOODS_WEIGHT");

            img = getJsonObject.getString("IMAGE_URL");
            videoUrl = getJsonObject.getString("VIDEO_URL");
            if (img == null || img.equals("null"))
                img = "";
            if (videoUrl == null || videoUrl.equals("null"))
                videoUrl = "";
            String imgStringList = getJsonObject.getString("goodsImgList");
            JSONArray imgJsonArray = new JSONArray(imgStringList);
            int maxImage = imgJsonArray.length();
            /**
             * 设置轮播图 goodsImgList
             */
            List<String> images = new ArrayList<>();
            String postStr[] = new String[maxImage];
            for (int i = 0; i < maxImage; i++) {
                postStr[i] = "IMG_URL";
            }
            int imageSize = 0;

            for (int i = 0; i < maxImage; i++) {
                if (!imgJsonArray.getJSONObject(i).getString(postStr[i]).equals("null")) {
                    imageSize++;
                }
            }
            intentImageArray = new String[imageSize];
            for (int i = 0; i < maxImage; i++) {
                if (!imgJsonArray.getJSONObject(i).getString(postStr[i]).equals("null")) {
                    images.add(imgJsonArray.getJSONObject(i).getString(postStr[i]));
                    intentImageArray[i] = imgJsonArray.getJSONObject(i).getString(postStr[i]);
                }
            }
            if (!img.isEmpty() && !videoUrl.isEmpty()) {
                images.add(0, img);
                String imgarray[] = new String[intentImageArray.length + 1];
                imgarray[0] = img;
                System.arraycopy(intentImageArray, 0, imgarray, 1, intentImageArray.length);
                intentImage.addAll(Arrays.asList(imgarray));
            } else {
                intentImage.addAll(Arrays.asList(intentImageArray));
            }
            String startSalePrice = getJsonObject.getString("GOODS_START_PRICE"); // 原价
            mGoodsDetailsOldPrice.setText("RM " + startSalePrice);
            mGoodsDetailsOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            // 商品价格
            mGoodsDetailsNewPrice.setText(getJsonObject.getString("GOODS_START_PRICE"));
            String systemTime = getJsonObject.getString("SYS_TIME"); // 系统时间
            String startTime = getJsonObject.getString("MARKET_START_TIME"); // 起售时间 单位秒
            String descTime = getJsonObject.getString("GOODS_DOWN_TIME"); // 降价时间
            String descType = getJsonObject.getString("GOODS_DOWN_TYPE"); // 降价类型
            String descValue = getJsonObject.getString("GOODS_DOWN_VALUE"); // 降价数值 或 降价比例
            String miniPrice = getJsonObject.getString("GOODS_MINI_PRICE"); // 最低价
            Log.e("aa", "---------降价类型: " + descType);
            Log.e("aa", "---------降价数值或降价比例: " + descValue);
            Log.e("aa", "---------最低价: " + miniPrice);
            Log.e("aa", "---------降价时间: " + descTime);
            Log.e("aa", "---------系统时间: " + systemTime);
            Log.e("aa", "---------起售时间: " + startTime);
            // 系统时间大于服务器开始降价时间 即 已经处于降价
            if (Long.parseLong(systemTime) > Long.parseLong(startTime)) { // 正在降价
                // 按金额降价：原价-（系统时间-起售时间）/降价时间*降价金额《最低价=最低价
                if (descType.equals("1")) {
                    double secondsAll = (Long.parseLong(systemTime) - Long.parseLong(startTime)); // 距离降价开始还有多少秒
                    Log.e("aa", "---------系统时间与起售时间的差值A: " + String.valueOf(secondsAll));
                    double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期
                    Log.e("aa", "---------差值A除以降价时间得到多少个降价区间值: " + String.valueOf(descTimes));
                    BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
                    double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double descPrice = descTimesReal * Double.valueOf(descValue); // 降价周期 * 单位降价金额
                    Log.e("aa", "---------降价区间值保留两位小数: " + String.valueOf(descTimesReal));
                    Log.e("aa", "---------降价具体数值: " + String.valueOf(descPrice));


                    int descTimesRealComplete = (int) descTimesReal; // 降价周期取整 表示降了多少次
                    Log.e("aa", "---------descTimesRealComplete: " + String.valueOf(descTimesRealComplete));
                    double descPriceComplete = descTimesRealComplete * Double.valueOf(descValue);
                    // 得到降价后的具体金额A 开始价格 - 降价周期*(降价比例*原价)
                    Log.e("aa", "---------descPriceComplete: " + String.valueOf(descPriceComplete));

                    // 得到降价后的具体金额A 开始价格 - (降价周期 * 单位降价金额)
                    double nowPrice = Double.valueOf(startSalePrice) - descPrice;
                    Log.e("aa", "---------现在的价格: " + String.valueOf(nowPrice));

                    // 得到降价后的具体金额A 开始价格 - 单位降价金额 降价周期到了才降价
                    //                    double nowPrice = Double.valueOf(startSalePrice) - Double.valueOf(descValue);

                    if (nowPrice < Double.valueOf(miniPrice) || nowPrice < 0) { // 如果小于等于最低价 现在的价就是计算得到的价A
                        Log.e("aa", "--------走计时器");
                        BigDecimal bigDecimalPay = new BigDecimal(miniPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        //                        mGoodsDetailsNewPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        homeTime = "0";
                        mGoodTimeHour.setText(" " + "00" + " ");
                        mGoodTimeMinute.setText(" " + "00" + " ");
                        mGoodTimeSecond.setText(" " + "00" + " ");
                        mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价

                    } else { // 如果大于最低价 现在的价就是最低价
                        Log.e("aa", "--------如果大于最低价");
                        BigDecimal bigDecimalPay = new BigDecimal(nowPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        //                        mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价

                        mGoodsDetailsNewPrice.setText(String.format("%.2f", Double.valueOf(startSalePrice) - descPriceComplete)); // 四舍五入 保留2位

                        //                        mGoodsDetailsNewPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        // 代售商品无倒计时 首页和正在降价有倒计时
                        if (goodsType.equals("base")) {
                            // 倒计时
                            // 取降价周期的小数点后两位
                            //                            String timesString = descTimesReal + "";
                            String timesString = descTimes + "";
                            String timeCounterString = timesString.substring(timesString.indexOf("."), timesString.length());
                            if (timeCounterString.equals("00")) { // 数值达到降价时间区间的首尾极限
                                timerTotal = Long.parseLong(descTime);
                            } else { // 数值在降价时间区间内
                                // 得到比如【1, 110】中的某个具体数值
                                double timesCounter = (Double.valueOf(timeCounterString) * Double.valueOf(descTime)); // 单位秒
                                String timesValue = String.valueOf((int) (Double.valueOf(descTime) - timesCounter)) + "000"; // 单位转换
                                timerTotal = Long.parseLong(timesValue); // 单位毫秒
                            }
                            Log.e("aa", "---------倒计时的总时间: " + timerTotal);
                            // 倒计时开始
                           /* long timeStartTime;
                            Log.e("OkHttp", "11111: " + timerTotal);
                            if (type.equals("home")) { // 首页第一个商品点击进入
                                timeStartTime = Long.parseLong(value);
                                Log.e("OkHttp", "2222: " + timeStartTime);
                            } else {
                                timeStartTime = timerTotal;
                                Log.e("OkHttp", "3333: " + timeStartTime);
                            }
                            if (timeStartTime == 0) {
                                homeTime = "0";
                                mGoodTimeHour.setText(" " + "00" + " ");
                                mGoodTimeMinute.setText(" " + "00" + " ");
                                mGoodTimeSecond.setText(" " + "00" + " ");
                            } else {*/
                            //                                Log.e("OkHttp", "4444: " + timeStartTime);
                            mTimer = new CountDownTimer(timerTotal, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    String allTime = secondsToTime(millisUntilFinished / 1000);
                                    homeTime = allTime;
                                    mGoodTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                    mGoodTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                    mGoodTimeSecond.setText(" " + allTime.substring(6, 8) + " ");
                                }

                                @Override
                                public void onFinish() {
                                    homeTime = "0";
                                    mGoodTimeHour.setText(" " + "00" + " ");
                                    mGoodTimeMinute.setText(" " + "00" + " ");
                                    mGoodTimeSecond.setText(" " + "00" + " ");
                                    //                                doRefresh();
                                    mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价
                                }
                            };
                            mTimer.start();
                        } else {
                            homeTime = "0";
                            mGoodTimeHour.setText(" " + "00" + " ");
                            mGoodTimeMinute.setText(" " + "00" + " ");
                            mGoodTimeSecond.setText(" " + "00" + " ");
                        }
                    }

                } else {
                    Log.e("aa", "--------------==========");
                    // 按比率降价：原价-（系统时间-起售时间）/降价时间*（原价*降价金额）《最低价=最低价
                    double secondsAll = (Long.parseLong(systemTime) - Long.parseLong(startTime)); // 距离降价开始还有多少秒
                    Log.e("789", "系统时间与起售时间的差值A: " + String.valueOf(secondsAll));
                    double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期
                    Log.e("789", "差值A除以降价时间得到多少个降价区间值: " + String.valueOf(descTimes));
                    BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
                    double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    // 降价周期*(降价比例*原价)
                    double descPrice = descTimesReal * ((Double.valueOf(startSalePrice)) * (Double.valueOf(descValue)));
                    // 得到降价后的具体金额A 开始价格 - 降价周期*(降价比例*原价)
                    Log.e("789", "降价区间值保留两位小数: " + String.valueOf(descTimesReal));
                    Log.e("789", "降价具体数值: " + String.valueOf(descPrice));


                    // 降价周期*(降价比例*原价)
                    int descTimesRealComplete = (int) descTimesReal; // 降价周期取整 表示降了多少次
                    Log.e("789", "descTimesRealComplete: " + String.valueOf(descTimesRealComplete));
                    double descPriceComplete = descTimesRealComplete * ((Double.valueOf(startSalePrice)) * (Double.valueOf(descValue)));
                    // 得到降价后的具体金额A 开始价格 - 降价周期*(降价比例*原价)
                    Log.e("789", "descPriceComplete: " + String.valueOf(descPriceComplete));

                    double nowPrice = Double.valueOf(startSalePrice) - descPrice;
                    Log.e("789", "现在的价格: " + String.valueOf(nowPrice));

                    // 得到降价后的具体金额A 开始价格 - 单位降价金额 降价周期到了才降价
                    //                    double nowPrice = Double.valueOf(startSalePrice) - Double.valueOf(descValue);

                    if (nowPrice < Double.valueOf(miniPrice) || nowPrice < 0) { // 如果小于等于最低价 现在的价就是计算得到的价A
                        Log.e("aa", "-------------傻逼-");
                        BigDecimal bigDecimalPay = new BigDecimal(miniPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        //                        mGoodsDetailsNewPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        homeTime = "0";
                        mGoodTimeHour.setText(" " + "00" + " ");
                        mGoodTimeMinute.setText(" " + "00" + " ");
                        mGoodTimeSecond.setText(" " + "00" + " ");
                        mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价
                    } else { // 如果大于最低价 现在的价就是最低价
                        Log.e("aa", "-------------傻逼1111-");
                        BigDecimal bigDecimalPay = new BigDecimal(nowPrice);
                        double payPriceReal = bigDecimalPay.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        nowPriceServer = String.format("%.2f", payPriceReal); // 保留2位
                        //                        mGoodsDetailsNewPrice.setText(String.valueOf(payPriceReal)); // 四舍五入 保留2位
                        //                        mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价


                        mGoodsDetailsNewPrice.setText(String.format("%.2f", Double.valueOf(startSalePrice) - descPriceComplete)); // 四舍五入 保留2位


                        // 代售商品无倒计时 首页和正在降价有倒计时
                        if (goodsType.equals("base")) {
                            Log.e("aa", "-------------222222-");
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
                            Log.e("789", "倒计时的总时间: " + timerTotal);
                            // 倒计时开始
                            long timeStartTime;
                            Log.e("OkHttp", "111112: " + timerTotal);
                            Log.e("OkHttp", "222269696: " + type);
                            Log.e("OkHttp", "222269697: " + value);
                            if (type.equals("home")) { // 首页第一个商品点击进入
                                timeStartTime = Long.parseLong(value);
                                Log.e("OkHttp", "22224: " + timeStartTime);
                            } else {
                                timeStartTime = timerTotal;
                                Log.e("OkHttp", "33332: " + timeStartTime);
                            }
                            if (timeStartTime == 0) {//开始即使kwkf
                                homeTime = "0";
                                mGoodTimeHour.setText(" " + "00" + " ");
                                mGoodTimeMinute.setText(" " + "00" + " ");
                                mGoodTimeSecond.setText(" " + "00" + " ");


                            } else {
                                Log.e("OkHttp", "444442: " + timeStartTime);
                                CountDownTimer mTimer = new CountDownTimer(timeStartTime, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        String allTime = secondsToTime(millisUntilFinished / 1000);
                                        homeTime = allTime;
                                        mGoodTimeHour.setText(" " + allTime.substring(0, 2) + " ");
                                        mGoodTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
                                        mGoodTimeSecond.setText(" " + allTime.substring(6, 8) + " ");
                                    }

                                    @Override
                                    public void onFinish() {
                                        homeTime = "0";
                                        mGoodTimeHour.setText(" " + "00" + " ");
                                        mGoodTimeMinute.setText(" " + "00" + " ");
                                        mGoodTimeSecond.setText(" " + "00" + " ");
                                        //                                doRefresh();
                                        mGoodsDetailsNewPrice.setText(nowPriceServer); // 现价
                                    }
                                };
                                mTimer.start();
                            }

                        } else {
                            Log.e("aa", "-------------333-");
                            homeTime = "0";
                            mGoodTimeHour.setText(" " + "00" + " ");
                            mGoodTimeMinute.setText(" " + "00" + " ");
                            mGoodTimeSecond.setText(" " + "00" + " ");
                        }
                    }

                }
            } else {  //改
//                // 系统时间小于服务器开始降价时间 即 还没有处于降价
//                // 代售商品无倒计时
//                if (goodsType.equals("base")) {
//                    // 倒计时
//                    double secondsAll = (Long.parseLong(startTime) - Long.parseLong(systemTime)) / 1000; // 距离降价开始还有多少秒
//                    double descTimes = secondsAll / Long.parseLong(descTime); // 得到多少个降价周期
//                    BigDecimal bigDecimalPayTimes = new BigDecimal(descTimes);
//                    double descTimesReal = bigDecimalPayTimes.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//                    // 取降价周期的小数点后两位
//                    String timesString = descTimesReal + "";
//                    String timeCounterString = timesString.substring(timesString.indexOf("."), timesString.length());
//                    if (timeCounterString.equals("00")) { // 数值达到降价时间区间的首尾极限
//                        timerTotal = Long.parseLong(descTime);
//                    } else { // 数值在降价时间区间内
//                        // 得到比如【1, 110】中的某个具体数值
//                        double timesCounter = (Double.valueOf(timeCounterString) * Double.valueOf(descTime)); // 单位秒
//                        String timesValue = String.valueOf((int) (Double.valueOf(descTime) - timesCounter)) + "000"; // 单位转换
//                        timerTotal = Long.parseLong(timesValue); // 单位毫秒
//                    }
//                    // 倒计时开始
//                    CountDownTimer mTimer = new CountDownTimer(timerTotal, 1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                            String allTime = secondsToTime(millisUntilFinished / 1000);
//                            mGoodTimeHour.setText(" " + allTime.substring(0, 2) + " ");
//                            mGoodTimeMinute.setText(" " + allTime.substring(3, 5) + " ");
//                            mGoodTimeSecond.setText(" " + allTime.substring(6, 8) + " ");
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            mGoodTimeHour.setText(" " + "00" + " ");
//                            mGoodTimeMinute.setText(" " + "00" + " ");
//                            mGoodTimeSecond.setText(" " + "00" + " ");
//                            doRefresh();
//                        }
//                    };
//                    mTimer.start();
//                } else {
//                    mGoodTimeHour.setText(" " + "00" + " ");
//                    mGoodTimeMinute.setText(" " + "00" + " ");
//                    mGoodTimeSecond.setText(" " + "00" + " ");
//                }
//                homeTime = "0";
//                mGoodTimeHour.setText(" " + "00" + " ");
//                mGoodTimeMinute.setText(" " + "00" + " ");
//                mGoodTimeSecond.setText(" " + "00" + " ");
                Log.e("aa", "------------走这");
            }
            mGoodsDetailsDetailsDefault.setText(getJsonObject.getString("GOODS_DETAIL"));
            //            Log.e("789", "loadPage: " + getJsonObject.getString("GOODS_DETAIL"));
            //            mGoodsDetailsDetailsDefault.setText("123");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list.clear();
        initDataA();

        initData(); // 初始化数据
        setListener(); // 设置监听器
    }

    /**
     * 获取产品详情内部类
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.goods_details_url, params[0],
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
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "获取产品详情的返回结果：" + data.toString());
                    //                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    String infoString = data.getString("data");
                    JSONObject jsonObject = new JSONObject(infoString);
                    loadPage(jsonObject);
                } catch (Exception e) {
                    //Toast.makeText(SettingsActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
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

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
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
     * 点击事件
     */
    @OnClick({R.id.goods_details_back, R.id.goods_details_go_msg, R.id.goods_details_btn_buy,
            R.id.goods_details_share, R.id.banner_rl,
            R.id.goods_details_transparent})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.goods_details_back:
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                finish();
                break;
            // 查看大图
            case R.id.banner_rl:
            case R.id.goods_details_transparent:
                /*if (bannerPosition == 0 || bannerPosition == intentImage.length) { // 第一张图片
                    if (videoUrl == null || videoUrl.equals("null")) { // 无视频
                        Intent intent = new Intent(mBaseActivity, ShowBannerActivity.class);
                        intent.putExtra("intentImageArray", intentImage);
                        startActivity(intent);
                    } else { // 有视频

                    }
                } else {
                    Intent intent = new Intent(mBaseActivity, ShowBannerActivity.class);
                    intent.putExtra("intentImageArray", intentImage);
                    startActivity(intent);
                }*/
                break;
            // 联系客服
            case R.id.goods_details_go_msg:
                ChatClient.getInstance().addConnectionListener(new MyConnectionListener(mBaseActivity));
                if (isLogin()) {
                    // 点击“在线客服”按钮的时候，判断是否已登录环信
                    if (ChatClient.getInstance().isLoggedInBefore()) {
                        Intent intent = new IntentBuilder(mBaseActivity)
                                .setTargetClass(ChatActivity.class)
                                .setTitleName("details")
                                .setServiceIMNumber("kefuchannelimid_505678")
                                .build();
                        startActivity(intent);
                    } else {
                        ToastUtils.show(mBaseActivity, "The account is logged in on other devices OR User does not exist");
                    }
                } else {
                    Intent intentA = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentA.putExtra("from", "goods");
                    intentA.putExtra("goodsId", goodsId);
                    intentA.putExtra("goodsType", goodsType);
                    intentA.putExtra("homeTime", homeTime);
                    startActivity(intentA);
                }
                break;
            // 购买
            case R.id.goods_details_btn_buy:
                if (!ClickUtils.INSTANCE.isFastDoubleClick()) {
                    mGoodsDetailsBtnBuy.setClickable(true);
                    if (isLogin()) {
                        //                    if (isDelete.equals("1")) {
                        //                        ToastUtils.show(mBaseActivity, "该商品已下架，请重新选择");
                        //                        finish();
                        //                    } else {

                        // 先刷新
                        //                    doRefresh();
                        mGoodsDetailsBtnBuy.setClickable(false);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                        // 获取当前时间 2018-12-25 12:12:12
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

                        final String payPrice = mGoodsDetailsNewPrice.getText().toString();
                        goodsPrice = payPrice;
                        // 获取 User 信息
                        IDataStorage dataStorage = DataStorageFactory.getInstance(
                                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                        mUser = dataStorage.load(User.class, "User");
                        HashMap<String, String> params = new HashMap<>();
                        params.put("goodsId", goodsId);
                        params.put("memberId", mUser.userID);
                        params.put("token", mUser.token);
                        params.put("clientTime", clientTime);
                        params.put("clientPrice", payPrice);
                        OrderTask mOrderTask = new OrderTask();
                        mOrderTask.execute(params);
                        //                    }
                    } else {
                        ToastUtils.show(mBaseActivity, "Please Login");
                        Intent intentA = new Intent(mBaseActivity, LoginNewActivity.class);
                        intentA.putExtra("from", "goods");
                        intentA.putExtra("goodsId", goodsId);
                        intentA.putExtra("goodsType", goodsType);
                        intentA.putExtra("homeTime", homeTime);
                        startActivity(intentA);
                    }
                }
                break;
            // 分享
            case R.id.goods_details_share:
                mSharePopupWindow = new SharePopupWindow(mBaseActivity, new View.OnClickListener() {
                    // 微信
                    @Override
                    public void onClick(View v) {
                        mSharePopupWindow.dismiss();
                        if (MyUtils.isWeixinAvilible(mBaseActivity)) {
                            // 初始化一个WXWebpageObject，填写url
                            WXWebpageObject webpage = new WXWebpageObject();
                            webpage.webpageUrl = "http://www.jimat.net/";

                            // 用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
                            WXMediaMessage msg = new WXMediaMessage(webpage);
                            msg.title = "jimat ";
                            msg.description = "我在JIMAT发现好物啦~";
                            Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                            msg.thumbData = BmpToByteArray.bmpToByteArray(thumbBmp, true);

                            // 构造一个Req
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = String.valueOf(System.currentTimeMillis());
                            req.message = msg;
                            req.scene = WXSceneSession;

                            // 调用api接口，发送数据到微信
                            MyApplication.weiXinApi.sendReq(req);
                        } else {
                            ToastUtils.show(mBaseActivity, "No WeChat");
                        }
                    }
                    // 朋友圈
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharePopupWindow.dismiss();
                        if (MyUtils.isWeixinAvilible(mBaseActivity)) {
                            // 初始化一个WXWebpageObject，填写url
                            WXWebpageObject webpage = new WXWebpageObject();
                            webpage.webpageUrl = "http://www.jimat.net/";

                            // 用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
                            WXMediaMessage msg = new WXMediaMessage(webpage);
                            msg.title = "jimat ";
                            msg.description = "我在JIMAT发现好物啦~";
                            Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                            msg.thumbData = BmpToByteArray.bmpToByteArray(thumbBmp, true);

                            // 构造一个Req
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = String.valueOf(System.currentTimeMillis());
                            req.message = msg;
                            req.scene = WXSceneTimeline;

                            // 调用api接口，发送数据到微信
                            MyApplication.weiXinApi.sendReq(req);
                        } else {
                            ToastUtils.show(mBaseActivity, "No WeChat");
                        }
                    }
                });
                View rootView = LayoutInflater.from(mBaseActivity)
                        .inflate(R.layout.activity_goods_details, null);
                mSharePopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
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
                    final String goodsPrice = mGoodsDetailsNewPrice.getText().toString();
                    mGoodsDetailsBtnBuy.setClickable(true);
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

    /**
     * Glide 图片加载类
     */
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
        finish();
        if (mTimer != null) {
            mTimer.cancel();
        }
        return true;
    }
}
