package com.enuos.jimat.activity.account.newInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.enuos.jimat.activity.common.WebActivity;
import com.enuos.jimat.activity.home.MainActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.model.bean.UserInfo;
import com.enuos.jimat.utils.MyUtils;
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

public class RegisterNewActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.register_new_back)
    ImageView mBack;
    @BindView(R.id.register_new_text_top_one)
    TextView mRegisterNewTextTopOne;
    @BindView(R.id.register_new_text_top_two)
    TextView mRegisterNewTextTopTwo;
    @BindView(R.id.register_new_account_linear_top)
    LinearLayout mRegisterNewAccountLinearTop;
    @BindView(R.id.register_new_account)
    EditText mRegisterNewAccount;
    @BindView(R.id.register_new_account_linear_bottom)
    LinearLayout mRegisterNewAccountLinearBottom;
    @BindView(R.id.register_new_sms_counter)
    TextView mRgisterNewSmsCounter;
    @BindView(R.id.register_new_sms_linear_top)
    LinearLayout mRegisterNewSmsLinearTop;
    @BindView(R.id.register_new_sms_linear_bottom)
    LinearLayout mRegisterNewSmsLinearBottom;
    @BindView(R.id.register_new_contact_user)
    TextView mRegisterNewContactUser;
    @BindView(R.id.register_new_contact)
    TextView mRegisterNewContact;
    @BindView(R.id.register_new_contact_linear)
    LinearLayout mRegisterNewContactLinear;
    @BindView(R.id.register_new_go_next)
    Button mRegisterNewGoNext;
    @BindView(R.id.register_new_password_linear_top)
    LinearLayout mRegisterNewPasswordLinearTop;
    @BindView(R.id.register_new_password)
    EditText mRegisterNewPassword;
    @BindView(R.id.register_new_password_is_hide)
    ImageView mRegisterNewPasswordIsHide;
    @BindView(R.id.register_new_password_linear_bottom)
    LinearLayout mRegisterNewPasswordLinearBottom;
    @BindView(R.id.register_new_text_top_three)
    TextView mRegisterNewTextTopThree;
    @BindView(R.id.register_new_sms_one)
    EditText mRegisterNewSmsOne;
    @BindView(R.id.register_new_sms_two)
    EditText mRegisterNewSmsTwo;
    @BindView(R.id.register_new_sms_three)
    EditText mRegisterNewSmsThree;
    @BindView(R.id.register_new_sms_four)
    EditText mRegisterNewSmsFour;
    @BindView(R.id.register_new_account_view_bottom_one)
    LinearLayout mRegisterNewAccountViewBottomOne;
    @BindView(R.id.register_new_password_linear_bottom_two)
    LinearLayout mRegisterNewPasswordLinearBottomTwo;
    @BindView(R.id.register_new_sms_btn_get)
    Button mRegisterNewSmsBtnGet;
    @BindView(R.id.register_new_go_save)
    Button mRegisterNewGoSave;
    @BindView(R.id.register_new_contact_linear_two)
    LinearLayout mRegisterNewContactLinearTwo;
    @BindView(R.id.register_new_password_linear_bottom_tips)
    LinearLayout mRegisterNewPasswordLinearBottomTips;

    private SendCodeTask mSendCodeTask;
    private RegisterTask mRegisterTask;
    private SweetAlertDialog mProgressDialog;
    private boolean isHideDisplay = true;
    private String myUserAccount, myPsw, myID, myToken, smsAll, smsServerCode;
    private int SMS_MAX_LENGTH = 1;  // 限制输入1个字
    private boolean isReInput = false;

    private LoginPswTask mLoginPswTask;
    private String hxCommonAccountHead = "jimataccounts";
    private String hxCommonPswHead = "jimatpassword";

    /**
     * 60秒倒计时
     */
    private CountDownTimer mTimer60 = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mRgisterNewSmsCounter.setText("Regain After " + String.valueOf(millisUntilFinished / 1000) + " s");
        }

        @Override
        public void onFinish() {
            mRgisterNewSmsCounter.setVisibility(View.GONE);
            mRegisterNewSmsBtnGet.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        ButterKnife.bind(this);

        mRegisterNewSmsOne.addTextChangedListener(this);
        mRegisterNewSmsTwo.addTextChangedListener(this);
        mRegisterNewSmsThree.addTextChangedListener(this);
        mRegisterNewSmsFour.addTextChangedListener(this);

        mRegisterNewTextTopTwo.setText("Please Enter Your Mobile Number & Join JIMAT");
        mRegisterNewContactUser.setText(" Terms of Use");

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == SMS_MAX_LENGTH) {
            if (mRegisterNewSmsOne.isFocused()) {
                mRegisterNewSmsOne.clearFocus();
                mRegisterNewSmsTwo.requestFocus();
                mRegisterNewSmsThree.clearFocus();
                mRegisterNewSmsFour.clearFocus();
            } else if (mRegisterNewSmsTwo.isFocused()) {
                mRegisterNewSmsOne.clearFocus();
                mRegisterNewSmsTwo.clearFocus();
                mRegisterNewSmsThree.requestFocus();
                mRegisterNewSmsFour.clearFocus();
            } else if (mRegisterNewSmsThree.isFocused()) {
                mRegisterNewSmsOne.clearFocus();
                mRegisterNewSmsTwo.clearFocus();
                mRegisterNewSmsThree.clearFocus();
                mRegisterNewSmsFour.requestFocus();
            } else if (mRegisterNewSmsFour.isFocused()) {
                mRegisterNewSmsOne.clearFocus();
                mRegisterNewSmsTwo.clearFocus();
                mRegisterNewSmsThree.clearFocus();
                mRegisterNewSmsFour.clearFocus();
            }
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.register_new_back, R.id.register_new_go_next, R.id.register_new_contact_user,
            R.id.register_new_contact, R.id.register_new_password_is_hide, R.id.register_new_sms_btn_get,
            R.id.register_new_go_save})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.register_new_back:
                Log.e("TAG", String.valueOf(isReInput));
                // 点击返回，重新输入手机号
                if (isReInput) {
                    // 隐藏/显示控件
                    mRegisterNewAccountLinearTop.setVisibility(View.VISIBLE);
                    mRegisterNewAccountLinearBottom.setVisibility(View.VISIBLE);
                    mRegisterNewContactLinear.setVisibility(View.VISIBLE);
                    mRegisterNewContactLinearTwo.setVisibility(View.VISIBLE);
                    mRegisterNewAccountViewBottomOne.setVisibility(View.VISIBLE);
                    mRegisterNewPasswordLinearBottomTwo.setVisibility(View.VISIBLE);
                    mRegisterNewSmsLinearTop.setVisibility(View.GONE);
                    mRegisterNewSmsLinearBottom.setVisibility(View.GONE);
                    mRegisterNewTextTopOne.setText("Sign Up");
                    mRegisterNewTextTopTwo.setText("Please Enter Your Mobile Number & Join JIMAT");
                    mRegisterNewTextTopThree.setVisibility(View.INVISIBLE);
                    isReInput = false;
                } else {
                    finish();
                }
                break;
            // 保存密码
            case R.id.register_new_go_save:
                isReInput = false;
                final String psw = mRegisterNewPassword.getText().toString();
                myPsw = psw;
                if (psw.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Password");
                } else if (psw.length() < 6 || psw.length() > 20) { // 数量
                    ToastUtils.show(mBaseActivity, "Please enter password length between 6-20 characters. Space and Special Characters are not allowed.");
                } else if (MyUtils.isSpecialChar(psw)) { // 特殊符号
                    ToastUtils.show(mBaseActivity, "Please enter password length between 6-20 characters. Space and Special Characters are not allowed.");
                } else {
                    // 开始注册
                    HashMap<String, String> params = new HashMap<>();
                    params.put("loginAccount", myUserAccount);
                    params.put("loginPassword", psw);
                    params.put("smsCode", smsAll);
                    mRegisterTask = new RegisterTask();
                    mRegisterTask.execute(params);
                }
                break;
            // 下一步
            case R.id.register_new_go_next:
                final String codePhone;
                Character number6 = mRegisterNewAccount.getText().toString().charAt(0);
                if (String.valueOf(number6).equals("6")) {
                    codePhone = mRegisterNewAccount.getText().toString();
                } else {
                    codePhone = 6 + mRegisterNewAccount.getText().toString();
                }
                myUserAccount = codePhone;
                if (mRegisterNewContactLinear.getVisibility() == View.VISIBLE) { // 输入手机号
                    isReInput = false;
                    if (codePhone.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Mobile Number");
                    } else if (codePhone.length() < 8 || codePhone.length() > 12) {
                        ToastUtils.show(mBaseActivity, "Please Enter The Correct Mobile Number");
                    } else {
                        isReInput = true;
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
                        // 隐藏/显示控件
                        mRegisterNewAccountLinearTop.setVisibility(View.GONE);
                        mRegisterNewAccountLinearBottom.setVisibility(View.GONE);
                        mRegisterNewContactLinear.setVisibility(View.GONE);
                        mRegisterNewContactLinearTwo.setVisibility(View.GONE);
                        mRegisterNewAccountViewBottomOne.setVisibility(View.GONE);
                        mRegisterNewPasswordLinearBottomTwo.setVisibility(View.GONE);
                        mRegisterNewSmsLinearTop.setVisibility(View.VISIBLE);
                        mRegisterNewSmsLinearBottom.setVisibility(View.VISIBLE);
                        mRegisterNewTextTopOne.setText("Enter The 4 Digits Verification Code");
                        String codeStr = codePhone.substring(0, 3) + " " +
                                codePhone.substring(3, 7) + " " + codePhone.substring(7, 11);
                        mRegisterNewTextTopTwo.setText("Verification Code has been sent to "
                                + codeStr);
                        mRegisterNewTextTopThree.setVisibility(View.VISIBLE);
                    }
                } else { // 输入验证码
                    isReInput = false;
                    final String smsOne = mRegisterNewSmsOne.getText().toString();
                    final String smsTwo = mRegisterNewSmsTwo.getText().toString();
                    final String smsThree = mRegisterNewSmsThree.getText().toString();
                    final String smsFour = mRegisterNewSmsFour.getText().toString();
                    smsAll = smsOne + smsTwo + smsThree + smsFour;
                    if (smsOne.equals("") || smsTwo.equals("") || smsThree.equals("") || smsFour.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Verification Code");
                    } else if (!smsServerCode.equals(smsAll)) {
                        ToastUtils.show(mBaseActivity, "Verification Code Incorrect. Please Try Again.");
                    } else {
                        // 隐藏下一步 显示保存密码
                        mRegisterNewGoNext.setVisibility(View.GONE);
                        mRegisterNewGoSave.setVisibility(View.VISIBLE);

                        mRegisterNewTextTopOne.setText("Password Setting");
                        mRegisterNewTextTopTwo.setText("Set password for your account");
                        mRegisterNewTextTopThree.setVisibility(View.INVISIBLE);
                        mRegisterNewAccountViewBottomOne.setVisibility(View.VISIBLE);
                        mRegisterNewPasswordLinearBottomTwo.setVisibility(View.VISIBLE);
                        mRegisterNewSmsLinearTop.setVisibility(View.GONE);
                        mRegisterNewSmsLinearBottom.setVisibility(View.GONE);
                        mRegisterNewPasswordLinearTop.setVisibility(View.VISIBLE);
                        mRegisterNewPasswordLinearBottom.setVisibility(View.VISIBLE);
                        mRegisterNewPasswordLinearBottomTips.setVisibility(View.VISIBLE);
                    }
                }
                break;
            // 用户协议
            case R.id.register_new_contact_user:
                Intent intentUser = new Intent(mBaseActivity, WebActivity.class);
                intentUser.putExtra("title", "Terms of Use");
                intentUser.putExtra("url", "http://47.254.192.108:8080/jimatInterface/fwxy.html");
                startActivity(intentUser);
                break;
            // 隐私权政策
            case R.id.register_new_contact:
                Intent intent = new Intent(mBaseActivity, WebActivity.class);
                intent.putExtra("title", "Privacy Policy");
                intent.putExtra("url", "http://47.254.192.108:8080/jimatInterface/yszc.html");
                startActivity(intent);
                break;
            // 隐藏/显示密码
            case R.id.register_new_password_is_hide:
                if (isHideDisplay) {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img_enable).into(mRegisterNewPasswordIsHide);
                    mRegisterNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img).into(mRegisterNewPasswordIsHide);
                    mRegisterNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 发送验证码
            case R.id.register_new_sms_btn_get:
                mRgisterNewSmsCounter.setVisibility(View.VISIBLE);
                mRegisterNewSmsBtnGet.setVisibility(View.GONE);
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
                params.put("mobile", myUserAccount);
                params.put("token", userToken);
                mSendCodeTask = new SendCodeTask();
                mSendCodeTask.execute(params);
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
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    smsServerCode = jsonObject.getString("code");
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
     * 注册内部类
     */
    private class RegisterTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mRegisterTask != null) {
                                    mRegisterTask.cancel(true);
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
                        UrlConfig.base_url + UrlConfig.register_url, params[0],
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
                    Log.e("TAG", "RegisterTask" + data.toString());
                    ToastUtils.show(mBaseActivity, "Sign Up Successful");
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    myID = jsonObject.getString("ID");
                    // 登录 APP 服务器
                    HashMap<String, String> params = new HashMap<>();
                    params.put("loginAccount", myUserAccount);
                    params.put("loginPassword", myPsw);
                    mLoginPswTask = new LoginPswTask();
                    mLoginPswTask.execute(params);
                } catch (Exception e) {
                    //Toast.makeText(RegisterActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mBaseActivity, result[1].toString());
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
                    Log.e("TAG", "登录接口的返回结果" + data.toString());
                    String infoString = data.getString("data").replaceAll("\'", "\"");
                    JSONObject jsonObject = new JSONObject(infoString);
                    myToken = jsonObject.getString("TOKEN");
                    // 保存用户的基本数据
                    User user = new User();
                    user.userID = myID;
                    user.userAccount = myUserAccount;
                    user.isLogin = "true";
                    user.token = myToken;
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    dataStorage.storeOrUpdate(user, "User");
                    // 登录环信
                    Log.e("789", "环信登陆11" + hxCommonAccountHead + myID);
                    Log.e("789", "环信登陆22" + hxCommonPswHead + myID);
                    ChatClient.getInstance().login(hxCommonAccountHead + myID, hxCommonPswHead + myID,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    // 处理模型层数据
                                    Model.getInstance().loginSuccess(new UserInfo(hxCommonAccountHead + myID));
                                    // 保存到本地数据库
                                    Model.getInstance().getUserAccountDao().addAccount(new UserInfo(hxCommonAccountHead + myID));

                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();

                                    Log.e("789", "环信登陆成功");

                                }

                                @Override
                                public void onError(int code, String error) {
                                    Log.e("789", "环信登陆失败" + String.valueOf(code));
                                    Log.e("789", "环信登陆失败" + error);
                                    Log.e("789", "环信登陆失败");
                                }

                                @Override
                                public void onProgress(int progress, String status) {
                                }
                            });
                    EventBus.getDefault().post(EventConfig.EVENT_LOGIN);
                    Intent intent = new Intent(mBaseActivity, MainActivity.class);
                    intent.putExtra("item", "0");
                    intent.putExtra("goodsType", "0");
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    // Toast.makeText(CustomerLoginActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mBaseActivity, result[1].toString());
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
