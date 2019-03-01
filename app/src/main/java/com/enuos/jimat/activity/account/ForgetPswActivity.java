package com.enuos.jimat.activity.account;

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
import android.widget.Toast;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPswActivity extends BaseActivity {

    @BindView(R.id.forget_back)
    ImageView mBack;
    @BindView(R.id.forget_type)
    TextView mForgetType;
    @BindView(R.id.forget_text_account)
    EditText mForgetTextAccount;
    @BindView(R.id.forget_ll_account)
    LinearLayout mForgetLlAccount;
    @BindView(R.id.forget_text_email)
    EditText mForgetTextEmail;
    @BindView(R.id.forget_ll_email)
    LinearLayout mForgetLlEmail;
    @BindView(R.id.forget_get_code)
    TextView mForgetGetCode;
    @BindView(R.id.forget_text_psw)
    EditText mForgetTextPsw;
    @BindView(R.id.forget_img_psw_is_hide)
    ImageView mForgetImgPswIsHide;
    @BindView(R.id.forget_text_psw_ensure)
    EditText mForgetTextPswEnsure;
    @BindView(R.id.forget_img_psw_is_hide_ensure)
    ImageView mForgetImgPswIsHideEnsure;
    @BindView(R.id.forget_bt_commit)
    Button mForgetBtCommit;
    @BindView(R.id.forget_view_account)
    View mForgetViewAccount;
    @BindView(R.id.forget_view_email)
    View mForgetViewEmail;
    @BindView(R.id.forget_text_code)
    EditText mForgetTextCode;

    private SendCodeTask mSendCodeTask;
    private SendEmailTask mSendEmailTask;
    private ForgetCodeTask mForgetCodeTask;
    private ForgetEmailTask mForgetEmailTask;
    private SweetAlertDialog mProgressDialog;

    private boolean isPhoneLogin = true;
    private boolean isHideDisplay = true;
    private boolean isHideEnsureDisplay = true;

    /**
     * 60秒倒计时
     */
    private CountDownTimer mTimer60 = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mForgetGetCode.setEnabled(false);
            mForgetGetCode.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mForgetGetCode.setEnabled(true);
            mForgetGetCode.setText("获取验证码");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw);
        ButterKnife.bind(this);
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.forget_back, R.id.forget_type, R.id.forget_get_code,
            R.id.forget_img_psw_is_hide, R.id.forget_img_psw_is_hide_ensure, R.id.forget_bt_commit})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.forget_back:
                finish();
                break;
            // 手机找回/邮箱找回
            case R.id.forget_type:
                if (isPhoneLogin) { // 邮箱找回
                    mForgetType.setText("手机找回");
                    mForgetLlAccount.setVisibility(View.GONE);
                    mForgetViewAccount.setVisibility(View.GONE);
                    mForgetLlEmail.setVisibility(View.VISIBLE);
                    mForgetViewEmail.setVisibility(View.VISIBLE);
                } else { // 手机找回
                    mForgetType.setText("邮箱找回");
                    mForgetLlAccount.setVisibility(View.VISIBLE);
                    mForgetViewAccount.setVisibility(View.VISIBLE);
                    mForgetLlEmail.setVisibility(View.GONE);
                    mForgetViewEmail.setVisibility(View.GONE);
                }
                isPhoneLogin = !isPhoneLogin;
                break;
            // 获取验证码
            case R.id.forget_get_code:
                final String codeAccount = mForgetTextAccount.getText().toString();
                if (isPhoneLogin) { // 手机找回
                    if (codeAccount.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入手机号");
                    } else if (!MyUtils.isMobileNO(codeAccount)) {
                        ToastUtils.show(mBaseActivity, "手机号码错误");
                    } else {
                        // 发送短信验证码
                        mTimer60.start();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("mobile", codeAccount);
                        mSendCodeTask = new SendCodeTask();
                        mSendCodeTask.execute(params);
                    }
                } else {  // 邮箱找回
                    // TODO: 2018/12/16 邮箱验证
//                    if (!MyUtils.isEmailAdress(codeAccount)) {
//                        ToastUtils.show(mBaseActivity, "邮箱账号错误");
//                    } else {
                        // 发送邮箱验证码
                        mTimer60.start();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", codeAccount);
                        mSendEmailTask = new SendEmailTask();
                        mSendEmailTask.execute(params);
//                    }
                }
                break;
            // 隐藏/显示密码
            case R.id.forget_img_psw_is_hide:
                if (isHideDisplay) {
                    mForgetTextPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    mForgetTextPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 隐藏/显示确认密码
            case R.id.forget_img_psw_is_hide_ensure:
                if (isHideEnsureDisplay) {
                    mForgetTextPswEnsure.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    mForgetTextPswEnsure.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideEnsureDisplay = !isHideEnsureDisplay;
                break;
            // 提交
            case R.id.forget_bt_commit:
                final String userAccount = mForgetTextAccount.getText().toString();
                final String userEmail = mForgetTextEmail.getText().toString();
                final String msgCode = mForgetTextCode.getText().toString();
                final String pswCode = mForgetTextPsw.getText().toString();
                final String pswEnsureCode = mForgetTextPswEnsure.getText().toString();
                if (isPhoneLogin) { // 手机找回
                    if (userAccount.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入手机号码");
                    } else if (msgCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入验证码");
                    } else if (pswCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入六位密码");
                    } else if (pswEnsureCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请再次输入密码");
                    } else {
                        if (!MyUtils.isMobileNO(userAccount)) {
                            ToastUtils.show(mBaseActivity, "手机号码错误");
                        } else if (pswCode.length() != 6 || pswEnsureCode.length() != 6) {
                            ToastUtils.show(mBaseActivity, "密码必须是六位");
                        } else if (!pswCode.equals(pswEnsureCode)) {
                            ToastUtils.show(mBaseActivity, "两次输入的密码不一致");
                        } else {
                            // 开始修改密码
                            HashMap<String, String> params = new HashMap<>();
                            params.put("mobile", userAccount);
                            params.put("smsCode", msgCode);
                            params.put("loginPassword", pswCode);
                            mForgetCodeTask = new ForgetCodeTask();
                            mForgetCodeTask.execute(params);
                        }
                    }
                } else { // 邮箱找回
                    if (userEmail.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入邮箱账号");
                    } else if (msgCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入验证码");
                    } else if (pswCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请输入六位密码");
                    } else if (pswEnsureCode.equals("")) {
                        ToastUtils.show(mBaseActivity, "请再次输入密码");
                    } else {
                        // TODO: 2018/12/5 邮箱验证
//                        if (!MyUtils.isEmailAdress(userAccount)) {
//                            ToastUtils.show(mBaseActivity, "邮箱账号错误");
//                        } else
                        if (pswCode.length() != 6 || pswEnsureCode.length() != 6) {
                            ToastUtils.show(mBaseActivity, "密码必须是六位");
                        } else if (!pswCode.equals(pswEnsureCode)) {
                            ToastUtils.show(mBaseActivity, "两次输入的密码不一致");
                        } else {
                            // 开始修改密码
                            HashMap<String, String> params = new HashMap<>();
                            params.put("email", userAccount);
                            params.put("smsCode", msgCode);
                            params.put("loginPassword", pswCode);
                            mForgetEmailTask = new ForgetEmailTask();
                            mForgetEmailTask.execute(params);
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
     * 发送邮箱验证码内部类
     */
    private class SendEmailTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.email_url, params[0],
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
                    Log.e("TAG", "SendEmailTask" + data.toString());
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
     * 忘记密码内部类 手机短信验证码
     */
    private class ForgetCodeTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mForgetCodeTask != null) {
                                    mForgetCodeTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("修改中");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("修改中");
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
     * 忘记密码内部类 邮箱验证码
     */
    private class ForgetEmailTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mForgetEmailTask != null) {
                                    mForgetEmailTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("修改中");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("修改中");
            }
            mProgressDialog.show();
        }

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.modify_psw_email_url, params[0],
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
                    Log.e("TAG", "ForgetEmailTask" + data.toString());
                    finish();
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
