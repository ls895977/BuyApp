package com.enuos.jimat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 个人工具类
 */

public class MyUtils extends Activity {
    public static String TAG = "com.sherlock.log";
    private static int SD_TOTAL_MEMORY = 0;//获取SD卡全部内存
    private static int SD_REMAIN_MEMORY = 1;//获取SD卡剩余内存
    public static Activity mActivity;

    public MyUtils(Activity activity) {
        mActivity = activity;
    }

    /**
     * 发送短信
     * <uses-permission android:name="android.permission.SEND_SMS"/>
     *
     * @param address 地址
     * @param content 内容
     */
    public static void sendSms(String address, String content) {
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> smsList = manager.divideMessage(content);
        for (String sms : smsList) {
            manager.sendTextMessage(address, null, sms, null, null);
        }
    }

    /**
     * 拨打电话
     * <uses-permission android:name="android.permission.CALL_PHONE"/>
     *
     * @param number 号码
     * @return 用于调用拨号界面的intent
     */
    public static Intent callNumber(String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        return intent;
    }

    /**
     * 判断SD卡片是否挂载
     *
     * @return boolean
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡容量
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
     *
     * @param activity 上下文
     * @param choice   SD_TOTAL_MEMORY=0：全部容量
     *                 SD_REMAIN_MEMORY=1：剩余容量
     * @return 容量
     */
    public static String getSDcardMemory(Activity activity, int choice) {
        File sdCard = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdCard.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            availableBlocks = statFs.getAvailableBlocksLong();
            totalBlocks = statFs.getBlockCountLong();
        } else {
            blockSize = statFs.getBlockSize();
            totalBlocks = statFs.getBlockCount();
            availableBlocks = statFs.getAvailableBlocks();
        }
        if (choice == SD_TOTAL_MEMORY) {
            return Formatter.formatFileSize(activity, totalBlocks * blockSize);

        } else if (choice == SD_REMAIN_MEMORY) {
            return Formatter.formatFileSize(activity, availableBlocks * blockSize);
        }
        return "获取SD卡容量失败";
    }

    /**
     * 将String写入文件中
     *
     * @param string string内容
     * @param file   目标文件
     * @throws IOException
     */
    public static void String2File(String string, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(string.getBytes());
    }

    /**
     * 从url连接中获取文件名称
     *
     * @param url 网络地址
     * @return 文件名称
     */
    public static String getFileNameFromURL(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is     输入流
     * @param decode 编码方式
     * @return 字符串
     */
    public static String Stream2String(InputStream is, String decode) {
        byte[] b = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            String text = new String(bos.toByteArray(), decode);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否是手机号码
     *
     * @param mobiles 手机号码
     * @return true/false
     */
    public static boolean isMobileNO(String mobiles) {
        if (mobiles.isEmpty()) {
            return false;
        }
        Pattern MB = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");
        /**
         * 中国移动：China Mobile
         * 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         */
        Pattern CM = Pattern.compile("^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\\\d{8}$");
        /**
         * 中国联通：China Unicom
         * 130,131,132,145,155,156,170,171,175,176,185,186
         */
        Pattern CU = Pattern.compile("^1(3[0-2]|4[5]|5[56]|7[0156]|8[56])\\\\d{8}$");
        /**
         * 中国电信：China Telecom
         * 133,149,153,170,173,177,180,181,189
         */
        Pattern CT = Pattern.compile("^1(3[3]|4[9]|53|7[037]|8[019])\\\\d{8}$");

        Pattern MY = Pattern.compile("^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$");
        return MB.matcher(mobiles).matches() || CM.matcher(mobiles).matches() || CU.matcher(mobiles).matches() || CT.matcher(mobiles).matches() || MY.matcher(mobiles).matches();
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断是否是邮箱地址
     *
     * @param emails 邮箱地址
     * @return true/false
     */
    public static boolean isEmailAdress(String emails) {
        if (emails.isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/");
        Matcher m = p.matcher(emails);
        return m.matches();
    }

    /**
     * 判断是否是数字
     *
     * @param number 数字
     * @return true/false
     */
    public static boolean isNumber(String number) {
        if (number.isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5)
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    /**
     * 通过图片的uri转换为bitmap
     *
     * @param context 上下文
     * @param uri     图片地址
     * @return bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将bitmap保存为图片文件
     *
     * @param bitmap  bitmap
     * @param picName 文件名称
     */
    public static void saveBitmap(Bitmap bitmap, String user, String picName) {
        //sd卡保存路径
        String absPath = Environment.getExternalStorageDirectory() + "/craftsman/" + user;
        try {
            File category = new File(absPath);
            File file = new File(absPath, picName + ".JPEG");
            if (file.exists()) {
                file.delete();
            } else {
                category.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态计算listView的高度以便于和scrollView结合
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 把drawable转化为bitmap
     */
    public static Bitmap drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * 生成验证码
     *
     * @return
     */
    public static String generifyCode() {
        Random random = new Random();
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int intTemp = random.nextInt(9);
            String strTemp = intTemp + "";
            sRand.append(strTemp);
        }
        return sRand.toString();
    }

    /**
     * 截取字符串的一部分
     *
     * @return
     */
    public static String getMiddleString(String src, String beg, String end) {
        String regex = beg + "(.*)" + end;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 根据时间转换成小时:分钟:秒
     */
    public static String toTimeStr(int secTotal) {
        String result = null;
        secTotal = secTotal / 1000;
        int hour = secTotal / 3600;
        int min = (secTotal % 3600) / 60;
        int sec = (secTotal % 3600) % 60;
        result = to2Str(hour) + ":" + to2Str(min) + ":" + to2Str(sec);
        return result;
    }

    public static String to2Str(int i) {
        if (i > 9) {
            return i + "";
        } else {
            return "0" + i;
        }
    }


    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 等比例缩放图片
     *
     * @param bm newWidth  newHeight
     * @return
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
//        ELog.e("====newbm====="+newbm.getHeight());
        return newbm;
    }

    /**
     * double转String,保留小数点后两位
     *
     * @param num
     * @return
     */
    public static String doubleToString(double num) {
        // 使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }

    // 秒 seconds to xx:xx:xx
    public static String secondsToTime(long seconds) {
        long temp = 0;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 / 60;
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");

        temp = seconds % 3600 % 60;
        sb.append((temp < 10) ? "0" + temp : "" + temp);
        return sb.toString();
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager(); // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0); // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }
}

