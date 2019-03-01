package com.enuos.jimat.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/30 0:58
 * @文件描述：
 * @修改历史： 2018/12/30 创建初始版本
 **********************************************************/
public class BmpToByteArray {
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 80, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
                output.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
        return result;
    }
}
