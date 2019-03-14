package com.enuos.jimat.activity.account.newInfo;

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
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class ForgetPswNewActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.forget_new_back)
    ImageView mBack;
    @BindView(R.id.forget_new_text_top_one)
    TextView mForgetNewTextTopOne;
    @BindView(R.id.forget_new_text_top_two)
    TextView mForgetNewTextTopTwo;
    @BindView(R.id.forget_new_account_linear_top)
    LinearLayout mForgetNewAccountLinearTop;
    @BindView(R.id.forget_new_account)
    EditText mForgetNewAccount;
    @BindView(R.id.forget_new_account_linear_bottom)
    LinearLayout mForgetNewAccountLinearBottom;
    @BindView(R.id.forget_new_sms_counter)
    TextView mForgetNewSmsCounter;
    @BindView(R.id.forget_new_sms_linear_top)
    LinearLayout mForgetNewSmsLinearTop;
    @BindView(R.id.forget_new_sms_linear_bottom)
    LinearLayout mForgetNewSmsLinearBottom;
    @BindView(R.id.forget_new_password_linear_top)
    LinearLayout mForgetNewPasswordLinearTop;
    @BindView(R.id.forget_new_password)
    EditText mForgetNewPassword;
    @BindView(R.id.forget_new_password_is_hide)
    ImageView mForgetNewPasswordIsHide;
    @BindView(R.id.forget_new_password_linear_bottom)
    LinearLayout mForgetNewPasswordLinearBottom;
    @BindView(R.id.forget_new_go_next)
    Button mForgetNewGoNext;
    @BindView(R.id.forget_new_text_top_three)
    TextView mForgetNewTextTopThree;
    @BindView(R.id.forget_new_sms_btn_get)
    Button mForgetNewSmsBtnGet;
    @BindView(R.id.forget_new_sms_one)
    EditText mForgetNewSmsOne;
    @BindView(R.id.forget_new_sms_two)
    EditText mForgetNewSmsTwo;
    @BindView(R.id.forget_new_sms_three)
    EditText mForgetNewSmsThree;
    @BindView(R.id.forget_new_sms_four)
    EditText mForgetNewSmsFour;
    @BindView(R.id.forget_new_account_view_bottom_one)
    LinearLayout mForgetNewAccountViewBottomOne;
    @BindView(R.id.forget_new_password_linear_bottom_two)
    LinearLayout mForgetNewPasswordLinearBottomTwo;
    @BindView(R.id.forget_new_go_save)
    Button mForgetNewGoSave;
    @BindView(R.id.forget_new_password_linear_bottom_tips)
    LinearLayout mForgetNewPasswordLinearBottomTips;

    private SendCodeTask mSendCodeTask;
    private ForgetCodeTask mForgetCodeTask;
    private SweetAlertDialog mProgressDialog;
    private boolean isHideDisplay = true;
    private String myUserAccount, smsAll, smsServerCode;
    private int SMS_MAX_LENGTH = 1;  // 限制输入1个字

    /**
     * 60秒倒计时
     */
    private CountDownTimer mTimer60 = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mForgetNewSmsCounter.setText("Regain After " + String.valueOf(millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            mForgetNewSmsCounter.setVisibility(View.GONE);
            mForgetNewSmsBtnGet.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_new);
        ButterKnife.bind(this);

        mForgetNewSmsOne.addTextChangedListener(this);
        mForgetNewSmsTwo.addTextChangedListener(this);
        mForgetNewSmsThree.addTextChangedListener(this);
        mForgetNewSmsFour.addTextChangedListener(this);
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
            if (mForgetNewSmsOne.isFocused()) {
                mForgetNewSmsOne.clearFocus();
                mForgetNewSmsTwo.requestFocus();
                mForgetNewSmsThree.clearFocus();
                mForgetNewSmsFour.clearFocus();
            } else if (mForgetNewSmsTwo.isFocused()) {
                mForgetNewSmsOne.clearFocus();
                mForgetNewSmsTwo.clearFocus();
                mForgetNewSmsThree.requestFocus();
                mForgetNewSmsFour.clearFocus();
            } else if (mForgetNewSmsThree.isFocused()) {
                mForgetNewSmsOne.clearFocus();
                mForgetNewSmsTwo.clearFocus();
                mForgetNewSmsThree.clearFocus();
                mForgetNewSmsFour.requestFocus();
            } else if (mForgetNewSmsFour.isFocused()) {
                mForgetNewSmsOne.clearFocus();
                mForgetNewSmsTwo.clearFocus();
                mForgetNewSmsThree.clearFocus();
                mForgetNewSmsFour.clearFocus();
            }
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.forget_new_back, R.id.forget_new_go_next, R.id.forget_new_password_is_hide,
            R.id.forget_new_sms_btn_get, R.id.forget_new_go_save})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.forget_new_back:
                finish();
                break;
            // 保存密码
            case R.id.forget_new_go_save:
                final String psw = mForgetNewPassword.getText().toString();
                mForgetNewGoNext.setText("A p p l y");
                if (psw.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Password");
                } else if (psw.length() < 6 || psw.length() > 20) { // 数量
                    ToastUtils.show(mBaseActivity, "Please enter password length between 6-20 characters. Space and Special Characters are not allowed.");
                } else if (MyUtils.isSpecialChar(psw)) { // 特殊符号
                    ToastUtils.show(mBaseActivity, "Please enter password length between 6-20 characters. Space and Special Characters are not allowed.");
                } else {
                    // 开始修改密码

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
                    params.put("smsCode", smsAll);
                    params.put("loginPassword", psw);
                    params.put("token", userToken);
                    mForgetCodeTask = new ForgetCodeTask();
                    mForgetCodeTask.execute(params);
                }
                break;
            // 下一步
            case R.id.forget_new_go_next:
                final String codePhone = "6"+mForgetNewAccount.getText().toString();
                myUserAccount = codePhone;
                if (mForgetNewTextTopOne.getText().toString().equals("Forgot Password")) { // 输入手机号
                    if (codePhone.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Mobile Number");
                    } else if (codePhone.length() < 8 && codePhone.length() > 12) {
                        ToastUtils.show(mBaseActivity, "Please Enter The Correct Mobile Number");
                    } else {
                        // 发送短信验证码
                        mTimer60.start();
                        myUserAccount = 6 + myUserAccount;
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
                        mForgetNewAccountLinearTop.setVisibility(View.GONE);
                        mForgetNewAccountLinearBottom.setVisibility(View.GONE);
                        mForgetNewAccountViewBottomOne.setVisibility(View.GONE);
                        mForgetNewPasswordLinearBottomTwo.setVisibility(View.GONE);
                        mForgetNewSmsLinearTop.setVisibility(View.VISIBLE);
                        mForgetNewSmsLinearBottom.setVisibility(View.VISIBLE);
                        mForgetNewTextTopOne.setText("Enter The 4 Digits Verification Code");
                        String codeStr = codePhone.substring(0, 3) + " " +
                                codePhone.substring(3, 7) + " " + codePhone.substring(7, 11);
                        mForgetNewTextTopTwo.setText("Verification Code has been sent to" + codeStr);
                        mForgetNewTextTopThree.setVisibility(View.VISIBLE);
                    }
                } else { // 输入验证码
                    final String smsOne = mForgetNewSmsOne.getText().toString();
                    final String smsTwo = mForgetNewSmsTwo.getText().toString();
                    final String smsThree = mForgetNewSmsThree.getText().toString();
                    final String smsFour = mForgetNewSmsFour.getText().toString();
                    smsAll = smsOne + smsTwo + smsThree + smsFour;
                    if (smsOne.equals("") || smsTwo.equals("") || smsThree.equals("") || smsFour.equals("")) {
                        ToastUtils.show(mBaseActivity, "Please Enter Verification Code");
                    } else if (!smsServerCode.equals(smsAll)) {
                        ToastUtils.show(mBaseActivity, "Verification Code Incorrect. Please Try Again.");
                    } else {
                        // 隐藏下一步 显示保存密码
                        mForgetNewGoNext.setVisibility(View.GONE);
                        mForgetNewGoSave.setVisibility(View.VISIBLE);

                        mForgetNewTextTopOne.setText("Password Setting");
                        mForgetNewTextTopTwo.setText("Set new password for your account");
                        mForgetNewTextTopThree.setVisibility(View.INVISIBLE);
                        mForgetNewAccountViewBottomOne.setVisibility(View.VISIBLE);
                        mForgetNewPasswordLinearBottomTwo.setVisibility(View.VISIBLE);
                        mForgetNewSmsLinearTop.setVisibility(View.GONE);
                        mForgetNewSmsLinearBottom.setVisibility(View.GONE);
                        mForgetNewPasswordLinearTop.setVisibility(View.VISIBLE);
                        mForgetNewPasswordLinearBottom.setVisibility(View.VISIBLE);
                        mForgetNewPasswordLinearBottomTips.setVisibility(View.VISIBLE);
                    }
                }
                break;
            // 隐藏/显示密码
            case R.id.forget_new_password_is_hide:
                if (isHideDisplay) {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img_enable).into(mForgetNewPasswordIsHide);
                    mForgetNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    Glide.with(mBaseActivity).load(R.drawable.psw_img).into(mForgetNewPasswordIsHide);
                    mForgetNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 发送验证码
            case R.id.forget_new_sms_btn_get:
                mForgetNewSmsCounter.setVisibility(View.VISIBLE);
                mForgetNewSmsBtnGet.setVisibility(View.GONE);
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
     * 忘记密码内部类 手机短信验证码
     */
    private class ForgetCodeTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mForgetCodeTask != null) {
                                    mForgetCodeTask.cancel(true);
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
                        UrlConfig.base_url + UrlConfig.modify_psw_phone_url, params[0],
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
                    Log.e("TAG", "ForgetTask" + data.toString());
                    finish();
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
}
