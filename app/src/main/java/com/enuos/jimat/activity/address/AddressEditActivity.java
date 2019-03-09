package com.enuos.jimat.activity.address;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.MyUtils;
import com.enuos.jimat.utils.address.ChooseCityInterface;
import com.enuos.jimat.utils.address.ChooseNewCityUtil;
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

public class AddressEditActivity extends BaseActivity {

    @BindView(R.id.address_edit_back)
    ImageView mBack;
    @BindView(R.id.address_edit_et_name)
    EditText mAddressEditEtName;
    @BindView(R.id.address_edit_et_phone)
    EditText mAddressEditEtPhone;
    @BindView(R.id.address_edit_text_area)
    TextView mAddressEditTextArea;
    @BindView(R.id.address_edit_et_area_details)
    EditText mAddressEditEtAreaDetails;
    @BindView(R.id.address_edit_btn_save)
    TextView mAddressEditBtnSave;
    @BindView(R.id.address_edit_ll_area)
    LinearLayout mAddressEditLlArea;
    @BindView(R.id.address_edit_switch)
    SwitchCompat mSwitch;
    @BindView(R.id.address_linear_delete)
    LinearLayout mAddressLinearDelete;
    @BindView(R.id.address_edit_back_rl)
    RelativeLayout mAddressEditBackRl;

    private User mUser;
    private String cityOne, cityTwo, cityThree, isDefault;
    private String addressId;
    private boolean isDefaultBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        ButterKnife.bind(this);

        addressId = getIntent().getStringExtra("addressId");
        mAddressEditEtName.setText(getIntent().getStringExtra("addressName"));
        mAddressEditEtPhone.setText(getIntent().getStringExtra("addressTel"));
        mAddressEditTextArea.setText(getIntent().getStringExtra("addressArea"));
        mAddressEditEtAreaDetails.setText(getIntent().getStringExtra("addressAreaDetails"));
        isDefault = getIntent().getStringExtra("isDefault");
        cityOne = getIntent().getStringExtra("province");
//        cityTwo = getIntent().getStringExtra("city");
//        cityThree = getIntent().getStringExtra("area");

        if (isDefault.equals("1")) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }

        if (mAddressEditEtName.length() != 0) {
            mAddressEditEtName.setSelection(mAddressEditEtName.length());
        }

        if (mAddressEditEtPhone.length() != 0) {
            mAddressEditEtPhone.setSelection(mAddressEditEtPhone.length());
        }

        if (mAddressEditEtAreaDetails.length() != 0) {
            mAddressEditEtAreaDetails.setSelection(mAddressEditEtAreaDetails.length());
        }
        // 获取 User 信息
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUser = dataStorage.load(User.class, "User");

        // 监听mSwitch
        listenSwitch();
    }

    /**
     * 监听mSwitch
     */
    private void listenSwitch() {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                isDefaultBoolean = mSwitch.isChecked();
            }
        });
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.address_edit_back_rl, R.id.address_edit_ll_area,
            R.id.address_linear_delete, R.id.address_edit_btn_save})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.address_edit_back_rl:
                finish();
                break;
            // 选择省市区
            case R.id.address_edit_ll_area:
                final ChooseNewCityUtil cityUtil = new ChooseNewCityUtil();
                String[] oldCityArray = {"Johor"};
                cityUtil.createDialog(this, oldCityArray, new ChooseCityInterface() {
                    @Override
                    public void sure(String[] newCityArray) {
                        cityOne = newCityArray[0];
                        mAddressEditTextArea.setText(cityOne);
                    }
                });

                /*final ChooseCityUtil cityUtil = new ChooseCityUtil();
                String[] oldCityArray = {"江苏", "南京", "栖霞"};
                cityUtil.createDialog(this, oldCityArray, new ChooseCityInterface() {
                    @Override
                    public void sure(String[] newCityArray) {
                        cityOne = newCityArray[0];
                        cityTwo = newCityArray[1];
                        cityThree = newCityArray[2];
                        mAddressEditTextArea.setText(cityOne + cityTwo + cityThree);
                    }
                });*/
                break;
            // 删除
            case R.id.address_linear_delete:
                final SweetAlertDialog deleteDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("Yes")
                        .setCancelText("No");
                deleteDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteDialog.dismiss();
                        DeleteAddressTask task = new DeleteAddressTask();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("addressId", addressId);
                        params.put("memberId", mUser.userID);
                        params.put("token", mUser.token);
                        task.execute(params);
                    }
                });
                deleteDialog.setTitleText("Confirm to Delete");
                deleteDialog.setContentText("the Shipping Address?");
                deleteDialog.show();
                break;
            // 保存
            case R.id.address_edit_btn_save:
                final String name = mAddressEditEtName.getText().toString();
                final String phone = mAddressEditEtPhone.getText().toString();
                final String area = mAddressEditTextArea.getText().toString();
                final String areaDetails = mAddressEditEtAreaDetails.getText().toString();
                final String isDefaultTag;
                if (isDefaultBoolean) {
                    isDefaultTag = "1";
                } else {
                    isDefaultTag = "2";
                }
                if (name.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Receiver Name");
                    break;
                } else if (phone.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Receiver Contact");
                    break;
                }
