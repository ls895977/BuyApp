package com.enuos.jimat.activity.account.newInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.goods.GoodsDetailsActivity;
import com.enuos.jimat.activity.home.MainActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.model.bean.UserInfo;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMClient;
import com.hyphenate.helpdesk.callback.Callback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class LoginNewActivity extends BaseActivity {

    @BindView(R.id.login_new_back)
    ImageView mBack;
    @BindView(R.id.login_new_go_register)
    TextView mLoginNewGoRegister;
    @BindView(R.id.login_new_account)
    EditText mLoginNewAccount;
    @BindView(R.id.login_new_password_linear_top)
    LinearLayout mLoginNewPasswordLinearTop;
    @BindView(R.id.login_new_password)
    EditText mLoginNewPassword;
    @BindView(R.id.login_new_password_is_hide)
    ImageView mLoginNewPasswordIsHide;
    @BindView(R.id.login_new_password_linear_bottom)
    LinearLayout mLoginNewPasswordLinearBottom;
    @BindView(R.id.login_new_sms_linear_top)
    LinearLayout mLoginNewSmsLinearTop;
    @BindView(R.id.login_new_sms)
    EditText mLoginNewSms;
    @BindView(R.id.login_new_sms_btn_get)
    Button mLoginNewSmsBtnGet;
    @BindView(R.id.login_new_sms_linear_bottom)
    LinearLayout mLoginNewSmsLinearBottom;
    @BindView(R.id.login_new_type_change)
    TextView mLoginNewTypeChange;
    @BindView(R.id.login_new_forget_password)
    TextView mLoginNewForgetPassword;
    @BindView(R.id.login_new_go_login)
    Button mLoginNewGoLogin;

    private LoginPswTask mLoginPswTask;
    private LoginSmsTask mLoginSmsTask;
    private SendCodeTask mSendCodeTask;
    private SweetAlertDialog mProgressDialog;
    private boolean isHideDisplay = true;
    private boolean isLoginPsw = true;
    private String hxCommonAccountHead = "jimataccounts";
    private String hxCommonPswHead = "jimatpassword";
    private String from, goodsId, goodsType, homeTime;

    /**
     * 60秒倒计时
     */
    private CountDownTimer mTimer60 = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mLoginNewSmsBtnGet.setEnabled(false);
            mLoginNewSmsBtnGet.setText("after " + String.valueOf(millisUntilFinished / 1000) + " second");
        }

        @Override
        public void onFinish() {
            mLoginNewSmsBtnGet.setEnabled(true);
            mLoginNewSmsBtnGet.setText("Send Code");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        ButterKnife.bind(this);

        from = getIntent().getStringExtra("from");
        goodsId = getIntent().getStringExtra("goodsId");
        goodsType = getIntent().getStringExtra("goodsType");
        homeTime = getIntent().getStringExtra("homeTime");
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.login_new_back, R.id.login_new_go_register, R.id.login_new_password_is_hide,
            R.id.login_new_sms_btn_get, R.id.login_new_type_change, R.id.login_new_forget_password,
            R.id.login_new_go_login})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.login_new_back:
                if (from.equals("message")) {
                    if (goodsType.equals("0")) {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "0");
                        intent.putExtra("goodsType", "0");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "2");
                        intent.putExtra("goodsType", "0");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            // 去注册
            case R.id.login_new_go_register:
                startActivity(new Intent(mBaseActivity, RegisterNewActivity.class));
                break;
            // 隐藏/显示密码
            case R.id.login_new_password_is_hide:
                if (isHideDisplay) {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img_enable).into(mLoginNewPasswordIsHide);
                    mLoginNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img).into(mLoginNewPasswordIsHide);
                    mLoginNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 发送验证码
            case R.id.login_new_sms_btn_get:
                final String codePhone = mLoginNewAccount.getText().toString();
                if (codePhone.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Mobile Number");
                } else if (codePhone.length() != 11) {
                    ToastUtils.show(mBaseActivity, "Please Enter The Correct Mobile Number");
                } else {
                    // 发送短信验证码
                    mTimer60.start();

                    // 取出token      params.put("token", userToken);
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = dataStorage.load(User.class, "User");
                    String userToken = "";
                    if (user != null && !user.userAccount.equals("")) {
                        userToken = user.token;
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", codePhone);
                    params.put("token", userToken);
                    mSendCodeTask = new SendCodeTask();
                    mSendCodeTask.execute(params);
                }
                break;
            // 切换登录方式
            case R.id.login_new_type_change:
                isLoginPsw = !isLoginPsw;
                if (isLoginPsw) { // 密码登录
                    mLoginNewTypeChange.setText("Mobile Login");
                    // 控件隐藏/显示
                    mLoginNewForgetPassword.setVisibility(View.VISIBLE);
                    mLoginNewPasswordLinearTop.setVisibility(View.VISIBLE);
                    mLoginNewPasswordLinearBottom.setVisibility(View.VISIBLE);
                    mLoginNewSmsLinearTop.setVisibility(View.GONE);
                    mLoginNewSmsLinearBottom.setVisibility(View.GONE);
                } else { // 验证码登录
                    mLoginNewTypeChange.setText("Account Login");
                    // 控件隐藏/显示
                    mLoginNewForgetPassword.setVisibility(View.GONE);
                    mLoginNewPasswordLinearTop.setVisibility(View.GONE);
                    mLoginNewPasswordLinearBottom.setVisibility(View.GONE);
                    mLoginNewSmsLinearTop.setVisibility(View.VISIBLE);
                    mLoginNewSmsLinearBottom.setVisibility(View.VISIBLE);
                }

                break;
            // 忘记密码
            case R.id.login_new_forget_password:
                startActivity(new Intent(mBaseActivity, ForgetPswNewActivity.class));
                break;
            // 登录
            case R.id.login_new_go_login:
                final String userAccount = mLoginNewAccount.getText().toString();
                final String psw = mLoginNewPassword.getText().toString();
                final String sms = mLoginNewSms.getText().toString();
                if (isLoginPsw) { // 密码登录
                    if (userAccount.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Username");
                    } else if (psw.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Password");
                    } else {
                        if (userAccount.length() != 11) {
                            ToastUtils.show(mBaseActivity, "Username/ Password Incorrect. Please Enter Again");
                        } else {
                            // 登录 APP 服务器
                            HashMap<String, String> params = new HashMap<>();
                            params.put("loginAccount", userAccount);
                            params.put("loginPassword", psw);
                            mLoginPswTask = new LoginPswTask();
                            mLoginPswTask.execute(params);
                        }
                    }
                } else { // 验证码登录
                    if (userAccount.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Username");
                    } else if (sms.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Verification Code");
                    } else {
                        if (userAccount.length() != 11) {
                            ToastUtils.show(mBaseActivity, "Username/ Verification Code Incorrect. Please Enter Again");
                        } else {
                            // 登录 APP 服务器
                            HashMap<String, String> params = new HashMap<>();
                            params.put("mobile", userAccount);
                            params.put("smsCode", sms);
                            mLoginSmsTask = new LoginSmsTask();
                            mLoginSmsTask.execute(params);
                            // 登录环信服务器
                        /*EMClient.getInstance().login(userAccount, hxCommonPsw, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                // 处理模型层数据
                                Model.getInstance().loginSuccess(new UserInfo(userAccount));
                                // 保存到本地数据库
                                Model.getInstance().getUserAccountDao().addAccount(new UserInfo(userAccount));

                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();

                                Log.e("TAG", "环信登陆成功");

                            }

                            @Override
                            public void onError(int code, String error) {
                                Log.e("TAG", "环信登陆失败");
                            }

                            @Override
                            public void onProgress(int progress, String status) { }
                        });*/
                        }
                    }
                }
                break;
        }
    }

    /**
     * 发送短信验证码内部类
     */
    private class SendCodeTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.sms_url, params[0],
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
                    Log.e("TAG", "SendCodeTask" + data.toString());
                    ToastUtils.show(mBaseActivity, data.getString("errormsg"));
                } catch (Exception e) {
                    //Toast.makeText(RegisterActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
     * 进行登录操作的内部类
     * 用于完成联网的异步任务
     */
    private class LoginPswTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mLoginPswTask != null) {
                                    mLoginPswTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("Loading");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("Loading");
            }
            mProgressDialog.show();
        }

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.login_psw_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) { }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object[] result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "登录接口的返回结果" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    // 保存用户的基本数据
                    User user = new User();
                    String userAccount = mLoginNewAccount.getText().toString();
                    final String ID =  jsonObject.getString("ID");
                    final String TOKEN =  jsonObject.getString("TOKEN");
                    user.userID = ID;
                    user.userAccount = userAccount;
                    user.isLogin = "true";
                    user.token = TOKEN;
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    dataStorage.storeOrUpdate(user, "User");
                    // 登录环信
                    Log.e("789", "环信登陆11" + hxCommonAccountHead + ID);
                    Log.e("789", "环信登陆22" + hxCommonPswHead + ID);
                    ChatClient.getInstance().login(hxCommonAccountHead + ID, hxCommonPswHead + ID,
                            new Callback() {
                        @Override
                        public void onSuccess() {
                            // 处理模型层数据
                            Model.getInstance().loginSuccess(new UserInfo(hxCommonAccountHead + ID));
                            // 保存到本地数据库
                            Model.getInstance().getUserAccountDao().addAccount(new UserInfo(hxCommonAccountHead + ID));

                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();

                            Log.e("789", "环信登陆成功");

                        }

                        @Override
                        public void onError(int code, String error) {
                            Log.e("789", "环信登陆失败" + String.valueOf(code));
                            Log.e("789", "环信登陆失败" + error);
                        }

                        @Override
                        public void onProgress(int progress, String status) { }
                    });
                    EventBus.getDefault().post(EventConfig.EVENT_LOGIN);
                    ToastUtils.show(mBaseActivity, "Login Successful");
