package com.enuos.jimat.activity.money;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.activity.account.newInfo.LoginNewActivity;
import com.enuos.jimat.activity.common.BaseActivity;
import com.enuos.jimat.model.Model;
import com.enuos.jimat.model.User;
import com.enuos.jimat.utils.ClickUtils;
import com.enuos.jimat.utils.event.EventConfig;
import com.enuos.jimat.utils.http.HttpUtils;
import com.enuos.jimat.utils.http.UrlConfig;
import com.enuos.jimat.utils.toast.ToastUtils;
import com.enuos.jimat.view.PhotoPopupWindow;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
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

public class MoneyUploadActivity extends BaseActivity {

    @BindView(R.id.money_upload_back)
    ImageView mBack;
    @BindView(R.id.money_upload_account)
    TextView mMoneyUploadAccount;
    @BindView(R.id.money_upload_img_one)
    ImageView mMoneyUploadImgOne;
    @BindView(R.id.money_upload_img_two)
    ImageView mMoneyUploadImgTwo;
    @BindView(R.id.money_upload_img_three)
    ImageView mMoneyUploadImgThree;
    @BindView(R.id.money_upload_btn_commit)
    Button mMoneyUploadBtnCommit;
    @BindView(R.id.money_upload_text_note)
    EditText mMoneyUploadTextNote;
    @BindView(R.id.money_upload_back_rl)
    RelativeLayout mMoneyUploadBackRl;

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";

    private Uri mImageUri;
    private PhotoPopupWindow mPhotoPopupWindow;
    private int imgCounts = -1;
    private Uri mImageUriA, mImageUriB, mImageUriC;
    private boolean imgFinishA = false;
    private boolean imgFinishB = false;
    private boolean imgFinishC = false;
    private String paramsStr[] = new String[]{"null", "null", "null"};
    private int maxImage = 3; // 最多添加3张图片

    private IDataStorage dataStorage;
    private User mUser;

    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_upload);
        ButterKnife.bind(this);
        dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        String userAccount = dataStorage.load(User.class, "User").userAccount;
        mUser = dataStorage.load(User.class, "User");
        mMoneyUploadAccount.setText(userAccount);

        mMoneyUploadImgTwo.setVisibility(View.VISIBLE);
        mMoneyUploadImgThree.setVisibility(View.GONE);

        // 加载背景图片
