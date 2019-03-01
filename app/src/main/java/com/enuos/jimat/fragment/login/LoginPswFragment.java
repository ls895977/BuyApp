package com.enuos.jimat.fragment.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.ForgetPswActivity;
import com.enuos.jimat.activity.account.RegisterActivity;
import com.enuos.jimat.activity.home.MainActivity;
import com.enuos.jimat.fragment.BaseFragment;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.model.bean.UserInfo;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.EMClient;
import com.hyphenate.helpdesk.callback.Callback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/5 10:24
 * @文件描述：
 * @修改历史： 2018/12/5 创建初始版本
 **********************************************************/
public class LoginPswFragment extends BaseFragment {

    @BindView(R.id.login_text_account_psw)
    EditText mLoginTextAccountPsw;
    @BindView(R.id.login_text_code_psw)
    EditText mLoginTextCodePsw;
    @BindView(R.id.login_img_code_is_hide)
    ImageView mLoginImgCodeIsHide;
    @BindView(R.id.login_bt_login_psw)
    Button mLoginBtLoginPsw;
    @BindView(R.id.login_text_go_register_psw)
    TextView mLoginTextGoRegisterPsw;
    @BindView(R.id.login_text_forget_psw)
    TextView mLoginTextForgetPsw;
    Unbinder unbinder;

    private LoginTask mLoginTask;
    private SweetAlertDialog mProgressDialog;
    public String hxCommonPsw = "1233";
    private boolean isHideDisplay = true;

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.fragment_login_psw, null);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.login_img_code_is_hide, R.id.login_bt_login_psw,
            R.id.login_text_go_register_psw, R.id.login_text_forget_psw})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 隐藏/显示密码
            case R.id.login_img_code_is_hide:
                if (isHideDisplay) {
                    mLoginTextCodePsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    mLoginTextCodePsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isHideDisplay = !isHideDisplay;
                break;
            // 去登录
            case R.id.login_bt_login_psw:
                final String userAccount = mLoginTextAccountPsw.getText().toString();
                final String pswCode = mLoginTextCodePsw.getText().toString();
                if (userAccount.equals("")) {
                    ToastUtils.show(mContext, "请输入手机号");
                } else if (pswCode.equals("")) {
                    ToastUtils.show(mContext, "请输入密码");
                } else {
                    if (!MyUtils.isMobileNO(userAccount)) {
                        ToastUtils.show(mContext, "手机号错误");
                    } else {
                        // 登录 APP 服务器
                        HashMap<String, String> params = new HashMap<>();
                        params.put("loginAccount", userAccount);
                        params.put("loginPassword", pswCode);
                        mLoginTask = new LoginTask();
                        mLoginTask.execute(params);

                        ChatClient.getInstance().login(userAccount, pswCode, new Callback() {
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
                        });
                    }
                }
                break;
            // 快速注册
            case R.id.login_text_go_register_psw:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            // 忘记密码
            case R.id.login_text_forget_psw:
                startActivity(new Intent(mContext, ForgetPswActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 进行登录操作的内部类
     * 用于完成联网的异步任务
     */
    private class LoginTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mLoginTask != null) {
                                    mLoginTask.cancel(true);
                                }
                            }
                        });
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("登录中");
            } else {
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("登录中");
            }
            mProgressDialog.show();
        }

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mContext,
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
                    String userAccount = mLoginTextAccountPsw.getText().toString();
                    user.userID = jsonObject.getString("ID");
                    user.userAccount = userAccount;
                    user.isLogin = "true";
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            mContext.getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    dataStorage.storeOrUpdate(user, "User");
                    EventBus.getDefault().post(EventConfig.EVENT_LOGIN);
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                } catch (Exception e) {
                    // Toast.makeText(CustomerLoginActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
//                Toast.makeText(mContext, result[1].toString(), Toast.LENGTH_SHORT).show();
                ToastUtils.show(mContext, "您还没有注册或密码错误");
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