//                    startActivity(new Intent(mBaseActivity, MainActivity.class));

                    Log.e("OkHttp", "111: " + from);
                    if (from.equals("home")) {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "0");
                        intent.putExtra("goodsType", "0");
                        startActivity(intent);
                        finish();
                    } else if (from.equals("goods")) {
                        Intent intent = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                        intent.putExtra("goodsId", goodsId);
                        intent.putExtra("goodsType", goodsType);
                        intent.putExtra("type", "home");
                        intent.putExtra("value", homeTime);
                        startActivity(intent);
                        finish();
                    } else if (from.equals("message")) {
                        Log.e("OkHttp", "222: " + from);
                        Log.e("OkHttp", "goodsType: " + goodsType);
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "1");
                        intent.putExtra("goodsType", goodsType);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "2");
                        intent.putExtra("goodsType", "0");
                        startActivity(intent);
                        finish();
                    }
                    Log.e("OkHttp", "333: " + from);
                } catch (Exception e) {
                    // Toast.makeText(CustomerLoginActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mBaseActivity, result[1].toString());
                startActivity(new Intent(mBaseActivity, RegisterNewActivity.class));
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
     * 进行登录操作的内部类
     * 用于完成联网的异步任务
     */
    private class LoginSmsTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mLoginSmsTask != null) {
                                    mLoginSmsTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("Loading");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("Loading");
            }
            mProgressDialog.show();
        }

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.login_sms_url, params[0],
                        HttpUtils.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) { }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object[] result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];
                    Log.e("TAG", "登录接口的返回结果" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    // 保存用户的基本数据
                    User user = new User();
                    String userAccount = mLoginNewAccount.getText().toString();
                    final String ID =  jsonObject.getString("ID");
                    final String TOKEN =  jsonObject.getString("TOKEN");
                    user.userID = ID;
                    user.userAccount = userAccount;
                    user.isLogin = "true";
                    user.token = TOKEN;
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            mBaseActivity.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    dataStorage.storeOrUpdate(user, "User");
                    // 登录环信
                    Log.e("789", "环信登陆11" + hxCommonAccountHead + ID);
                    Log.e("789", "环信登陆22" + hxCommonPswHead + ID);
                    ChatClient.getInstance().login(hxCommonAccountHead + ID, hxCommonPswHead + ID,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    // 处理模型层数据
                                    Model.getInstance().loginSuccess(new UserInfo(hxCommonAccountHead + ID));
                                    // 保存到本地数据库
                                    Model.getInstance().getUserAccountDao().addAccount(new UserInfo(hxCommonAccountHead + ID));

                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();

                                    Log.e("789", "环信登陆成功");

                                }

                                @Override
                                public void onError(int code, String error) {
                                    Log.e("789", "环信登陆失败" + String.valueOf(code));
                                    Log.e("789", "环信登陆失败" + error);
                                }

                                @Override
                                public void onProgress(int progress, String status) { }
                            });
                    EventBus.getDefault().post(EventConfig.EVENT_LOGIN);
                    ToastUtils.show(mBaseActivity, "Login Successful");
//                    startActivity(new Intent(mBaseActivity, MainActivity.class));

                    if (from.equals("home")) {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "0");
                        intent.putExtra("goodsType", "0");
                        startActivity(intent);
                        finish();
                    } else if (from.equals("goods")) {
                        Intent intent = new Intent(mBaseActivity, GoodsDetailsActivity.class);
                        intent.putExtra("goodsId", goodsId);
                        intent.putExtra("goodsType", goodsType);
                        intent.putExtra("type", "home");
                        intent.putExtra("value", homeTime);
                        startActivity(intent);
                        finish();
                    } else if (from.equals("message")) {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "1");
                        intent.putExtra("goodsType", goodsType);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mBaseActivity, MainActivity.class);
                        intent.putExtra("item", "2");
                        intent.putExtra("goodsType", "0");
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    // Toast.makeText(CustomerLoginActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mBaseActivity, result[1].toString());
                startActivity(new Intent(mBaseActivity, RegisterNewActivity.class));
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

}
