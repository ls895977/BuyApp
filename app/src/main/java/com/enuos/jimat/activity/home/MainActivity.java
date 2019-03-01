package com.enuos.jimat.activity.home;

import android.Manifest;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.ChatActivity;
import com.enuos.jimat.activity.home.newInfo.HomeNewActivity;
import com.enuos.jimat.activity.home.newInfo.MineNewActivity;
import com.enuos.jimat.adapter.MainAdapter;
import com.enuos.jimat.adapter.NoScrollViewPager;
import com.enuos.jimat.inter.OnTabActivityResultListener;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.PrefUtils;
import com.enuos.jimat.utils.easeui.MyConnectionListener;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_tab)
    PageNavigationView mTabLayout;
    @BindView(R.id.main_pager)
    NoScrollViewPager mViewPager;

    public static final int PAGE_HOME = 0;
    public static final int PAGE_Message = 1;
    public static final int PAGE_MINE = 2;

    private NavigationController mController;

    private long mPressedTime = 0;

    /**
     * 在这个案例中，我们需要往 ViewPager 中添加 Activity，但是 ViewPager 加载的是 View 对象
     * 所以在这里我们需要把 Activity 转化为 View 对象，因此需要 LocalActivityManager 类
     * LocalActivityManager 类是管理 Activity 的，然后通过 startActivity() 这个方法获取当前 Window 对象
     * 再调用 getDecorView() 方法获取当前 Activity 对应的 View
     */
    private LocalActivityManager mManager;

    private String itemIntent, goodsType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mManager = new LocalActivityManager(this, true);
        mManager.dispatchCreate(savedInstanceState);

        mTabLayout.setBackgroundColor(Color.argb(100, 246, 246, 246));
        mController = mTabLayout.custom()
                .addItem(newItem(R.drawable.home_normal, R.drawable.home_selected, ""))
                .addItem(newItem(R.drawable.message_normal, R.drawable.message_selected, ""))
                .addItem(newItem(R.drawable.mine_normal, R.drawable.mine_selected, ""))
                .build();

        setListener();
        addActivities();
//        mViewPager.setCurrentItem(0);
        itemIntent = getIntent().getStringExtra("item");
        goodsType = getIntent().getStringExtra("goodsType");
        Log.e("OkHttp", "goodsType111111: " + goodsType);
        Log.e("OkHttp", "itemIntent22222: " + itemIntent);
//        mViewPager.setCurrentItem(Integer.parseInt(itemIntent));
        if (itemIntent.equals("0")) {
            mViewPager.setCurrentItem(PAGE_HOME);
            mController.setSelect(PAGE_HOME);
        } else if (itemIntent.equals("1")) {
//            mViewPager.setCurrentItem(PAGE_Message);
//            mController.setSelect(PAGE_Message);
//            Log.e("OkHttp", "itemIntent333: " + itemIntent);
//            Log.e("OkHttp", "goodsType2222222: " + goodsType);


//            Intent intent = new IntentBuilder(mBaseActivity)
//                    .setTargetClass(ChatActivity.class)
//                    .setTitleName(goodsType)
//                    .setServiceIMNumber("kefuchannelimid_505678")
//                    .build();
//            startActivity(intent);
//            finish();
        } else {
            mViewPager.setCurrentItem(PAGE_MINE);
            mController.setSelect(PAGE_MINE);
        }
        permissionRequest();

    }

    /*public void onResume() {
        itemIntent = getIntent().getStringExtra("item");
        Log.e("OkHttp", "itemIntent22222: " + itemIntent);
//        mViewPager.setCurrentItem(Integer.parseInt(itemIntent));
        if (itemIntent.equals("0")) {
            mViewPager.setCurrentItem(PAGE_HOME);
            mController.setSelect(PAGE_HOME);
        } else if (itemIntent.equals("1")) {
//            mViewPager.setCurrentItem(PAGE_Message);
//            mController.setSelect(PAGE_Message);
        } else {
            mViewPager.setCurrentItem(PAGE_MINE);
            mController.setSelect(PAGE_MINE);
        }
        super.onResume();
    }*/

    /**
     * 自定义Item
     */
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        normalItemView.setTextDefaultColor(ContextCompat.getColor(mBaseActivity, R.color.color_4A4A4A));
        normalItemView.setTextCheckedColor(ContextCompat.getColor(mBaseActivity, R.color.color_B63132));
        return normalItemView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取当前活动的Activity实例
        Activity subActivity = mManager.getCurrentActivity();
        // 判断是否实现返回值接口
        if (subActivity instanceof OnTabActivityResultListener) {
            // 获取返回值接口实例
            OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
            // 转发请求到子Activity
            listener.onTabActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 权限申请
     */
    private void permissionRequest() {
        if(Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
//            if (ContextCompat.checkSelfPermission(mBaseActivity,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED){
//                permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.READ_LOGS)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.READ_LOGS);
            }
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.WAKE_LOCK)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            }
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            }
            if (ContextCompat.checkSelfPermission(mBaseActivity,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.CAMERA);
            }
