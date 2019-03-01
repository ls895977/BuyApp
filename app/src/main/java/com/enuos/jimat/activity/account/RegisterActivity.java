package com.enuos.jimat.activity.account;

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
import android.widget.TextView;
import android.widget.Toast;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.activity.common.WebActivity;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_back)
    ImageView mBack;
    @BindView(R.id.register_text_account)
    EditText mRegisterTextAccount;
    @BindView(R.id.register_text_code)
    EditText mRegisterTextCode;
    @BindView(R.id.register_get_code)
    TextView mRegisterGetCode;
    @BindView(R.id.register_text_psw)
    EditText mRegisterTextPsw;
    @BindView(R.id.register_img_psw_is_hide)
    ImageView mRegisterImgPswIsHide;
    @BindView(R.id.register_bt_register)
    Button mRegisterBtRegister;
    @BindView(R.id.register_contact)
    TextView mRegisterContact;
    @BindView(R.id.register_contact_user)
    TextView mRegisterContactUser;

    private RegisterTask mRegisterTask;
    private SendCodeTask mSendCodeTask;
    private SweetAlertDialog mProgressDialog;
    private boolean isHideDisplay = true;
    private String myUserAccount, myPswCode;

    /**
     * 60秒倒计时
     */
    private CountDownTimer mTimer60 = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mRegisterGetCode.setEnabled(false);
            mRegisterGetCode.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mRegisterGetCode.setEnabled(true);
            mRegisterGetCode.setText("获取验证码");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.register_back, R.id.register_get_code, R.id.register_img_psw_is_hide,
            R.id.register_bt_register, R.id.register_contact_user, R.id.register_contact})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.register_back:
                finish();
                break;
            // 获取验证码
            case R.id.register_get_code:
                final String codePhone = mRegisterTextAccount.getText().toString();
                if (codePhone.equals("")) {
                    ToastUtils.show(mBaseActivity, "请输入手机号码");
                } else if (!MyUtils.isMobileNO(codePhone)) {
                    ToastUtils.show(mBaseActivity, "手机号码错误");
                } else {
                    // 发送短信验证码
                    mTimer60.start();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile", codePhone);
                    mSendCodeTask = new SendCodeTask();
                    mSendCodeTask.execute(params);
                }
                break;
            // 隐藏/显示密码
            case R.id.register_img_psw_is_hide:
                if (isHideDisplay) {
                    mRegisterTextPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    mRegisterTextPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 去注册
            case R.id.register_bt_register:
                final String userAccount = mRegisterTextAccount.getText().toString();
                final String msgCode = mRegisterTextCode.getText().toString();
                final String pswCode = mRegisterTextPsw.getText().toString();
                myUserAccount = userAccount;
                myPswCode = pswCode;
                if (userAccount.equals("")) {
                    ToastUtils.show(mBaseActivity, "请输入手机号码");
                } else if (msgCode.equals("")) {
                    ToastUtils.show(mBaseActivity, "请输入验证码");
                } else if (pswCode.equals("")) {
                    ToastUtils.show(mBaseActivity, "请输入密码");
                } else {
                    if (!MyUtils.isMobileNO(userAccount)) {
                        ToastUtils.show(mBaseActivity, "手机号码错误");
                    } else if (pswCode.length() < 6 || pswCode.length() > 20) {
                        ToastUtils.show(mBaseActivity, "密码必须是6-20位");
                    } else {
                        // 开始注册
                        HashMap<String, String> params = new HashMap<>();
                        params.put("loginAccount", userAccount);
                        params.put("loginPassword", pswCode);
                        params.put("smsCode", msgCode);
                        mRegisterTask = new RegisterTask();
                        mRegisterTask.execute(params);

                    }
                }
                break;
            // 用户协议
            case R.id.register_contact_user:
                Intent intentUser = new Intent(mBaseActivity, WebActivity.class);
                intentUser.putExtra("title", "用户协议");
                intentUser.putExtra("url", "http://47.254.192.108:8080/jimatInterface/fwxy.html");
                startActivity(intentUser);
                break;
            // 隐私权政策
            case R.id.register_contact:
                Intent intent = new Intent(mBaseActivity, WebActivity.class);
                intent.putExtra("title", "隐私权政策");
                intent.putExtra("url", "http://47.254.192.108:8080/jimatInterface/yszc.html");
                startActivity(intent);
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
                    Toast.makeText(mBaseActivity, data.getString("errormsg"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(RegisterActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
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
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mRegisterTask != null) {
                                    mRegisterTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("注册中");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("注册中");
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
                    // 注册环信服务器
                    ChatClient.getInstance().register(myUserAccount, myPswCode, new Callback() {
                        @Override
                        public void onSuccess() {
                            // 注册环信服务器

                        }

                        @Override
                        public void onError(int code, String error) {
                            Log.e("TAG", "环信登陆失败");
                        }

                        @Override
                        public void onProgress(int progress, String status) { }
                    });
                    RegisterActivity.this.finish();
                    Toast.makeText(mBaseActivity, data.getString("errormsg"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(RegisterActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mBaseActivity, result[1].toString(), Toast.LENGTH_SHORT).show();
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