//        Glide.with(mBaseActivity).load(R.drawable.upload_add).into(mMoneyUploadImgOne);
        Glide.with(mBaseActivity).load(R.drawable.upload_add).into(mMoneyUploadImgTwo);
        Glide.with(mBaseActivity).load(R.drawable.upload_add).into(mMoneyUploadImgThree);

    }

    /**
     * 点击事件
     */
    @OnClick({R.id.money_upload_back_rl, R.id.money_upload_img_one,
            R.id.money_upload_img_two, R.id.money_upload_img_three, R.id.money_upload_btn_commit})
    public void onViewClick(View view) {
        switch (view.getId()) {
            // 返回
            case R.id.money_upload_back_rl:
                finish();
                break;
            // 第1张图片
            case R.id.money_upload_img_one:
                if (imgFinishA) { // 第一张图片已经存在
                    chooseCaptureOrPic();
                }
                break;
            // 第2张图片
            case R.id.money_upload_img_two:
                if (!imgFinishA) { // 第一张图片没有存在
                    imgCounts = 0;
                } else {
                    imgCounts = 1;

                }
                chooseCaptureOrPic();
                break;
            // 第3张图片
            case R.id.money_upload_img_three:
                if (imgFinishA && imgFinishB) { // 第一 二张图片已经存在
                    imgCounts = 2;
                    chooseCaptureOrPic();
                }
                break;
            // 上传
            case R.id.money_upload_btn_commit:
                if (!ClickUtils.INSTANCE.isFastDoubleClick()) {
                    if (!checkPic()) {
                        break;
                    } else {
                        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Loading");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        final String detail = mMoneyUploadTextNote.getText().toString();
                        String userID = dataStorage.load(User.class, "User").userID;
                        String token = dataStorage.load(User.class, "User").token;
                        UploadPayTask task = new UploadPayTask();
                        // text
                        LinkedHashMap<String, String> strParams = new LinkedHashMap<>();
                        strParams.put("memberId", userID);
                        strParams.put("token", token);
                        strParams.put("details", detail);
                        // image
                        LinkedHashMap<String, File> fileParams = new LinkedHashMap<>();
                        File imageFile[] = new File[maxImage];
                        Uri image[] = new Uri[]{mImageUriA, mImageUriB, mImageUriC};
                        Boolean imageFinish[] = new Boolean[]{imgFinishA, imgFinishB, imgFinishC};
                        String postStr[] = new String[] {"imgOne", "imgTwo", "imgThree"};
                        int postCount = 0;
                        for (int i = 0; i < maxImage; i++) {
                            if (imageFinish[i]) {
                                imageFile[i] = new File(image[i].getEncodedPath());
                                fileParams.put(postStr[postCount], imageFile[i]);
                                paramsStr[postCount] = postStr[postCount];
                                postCount++;
                            }
                        }
                        task.execute(new LinkedHashMap[] {strParams, fileParams});
                    }
                }
                break;
        }
    }

    /**
     * 检查【图片】是否正确录入
     */
    private boolean checkPic() {
        if (!imgFinishA && !imgFinishB && !imgFinishC) {
            ToastUtils.show(mBaseActivity, "Please Upload Bank Transfer Receipt");
        } else {
            return true;
        }
        return false;
    }

    /**
     * 选择拍照 或 从图库添加
     */
    private void chooseCaptureOrPic() {
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
                .inflate(R.layout.activity_money_upload, null);
        mPhotoPopupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                    switch (imgCounts) {
                        case 0:
                            imgFinishA = true;
                            mImageUriA = mImageUri;
                            mMoneyUploadImgOne.setImageURI(mImageUri);

                            // 存在第一张 红色方框2显示 红色方框3隐藏
                            mMoneyUploadImgTwo.setVisibility(View.VISIBLE);
                            mMoneyUploadImgThree.setVisibility(View.GONE);
                            break;
                        case 1:
                            imgFinishB = true;
                            mImageUriB = mImageUri;
                            mMoneyUploadImgTwo.setImageURI(mImageUri);

                            // 存在第一张 第二张 红色方框3显示
                            mMoneyUploadImgThree.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            imgFinishC = true;
                            mImageUriC = mImageUri;
                            mMoneyUploadImgThree.setImageURI(mImageUri);

                            // 存在第一张 第二张 第三张
                            mMoneyUploadImgThree.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
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
        }
    }

    /**
     * 内部类
     * 上传支付凭证
     */
    private class UploadPayTask extends AsyncTask<LinkedHashMap<String, Object>, Integer, Object[]> {

        // doInBackground方法内部执行后台任务, 不可在此方法内修改 UI
        @Override
        protected Object[] doInBackground(LinkedHashMap<String, Object>... params) {
            LinkedHashMap<String, String> strParams = new LinkedHashMap<>();
            LinkedHashMap<String, File> fileParams = new LinkedHashMap<>();
            // text
            strParams.put("memberId", params[0].get("memberId").toString());
            strParams.put("token", params[0].get("token").toString());
            strParams.put("details", params[0].get("details").toString());
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
                    Log.e("TAG", "上传支付凭证的结果：" + data.toString());
                    String imgFile = data.getString("imgFile");
                    Log.e("TAG", "上传支付凭证的结果imgFile：" + imgFile);
                    final String detail = mMoneyUploadTextNote.getText().toString();
                    // 上传图片地址和文字
                    HashMap<String, String> params = new HashMap<>();
                    params.put("memberId", mUser.userID);
                    params.put("token", mUser.token);
                    params.put("imgUrl", imgFile);
                    params.put("detail", detail);
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
                        UrlConfig.base_url + UrlConfig.upload_pay_add_url, params[0],
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
                    Log.e("TAG", "上传图片地址和文字的返回结果：" + data.toString());
                    ToastUtils.show(mBaseActivity, data.getString("errormsg"));
                    String stringData = data.getString("data");
                    JSONObject jsonObjectData = new JSONObject(stringData);
                    // 详情页
                    pDialog.dismiss();
                    Intent intent = new Intent(mBaseActivity, UploadRecordDetailsActivity.class);
                    intent.putExtra("ID", jsonObjectData.getString("ID"));
                    intent.putExtra("status", "1");
                    startActivity(intent);
                    MoneyUploadActivity.this.finish();
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
                ToastUtils.show(mBaseActivity, "Please Login");
                Intent intentG = new Intent(mBaseActivity, LoginNewActivity.class);
                intentG.putExtra("from", "mine");
                intentG.putExtra("goodsId", "");
                intentG.putExtra("goodsType", "");
                intentG.putExtra("homeTime", "0");
                startActivity(intentG);
                finish();
            }

        }
    }

}