//            if (ContextCompat.checkSelfPermission(mBaseActivity,
//                    Manifest.permission.CALL_PHONE)
//                    != PackageManager.PERMISSION_GRANTED){
//                permissionsList.add(Manifest.permission.CALL_PHONE);
//            }
            ActivityCompat.requestPermissions(mBaseActivity,
                    permissionsList.toArray(new String[permissionsList.size()]), 123);
        }
    }

    /**
     * 设置监听器
     */
    private void setListener() {

        mController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                switch (index) {
                    case PAGE_HOME:
                        mViewPager.setCurrentItem(PAGE_HOME);
                        mController.setSelect(PAGE_HOME);
                        break;
                    case PAGE_Message:
                        ChatClient.getInstance().addConnectionListener(new MyConnectionListener(mBaseActivity));
                        if (isLogin()) {
                            // 点击“在线客服”按钮的时候，判断是否已登录环信
                            if (ChatClient.getInstance().isLoggedInBefore() || goodsType.equals("1")) {
                                Log.e("OkHttp", "goodsType333333: " + goodsType);
//                                mViewPager.setCurrentItem(PAGE_Message);
//                                mController.setSelect(PAGE_Message);
                                Intent intent = new IntentBuilder(mBaseActivity)
                                        .setTargetClass(ChatActivity.class)
                                        .setTitleName(String.valueOf(mViewPager.getCurrentItem()))
                                        .setServiceIMNumber("kefuchannelimid_505678")
                                        .build();
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("OkHttp", "goodsType444444: " + goodsType);
                                ToastUtils.show(mBaseActivity, "The account is logged in on other devices OR User does not exist");
                            }
                        } else {
//                            String currentItem;
//                            if (mViewPager.getCurrentItem() == 1) {
//                                currentItem = itemIntent;
//                            } else {
//                                currentItem = String.valueOf(mViewPager.getCurrentItem());
//                            }
                            Log.e("OkHttp", "444444: " + String.valueOf(mViewPager.getCurrentItem()));
                            Intent intentA = new Intent(mBaseActivity, LoginNewActivity.class);
                            intentA.putExtra("from", "message");
                            intentA.putExtra("goodsId", "");
                            intentA.putExtra("goodsType", String.valueOf(mViewPager.getCurrentItem()));
                            startActivity(intentA);
                            finish();
                        }
                        break;
                    case PAGE_MINE:
                        mViewPager.setCurrentItem(PAGE_MINE);
                        mController.setSelect(PAGE_MINE);
                        break;
                }
            }

            @Override
            public void onRepeat(int index) { }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mController.setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addActivities() {

        List<View> views = new ArrayList<>();
        Intent intent = new Intent();

        intent.setClass(mBaseActivity, HomeNewActivity.class);
        views.add(getView("activity_home_new", intent));

        intent.setClass(mBaseActivity, MessageActivity.class);
        views.add(getView("activity_message", intent));

//        intent.setClass(mBaseActivity, ChatActivity.class);
//        views.add(getView("activity_chat", intent));

        intent.setClass(mBaseActivity, MineNewActivity.class);
        views.add(getView("activity_mine_new", intent));

        mViewPager.setAdapter(new MainAdapter(views));
    }

    /**
     * LocalActivityManager 提供了一个重要方法 startActivity()
     * 该方法正是利用主线程 mActivityThread 去装载指定的 Activity
     */
    private View getView(String id, Intent intent) {
        return mManager.startActivity(id, intent).getDecorView();
    }

    /**
     * 双击退出
     */
    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis(); // 获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) { // 比较两次按键时间差
            ToastUtils.show(mBaseActivity, "Press again to exit the program");
            mPressedTime = mNowTime;
        } else { // 退出程序
            PrefUtils.setBoolean(mBaseActivity, "isLogin", false);
            Intent intent = new Intent("com.enuos.exit");
            sendBroadcast(intent);
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
        if (user != null && !user.userAccount.equals("") ) {
            return true;
        }
        return false;
    }

}
