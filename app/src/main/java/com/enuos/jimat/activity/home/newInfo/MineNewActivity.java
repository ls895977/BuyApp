package com.enuos.jimat.activity.home.newInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.account.newInfo.RegisterNewActivity;
import com.enuos.jimat.activity.address.MineAddressActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.WebActivity;
import com.enuos.jimat.activity.mine.MineInfoActivity;
import com.enuos.jimat.activity.mine.MineSetActivity;
import com.enuos.jimat.activity.mine.history.MineHistoryActivity;
import com.enuos.jimat.activity.money.MineMoneyActivity;
import com.enuos.jimat.activity.msg.MineMsgActivity;
import com.enuos.jimat.activity.order.MineOrderActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.ClickUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineNewActivity extends BaseActivity {

    @BindView(R.id.mine_head_icon_new)
    RoundedImageView   mMineHeadIconNew;
    @BindView(R.id.mine_new_head_icon_pic)
    RoundedImageView   mMineNewHeadIconPic;
    @BindView(R.id.mine_new_user_go_login)
    TextView           mMineNewUserGoLogin;
    @BindView(R.id.mine_new_user_go_view)
    View               mMineNewUserGoView;
    @BindView(R.id.mine_new_user_go_register)
    TextView           mMineNewUserGoRegister;
    @BindView(R.id.mine_new_user_account)
    TextView           mMineNewUserAccount;
    @BindView(R.id.mine_new_linear_order)
    LinearLayout       mMineNewLinearOrder;
    @BindView(R.id.mine_new_linear_money)
    LinearLayout       mMineNewLinearMoney;
    @BindView(R.id.mine_new_linear_address)
    LinearLayout       mMineNewLinearAddress;
    @BindView(R.id.mine_new_linear_info)
    LinearLayout       mMineNewLinearInfo;
    @BindView(R.id.mine_new_linear_msg)
    LinearLayout       mMineNewLinearMsg;
    @BindView(R.id.mine_new_linear_history)
    LinearLayout       mMineNewLinearHistory;
    @BindView(R.id.mine_new_text_phone)
    TextView           mMineNewTextPhone;
    @BindView(R.id.mine_new_linear_phone)
    LinearLayout       mMineNewLinearPhone;
    @BindView(R.id.mine_new_linear_protocl)
    LinearLayout       mMineNewLinearProtocl;
    @BindView(R.id.mine_new_linear_set)
    LinearLayout       mMineNewLinearSet;
    @BindView(R.id.mine_new_swipe_refresh)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.mine_new_img_msg)
    ImageView          mMineNewImgMsg;

    private SweetAlertDialog mProgressDialog;
    private String           account, headImage, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_new);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);
        // 进入界面后先获取个人信息
        if (isLogin()) {
            mMineNewUserAccount.setVisibility(View.VISIBLE);
            mMineNewUserGoLogin.setVisibility(View.GONE);
            mMineNewUserGoView.setVisibility(View.GONE);
            mMineNewUserGoRegister.setVisibility(View.GONE);
        } else {
            mMineNewUserAccount.setVisibility(View.GONE);
            mMineNewUserGoLogin.setVisibility(View.VISIBLE);
            mMineNewUserGoView.setVisibility(View.VISIBLE);
            mMineNewUserGoRegister.setVisibility(View.VISIBLE);
        }
        setSwipe();
        doRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_LOGIN)) {
            doRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitEvent(String message) {
        if (message.equals(EventConfig.EVENT_EXIT)) {
            mMineNewUserAccount.setVisibility(View.GONE);
            mMineNewUserGoLogin.setVisibility(View.VISIBLE);
            mMineNewUserGoView.setVisibility(View.VISIBLE);
            mMineNewUserGoRegister.setVisibility(View.VISIBLE);

            mMineNewImgMsg.setVisibility(View.GONE);

            Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineNewHeadIconPic);
        }
    }

    @Override
    protected void onResume() {
        doRefresh();
        super.onResume();
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
        mSwipe.setColorSchemeColors(ContextCompat.getColor(mBaseActivity, R.color.blue_btn_bg_color));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    /**
     * 获得个人信息
     */
    private void doRefresh() {
        mSwipe.setRefreshing(true);
        if (isLogin()) {
            mMineNewUserAccount.setVisibility(View.VISIBLE);
            mMineNewUserGoLogin.setVisibility(View.GONE);
            mMineNewUserGoView.setVisibility(View.GONE);
            mMineNewUserGoRegister.setVisibility(View.GONE);

            IDataStorage dataStorage = DataStorageFactory.getInstance(
                    getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
            String userID = dataStorage.load(User.class, "User").userID;
            String userAccount = dataStorage.load(User.class, "User").userAccount;
            String token = dataStorage.load(User.class, "User").token;
            // 获取用户个人信息
            UserMsgTask task = new UserMsgTask();
            HashMap<String, String> params = new HashMap<>();
            params.put("memberId", userID);
            params.put("token", token);
            params.put("memberMobile", userAccount);
            task.execute(params);
        } else {
            mMineNewUserAccount.setVisibility(View.GONE);
            mMineNewUserGoLogin.setVisibility(View.VISIBLE);
            mMineNewUserGoView.setVisibility(View.VISIBLE);
            mMineNewUserGoRegister.setVisibility(View.VISIBLE);

            mMineNewImgMsg.setVisibility(View.GONE);

            Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineNewHeadIconPic);
        }
        mSwipe.setRefreshing(false);
    }
    String phoneToast;
    /**
     * 加载页面所有的视图元素
     */
    private void loadPage(JSONObject getJsonObject) {
        try {
            IDataStorage dataStorage = DataStorageFactory.getInstance(
                    getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
            account = dataStorage.load(User.class, "User").userAccount;
            // 隐藏手机号中间4位
            Log.e("aa","---account----"+account);
            if(account.length()==11) {
                 phoneToast = account.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }else if(account.length()==10) {
                phoneToast = account.replaceAll("(\\d{3})\\d{4}(\\d{3})", "$1****$2");
            }else if(account.length()==8){
                phoneToast = account.replaceAll("(\\d{3})\\d{3}(\\d{3})", "$1****$2");
            }else if(account.length()==9){
                phoneToast = account.replaceAll("(\\d{3})\\d{3}(\\d{3})", "$1****$2");
            }else if(account.length()==12){
                phoneToast = account.replaceAll("(\\d{4})\\d{4}(\\d{4})", "$1****$2");
            }
            mMineNewUserAccount.setText(phoneToast);

            name = getJsonObject.getString("TRUE_NAME");

            headImage = getJsonObject.getString("MEMBER_AVATAR");
            if (headImage.equals("null") || headImage.equals("")) {
                Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineNewHeadIconPic);
            } else {
                Glide.with(mBaseActivity).load(headImage).into(mMineNewHeadIconPic);
            }

            // 通知的红点显示/隐藏
            if (Integer.parseInt(getJsonObject.getString("MESSAG_COUNT")) > 0) {
                mMineNewImgMsg.setVisibility(View.VISIBLE);
            } else {
                mMineNewImgMsg.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户个人信息内部类
     */
    private class UserMsgTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.user_info_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineNewHeadIconPic);
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
                    Log.e("TAG", "获取用户个人信息的返回结果：" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    loadPage(jsonObject);
                    mSwipe.setRefreshing(false);
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
     * 点击事件
     */
    @OnClick({R.id.mine_new_user_go_login, R.id.mine_new_user_go_register, R.id.mine_new_linear_order,
            R.id.mine_new_linear_money, R.id.mine_new_linear_address, R.id.mine_new_linear_info,
            R.id.mine_new_linear_msg, R.id.mine_new_linear_history, R.id.mine_new_linear_phone,
            R.id.mine_new_linear_protocl, R.id.mine_new_linear_set, R.id.mine_new_head_icon_pic})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 去登录
            case R.id.mine_new_user_go_login:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                Intent intentM = new Intent(mBaseActivity, LoginNewActivity.class);
                intentM.putExtra("from", "mine");
                intentM.putExtra("goodsId", "");
                intentM.putExtra("goodsType", "");
                intentM.putExtra("homeTime", "0");
                startActivity(intentM);
                break;
            // 去注册
            case R.id.mine_new_user_go_register:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                startActivity(new Intent(mBaseActivity, RegisterNewActivity.class));
                break;
            // 我的订单
            case R.id.mine_new_linear_order:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineOrderActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentA = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentA.putExtra("from", "mine");
                    intentA.putExtra("goodsId", "");
                    intentA.putExtra("goodsType", "");
                    intentA.putExtra("homeTime", "0");
                    startActivity(intentA);
                }
                break;
            // 我的钱包
            case R.id.mine_new_linear_money:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineMoneyActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentC = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentC.putExtra("from", "mine");
                    intentC.putExtra("goodsId", "");
                    intentC.putExtra("goodsType", "");
                    intentC.putExtra("homeTime", "0");
                    startActivity(intentC);
                }
                break;
            // 收货地址
            case R.id.mine_new_linear_address:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineAddressActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentD = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentD.putExtra("from", "mine");
                    intentD.putExtra("goodsId", "");
                    intentD.putExtra("goodsType", "");
                    intentD.putExtra("homeTime", "0");
                    startActivity(intentD);
                }
                break;
            // 个人资料
            case R.id.mine_new_head_icon_pic:
            case R.id.mine_new_linear_info:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineInfoActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentE = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentE.putExtra("from", "mine");
                    intentE.putExtra("goodsId", "");
                    intentE.putExtra("goodsType", "");
                    intentE.putExtra("homeTime", "0");
                    startActivity(intentE);
                }
                break;
            // 我的通知
            case R.id.mine_new_linear_msg:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineMsgActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentF = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentF.putExtra("from", "mine");
                    intentF.putExtra("goodsId", "");
                    intentF.putExtra("goodsType", "");
                    intentF.putExtra("homeTime", "0");
                    startActivity(intentF);
                }
                break;
            // 历史产品
            case R.id.mine_new_linear_history:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                startActivity(new Intent(mBaseActivity, MineHistoryActivity.class));
                break;
            // 客服电话
            case R.id.mine_new_linear_phone:
                /*final String phone = mMineNewTextPhone.getText().toString();
                final SweetAlertDialog phoneDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("Confirm")
                        .setCancelText("Cancel");
                phoneDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        phoneDialog.dismiss();
                        if (!phone.equals("") && !phone.equals("null")) {
                            startActivity(callNumber(phone.replace("-", "")));
                        }
                    }
                });
                phoneDialog.setTitleText("Are you sure you want to call?");
                phoneDialog.setContentText("consumer hotline：" + phone);
                phoneDialog.show();*/
                Uri uri = Uri.parse("mailto: cs.jimat@outlook.com");
                Intent intentEmail = new Intent(Intent.ACTION_SENDTO, uri);
                this.startActivity(intentEmail);
                break;
            // 协议与声明
            case R.id.mine_new_linear_protocl:
                Intent intent = new Intent(mBaseActivity, WebActivity.class);
                intent.putExtra("title", "Terms of Use");
                intent.putExtra("url", "http://47.254.192.108:8080/jimatInterface/fwxy.html");
                startActivity(intent);
                /*if (isLogin()) {
                    Intent intent = new IntentBuilder(mBaseActivity)
                            .setServiceIMNumber("kefuchannelimid_505678")
                            .build();
                    startActivity(intent);
                } else {
                    startActivity(new Intent(mBaseActivity, LoginNewActivity.class));
                }*/
                break;
            // 设置
            case R.id.mine_new_linear_set:
                if (ClickUtils.INSTANCE.isFastDoubleClick())
                    return;
                if (isLogin()) {
                    startActivity(new Intent(mBaseActivity, MineSetActivity.class));
                } else {
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentG = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentG.putExtra("from", "mine");
                    intentG.putExtra("goodsId", "");
                    intentG.putExtra("goodsType", "");
                    intentG.putExtra("homeTime", "0");
                    startActivity(intentG);
                }
                break;
        }
    }

}
