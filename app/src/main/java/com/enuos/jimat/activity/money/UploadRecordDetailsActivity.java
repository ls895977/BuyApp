package com.enuos.jimat.activity.money;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class UploadRecordDetailsActivity extends BaseActivity {

    @BindView(R.id.upload_record_details_back)
    ImageView mBack;
    @BindView(R.id.upload_record_details_title)
    TextView mUploadRecordDetailsTitle;
    @BindView(R.id.upload_record_details_account)
    TextView mUploadRecordDetailsAccount;
    @BindView(R.id.upload_record_details_img_one)
    ImageView mUploadRecordDetailsImgOne;
    @BindView(R.id.upload_record_details_img_two)
    ImageView mUploadRecordDetailsImgTwo;
    @BindView(R.id.upload_record_details_img_three)
    ImageView mUploadRecordDetailsImgThree;
    @BindView(R.id.upload_record_details_text_plus)
    TextView mUploadRecordDetailsTextPlus;
    @BindView(R.id.upload_record_details_img_flag)
    ImageView mUploadRecordDetailsImgFlag;
    @BindView(R.id.upload_record_details_audit_doing_time)
    TextView mUploadRecordDetailsAuditDoingTime;
    @BindView(R.id.upload_record_details_audit_doing_linear)
    LinearLayout mUploadRecordDetailsAuditDoingLinear;
    @BindView(R.id.upload_record_details_audit_success_time)
    TextView mUploadRecordDetailsAuditSuccessTime;
    @BindView(R.id.upload_record_details_audit_success_doing_time)
    TextView mUploadRecordDetailsAuditSuccessDoingTime;
    @BindView(R.id.upload_record_details_audit_success_linear)
    LinearLayout mUploadRecordDetailsAuditSuccessLinear;
    @BindView(R.id.upload_record_details_audit_fail_time)
    TextView mUploadRecordDetailsAuditFailTime;
    @BindView(R.id.upload_record_details_audit_fail_doing_time)
    TextView mUploadRecordDetailsAuditFailDoingTime;
    @BindView(R.id.upload_record_details_audit_fail_linear)
    LinearLayout mUploadRecordDetailsAuditFailLinear;
    @BindView(R.id.upload_record_details_audit_success_text)
    TextView mUploadRecordDetailsAuditSuccessText;
    @BindView(R.id.upload_record_details_audit_fail_text)
    TextView mUploadRecordDetailsAuditFailText;
    @BindView(R.id.upload_record_details_back_rl)
    RelativeLayout mUploadRecordDetailsBackRl;

    private String ID, status;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_record_details);
        ButterKnife.bind(this);

        ID = getIntent().getStringExtra("ID");
        status = getIntent().getStringExtra("status");

        if (status.equals("1")) {
            mUploadRecordDetailsTitle.setText("Processing");
        } else if (status.equals("2")) {
            mUploadRecordDetailsTitle.setText("Verification Failed");
        } else {
            mUploadRecordDetailsTitle.setText("Verification Successful");
        }

        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        mUploadRecordDetailsAccount.setText(mUser.userAccount);
        // 进入界面之后先获取信息
        refresh();
    }

    /**
     * 用户刷新后执行
     * 用于获取网络数据
     */
    private void refresh() {
        // 取出token      params.put("token", userToken);
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        String userToken = "";
        if (user != null && !user.userAccount.equals("")) {
            userToken = user.token;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("remittanceId", ID);
        params.put("token", userToken);
        DoPostTask mDoPostTask = new DoPostTask();
        mDoPostTask.execute(params);
    }

    /**
     * 加载界面
     */
    private void loadPage(JSONObject jsonObject) {
        try {
            String stringData = jsonObject.getString("data");
            JSONObject jsonObjectData = new JSONObject(stringData);
            // 留言
            mUploadRecordDetailsTextPlus.setText(jsonObjectData.getString("DETAIL"));
            // 审核进度
            if (status.equals("1")) { // 审核中
                mUploadRecordDetailsAuditDoingLinear.setVisibility(View.VISIBLE);
                mUploadRecordDetailsAuditSuccessLinear.setVisibility(View.GONE);
                mUploadRecordDetailsAuditFailLinear.setVisibility(View.GONE);

                mUploadRecordDetailsAuditDoingTime.setText(jsonObjectData.getString("CREATE_TIME"));
            } else if (status.equals("2")) { // 审核失败
                mUploadRecordDetailsAuditDoingLinear.setVisibility(View.GONE);
                mUploadRecordDetailsAuditSuccessLinear.setVisibility(View.GONE);
                mUploadRecordDetailsAuditFailLinear.setVisibility(View.VISIBLE);

                mUploadRecordDetailsAuditFailDoingTime.setText(jsonObjectData.getString("CREATE_TIME"));
                mUploadRecordDetailsAuditFailTime.setText(jsonObjectData.getString("AUDIT_TIME"));
                mUploadRecordDetailsAuditFailText.setText(jsonObjectData.getString("FINDING_AUDIT"));
            } else { // 审核成功
                mUploadRecordDetailsAuditDoingLinear.setVisibility(View.GONE);
                mUploadRecordDetailsAuditSuccessLinear.setVisibility(View.VISIBLE);
                mUploadRecordDetailsAuditFailLinear.setVisibility(View.GONE);

                mUploadRecordDetailsAuditSuccessDoingTime.setText(jsonObjectData.getString("CREATE_TIME"));
                mUploadRecordDetailsAuditSuccessTime.setText(jsonObjectData.getString("AUDIT_TIME"));
                mUploadRecordDetailsAuditSuccessText.setText(jsonObjectData.getString("FINDING_AUDIT"));
            }
            // 图片
            String jsonArrayString = jsonObjectData.getString("remittanceImgMapList");
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            int imageNumber = jsonArrayString.length();
            if (imageNumber == 1) { // 一张
                String imgUrlOne = jsonArray.getJSONObject(0).getString("IMG_URL");
                if (imgUrlOne != null) {
                    Glide.with(mBaseActivity).load(imgUrlOne.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgOne);
                }
            } else if (imageNumber == 2) { // 二张
                String imgUrlOne = jsonArray.getJSONObject(0).getString("IMG_URL");
                if (imgUrlOne != null) {
                    Glide.with(mBaseActivity).load(imgUrlOne.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgOne);
                }

                String imgUrlTwo = jsonArray.getJSONObject(1).getString("IMG_URL");
                if (imgUrlTwo != null) {
                    Glide.with(mBaseActivity).load(imgUrlTwo.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgTwo);
                }
            } else { // 三张
                String imgUrlOne = jsonArray.getJSONObject(0).getString("IMG_URL");
                if (imgUrlOne != null) {
                    Glide.with(mBaseActivity).load(imgUrlOne.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgOne);
                }

                String imgUrlTwo = jsonArray.getJSONObject(1).getString("IMG_URL");
                if (imgUrlTwo != null) {
                    Glide.with(mBaseActivity).load(imgUrlTwo.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgTwo);
                }

                String imgUrlThree = jsonArray.getJSONObject(2).getString("IMG_URL");
                if (imgUrlThree != null) {
                    Glide.with(mBaseActivity).load(imgUrlThree.replaceAll("\'", "\""))
                            .into(mUploadRecordDetailsImgThree);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部类
     * 凭证详情列表
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.upload_pay_details_url, params[0],
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
                    Log.e("TAG", "凭证详情列表的返回结果：" + data.toString());
                    loadPage(data);
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
     * 点击事件
     */
    @OnClick({R.id.upload_record_details_back_rl})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.upload_record_details_back_rl:
                finish();
                break;
        }
    }

}
