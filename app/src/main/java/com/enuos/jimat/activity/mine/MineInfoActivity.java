package com.enuos.jimat.activity.mine;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.PrefUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.enuos.jimat.view.PhotoPopupWindow;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

public class MineInfoActivity extends BaseActivity {

    @BindView(R.id.mine_info_back)
    ImageView mBack;
    @BindView(R.id.mine_info_headImage)
    RoundedImageView mMineInfoHeadImage;
    @BindView(R.id.mine_info_linear_headImage)
    LinearLayout mMineInfoLinearHeadImage;
    @BindView(R.id.mine_info_text_account)
    TextView mMineInfoTextAccount;
    @BindView(R.id.mine_info_text_name)
    TextView mMineInfoTextName;
    @BindView(R.id.mine_info_linear_text_name)
    LinearLayout mMineInfoLinearTextName;
    @BindView(R.id.mine_info_back_rl)
    RelativeLayout mMineInfoBackRl;

    private String mUserAccount, mUserId, token, headImage, name;
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";

    private Uri mImageUri, mImageUriA;

    private PhotoPopupWindow mPhotoPopupWindow;
    private SweetAlertDialog mProgressDialog;
    private IDataStorage dataStorage;

    private String paramsStr[] = new String[]{"null"};
    private int maxImage = 1; // 最多添加1张图片
    private boolean imgFinishA = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_info);
        ButterKnife.bind(this);

        // 注册 EventBus
        EventBus.getDefault().register(this);
        // 初始化 Dialog
        mProgressDialog = new SweetAlertDialog(mBaseActivity, SweetAlertDialog.PROGRESS_TYPE);
        // 初始化 IDataStorage
        dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        mUserAccount = dataStorage.load(User.class, "User").userAccount;
        mUserId = dataStorage.load(User.class, "User").userID;
        token = dataStorage.load(User.class, "User").token;
        // 进入界面后先获取个人信息
        doRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_SETTINGS)) {
            doRefresh();
        }
    }

    /**
     * 处理回调结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 小图切割
                case REQUEST_SMALL_IMAGE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                // 大图切割
                case REQUEST_BIG_IMAGE_CUTTING:
                    imgFinishA = true;
                    mImageUriA = mImageUri;
                    mMineInfoHeadImage.setImageURI(mImageUri);
                    // 上传
                    UploadPicTask task = new UploadPicTask();
                    // text
                    LinkedHashMap<String, String> strParams = new LinkedHashMap<>();
                    strParams.put("memberId", mUserId);
                    strParams.put("token", token);
                    // image
                    LinkedHashMap<String, File> fileParams = new LinkedHashMap<>();
                    File imageFile[] = new File[maxImage];
                    Uri image[] = new Uri[]{mImageUriA};
                    Boolean imageFinish[] = new Boolean[]{imgFinishA};
                    String postStr[] = new String[]{"imgOne"};
                    int postCount = 0;
                    for (int i = 0; i < maxImage; i++) {
                        if (imageFinish[i]) {
                            imageFile[i] = new File(image[i].getEncodedPath());
                            fileParams.put(postStr[postCount], imageFile[i]);
                            paramsStr[postCount] = postStr[postCount];
                            postCount++;
                        }
                    }
                    task.execute(new LinkedHashMap[]{strParams, fileParams});
                    break;
                // 相册选取
                case REQUEST_IMAGE_GET:
                    try {
                        // startSmallPhotoZoom(data.getData());
                        startBigPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    // startSmallPhotoZoom(Uri.fromFile(temp));
                    startBigPhotoZoom(temp);
                    break;
            }
        }
    }

    /**
     * 处理权限回调结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } /*else {
                        Toast.makeText(mBaseActivity, "未找到图片查看器", Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    imageCapture();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 400:
                break;
        }
    }

    /**
     * 获得信息
     */
    private void doRefresh() {
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
    }

    /**
     * 加载页面所有的视图元素
     */
    private void loadPage(JSONObject getJsonObject) {
        try {
            name = getJsonObject.getString("TRUE_NAME");
            headImage = getJsonObject.getString("MEMBER_AVATAR");
            PrefUtils.setString(getApplication(), "UserPic",
                    (getJsonObject.getString("MEMBER_AVATAR") == null) ? "" : getJsonObject.getString("MEMBER_AVATAR"));
            mMineInfoTextAccount.setText(mUserAccount);
            mMineInfoTextName.setText(name);
            if (headImage.equals("null") || headImage.equals("")) {
                Glide.with(mBaseActivity).load(R.drawable.default_icon).into(mMineInfoHeadImage);
            } else {
                Glide.with(mBaseActivity).load(headImage).into(mMineInfoHeadImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.mine_info_back_rl, R.id.mine_info_linear_headImage, R.id.mine_info_linear_text_name})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.mine_info_back_rl:
                finish();
                break;
            // 修改头像
            case R.id.mine_info_linear_headImage:
                mPhotoPopupWindow = new PhotoPopupWindow(mBaseActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(mBaseActivity,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(mBaseActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(mBaseActivity,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                        } else {
                            // 权限已经申请，直接拍照
                            mPhotoPopupWindow.dismiss();
                            imageCapture();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(mBaseActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            //权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(mBaseActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                        } else {
                            // 如果权限已经申请过，直接进行图片选择
                            mPhotoPopupWindow.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            // 判断系统中是否有处理该 Intent 的 Activity
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_IMAGE_GET);
                            } /*else {
                                Toast.makeText(mBaseActivity, "未找到图片查看器", Toast.LENGTH_SHORT).show();
                            }*/
                        }
                    }
                });
                View rootView = LayoutInflater.from(mBaseActivity)
                        .inflate(R.layout.activity_mine_info, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            // 修改昵称
            case R.id.mine_info_linear_text_name:
                Intent intentInfo = new Intent(mBaseActivity, EditNameActivity.class);
                intentInfo.putExtra("name", name);
                startActivity(intentInfo);
                break;
        }
    }

    /**
     * 判断系统及拍照
     */
    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(this,
                    "com.enuos.jimat.fileProvider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(File inputFile) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(mBaseActivity, inputFile), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public void startBigPhotoZoom(Uri uri) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 小图模式中，保存图片后，设置到视图中
     * 将图片保存设置到视图中
     */
    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            // 创建 icon 文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String storage = Environment.getExternalStorageDirectory().getPath();
                File dirFile = new File(storage + "/smallIcon");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.e("TAG", "文件夹创建失败");
                    } else {
                        Log.e("TAG", "文件夹创建成功");
                    }
                }
                File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
                // 保存图片
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 在视图中显示图片
            mMineInfoHeadImage.setImageBitmap(photo);
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

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * 内部类
     * 上传图片
     */
    private class UploadPicTask extends AsyncTask<LinkedHashMap<String, Object>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(LinkedHashMap<String, Object>... params) {
            LinkedHashMap<String, String> strParams = new LinkedHashMap<>();
            LinkedHashMap<String, File> fileParams = new LinkedHashMap<>();
            // text
            strParams.put("memberId", params[0].get("memberId").toString());
            strParams.put("token", params[0].get("token").toString());
            // files
            File imageFile[] = new File[maxImage];
            for (int i = 0; i < maxImage; i++) {
                if (!(paramsStr[i].equals("null"))) {
                    imageFile[i] = new File(params[1].get(paramsStr[i]).toString());
                    fileParams.put(paramsStr[i], imageFile[i]);
                }
            }

            return HttpUtils.postHttpJPGLinked(mBaseActivity, UrlConfig.base_url + UrlConfig.upload_png_url,
                    strParams, fileParams);
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
                    Log.e("TAG", "上传图片的结果：" + data.toString());
                    String imgFile = data.getString("imgFile");
                    Log.e("TAG", "上传图片的结果imgFile：" + imgFile);
                    // 上传图片地址
                    HashMap<String, String> params = new HashMap<>();
                    params.put("memberId", mUserId);
                    params.put("token", token);
                    params.put("memberAvatar", imgFile);
                    DoPostTask mDoPostTask = new DoPostTask();
                    mDoPostTask.execute(params);
                } catch (Exception e) {
                    // Toast.makeText(ChooseAddressActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
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
     * 上传图片地址和文字
     */
    private class DoPostTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtils.postHttp(mBaseActivity,
                        UrlConfig.base_url + UrlConfig.edit_headimage_url, params[0],
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
                    Log.e("TAG", "修改头像图片的返回结果：" + data.toString());
                    ToastUtils.show(mBaseActivity, data.getString("errormsg"));
                } catch (Exception e) {
                    //Toast.makeText(ChooseContactActivity.this, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                ToastUtils.show(mBaseActivity, result[1].toString());
            }

        }
    }
}
