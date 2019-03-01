package com.enuos.jimat.activity.mine;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
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

public class EditNameActivity extends BaseActivity {

    @BindView(R.id.edit_name_back)
    ImageView mBack;
    @BindView(R.id.edit_name_et)
    EditText mEditNameEt;
    @BindView(R.id.edit_name_clear)
    ImageView mEditNameClear;
    @BindView(R.id.edit_name_btn_save)
    Button mEditNameBtnSave;
    @BindView(R.id.edit_name_back_rl)
    RelativeLayout mEditNameBackRl;

    private ChangeNicknameTask mChangeNicknameTask;
    private SweetAlertDialog mProgressDialog;
    private String userId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        ButterKnife.bind(this);

        mEditNameEt.setText(getIntent().getStringExtra("name"));
        if (mEditNameEt.length() != 0) {
            mEditNameEt.setSelection(mEditNameEt.length());
        }

        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        userId = dataStorage.load(User.class, "User").userID;
        token = dataStorage.load(User.class, "User").token;

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.edit_name_back_rl, R.id.edit_name_clear, R.id.edit_name_btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.edit_name_back_rl:
                finish();
                break;
            //  清除 EditText 中的内容
            case R.id.edit_name_clear:
                mEditNameEt.setText("");
                break;
            // 保存
            case R.id.edit_name_btn_save:
                final String newNick = mEditNameEt.getText().toString();
                if (newNick.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please enter a nickname");
                } else if (newNick.length() > 20) {
                    ToastUtils.show(mBaseActivity, "Nickname cannot be more than 20 alphanumeric");
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("memberId", userId);
                    params.put("token", token);
                    params.put("trueName", newNick);
                    mChangeNicknameTask = new ChangeNicknameTask();
                    mChangeNicknameTask.execute(params);
                }
                break;
        }
    }

    /**
     * 修改用户昵称的异步网络任务
     */
    private class ChangeNicknameTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (mChangeNicknameTask != null) {
                                    mChangeNicknameTask.cancel(true);
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
                        UrlConfig.base_url + UrlConfig.edit_name_url, params[0],
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
                    Log.e("TAG", "修改昵称的结果" + data.toString());
                    ToastUtils.show(mBaseActivity, data.getString("errormsg"));
                    EventBus.getDefault().post(EventConfig.EVENT_SETTINGS);
                    mBaseActivity.finish();
                } catch (Exception e) {
                    // Toast.makeText(ChangeNicknameActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