//                else if (!MyUtils.isMobileNO(phone)) {
//                    ToastUtils.show(mBaseActivity, "Please enter the correct Mobile Number");
//                }
                else if (area.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter State");
                    break;
                } else if (areaDetails.equals("")) {
                    ToastUtils.show(mBaseActivity, "Please Enter Address Detail");
                    break;
                } else {
                    String takeProvinceId = getPosId(cityOne);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("addressId", addressId);
                    params.put("memberId", mUser.userID);
                    params.put("token", mUser.token);
                    params.put("takeName", name);
                    params.put("takeMobile", phone);
                    params.put("takeProvince", cityOne);
                    params.put("takeProvinceId", takeProvinceId);
                    params.put("takeCity", "");
                    params.put("takeArea", "");
                    params.put("takeAddress", areaDetails);
                    params.put("isDefault", isDefaultTag);
                    EditAddresstTask task = new EditAddresstTask();
                    task.execute(params);
                }
                break;
        }
    }

    /**
     * 修改收货地址的内部类
     */
    private class EditAddresstTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.address_update_url, params[0],
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
                    Log.e("TAG", data.toString());
                    ToastUtils.show(mBaseActivity, data.getString("errormsg"));
                    EventBus.getDefault().post(EventConfig.EVENT_ADDRESS);
                    finish();
                } catch (Exception e) {
                    // Toast.makeText(AddContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
                    ToastUtils.show(mBaseActivity, "Please Login");
                    Intent intentG = new Intent(mBaseActivity, LoginNewActivity.class);
                    intentG.putExtra("from", "mine");
                    intentG.putExtra("goodsId", "");
                    intentG.putExtra("goodsType", "");
                    intentG.putExtra("homeTime", "0");
                    startActivity(intentG);
                    finish();
                }
                // 同一账户多个终端登录
            }

        }
    }

    /**
     * 内部类
     * 删除某个收货地址
     */
    private class DeleteAddressTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.address_delete_url, params[0],
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
                    Log.e("TAG", "删除收货地址的结果：" + data.toString());
                    EventBus.getDefault().post(EventConfig.EVENT_ADDRESS);
                    finish();
                } catch (Exception e) {
                    // Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
     * 计算ID
     */
    private String getPosId(String area) {
        String strId;
        switch (area) {
            case "Johor":
                strId = "110000";
                break;
            case "Kedah":
                strId = "120000";
                break;
            case "Kelantan":
                strId = "130000";
                break;
            case "Kuala Lumpur":
                strId = "140000";
                break;
            case "Labuan":
                strId = "150000";
                break;
            case "Melaka":
                strId = "210000";
                break;
            case "Negeri Sembilan":
                strId = "220000";
                break;
            case "Pahang":
                strId = "230000";
                break;
            case "Perak":
                strId = "310000";
                break;
            case "Perlis":
                strId = "320000";
                break;
            case "Pulau Pinang":
                strId = "330000";
                break;
            case "Putrajaya":
                strId = "340000";
                break;
            case "Sabah":
                strId = "350000";
                break;
            case "Sarawak":
                strId = "360000";
                break;
            case "Selangor":
                strId = "370000";
                break;
            case "Terengganu":
                strId = "410000";
                break;
            default:
                strId = "110000";
                break;
        }
        return strId;
    }
}
