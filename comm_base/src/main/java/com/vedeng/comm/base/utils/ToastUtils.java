package com.vedeng.comm.base.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.vedeng.comm.base.MicCommonConfigHelper;

/**********************************************************
 * @文件名称：ToastUtil.java
 * @文件作者：聂中泽
 * @创建时间：2014年10月22日 上午11:16:49
 * @文件描述：提醒工具类--toast:普通吐司 show：优化后的吐司
 * @修改历史：2014年10月22日创建初始版本
 **********************************************************/
public class ToastUtils {
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    private static Toast toast;
    private static Handler handler = new Handler();
    private static Runnable run = new Runnable() {
        public void run() {
            toast.cancel();
        }
    };

    private ToastUtils() {

    }

    public static void toast(Context context, Object obj, int duration) {
        if (context != null && obj != null) {
            Toast.makeText(context, obj.toString(), duration).show();
        }
    }

    public static void toast(Context context, Object obj) {
        toast(context, obj.toString(), Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, int resId) {
        toast(context, context.getString(resId));
    }

    public static void toastInParentCenter(Context context, Object obj, int duration) {
        if (context != null && obj != null) {
            Toast toast = Toast.makeText(context, obj.toString(), duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void toastInParentCenter(Context context, Object obj) {
        toastInParentCenter(context, obj.toString(), Toast.LENGTH_SHORT);
    }

    public static void toastInParentCenter(Context context, int resId) {
        toastInParentCenter(context, context.getString(resId));
    }

    private static void toast(Context ctx, CharSequence msg, int duration) {
        handler.removeCallbacksAndMessages(null);
        // handler的duration不能直接对应Toast的常量时长，在此针对Toast的常量相应定义时长
        switch (duration) {
            case LENGTH_SHORT:// Toast.LENGTH_SHORT值为0，对应的持续时间大概为1s
                duration = 1000;
                break;
            case LENGTH_LONG:// Toast.LENGTH_LONG值为1，对应的持续时间大概为3s
                duration = 3000;
                break;
            default:
                break;
        }
        if (null != toast) {
            toast.cancel();
        }
        if (ctx == null) {
            toast = Toast.makeText(MicCommonConfigHelper.getInstance().getContext(), msg, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(ctx.getApplicationContext(), msg, Toast.LENGTH_LONG);
        }

        handler.postDelayed(run, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    /**
     * 弹出Toast
     *
     * @param ctx 弹出Toast的上下文
     * @param msg 弹出Toast的内容
     */
    public static void show(Context ctx, CharSequence msg) throws NullPointerException {
        show(ctx, msg, LENGTH_LONG);
    }

    /**
     * 弹出Toast
     *
     * @param ctx 弹出Toast的上下文
     * @param msg 弹出Toast的内容
     */
    public static void showLong(Context ctx, CharSequence msg) throws NullPointerException {
        show(ctx, msg, LENGTH_LONG);
    }

    /**
     * 弹出Toast
     *
     * @param ctx 弹出Toast的上下文
     * @param resId 弹出Toast的内容
     */
    public static void showLong(Context ctx, int resId) throws NullPointerException {
        show(ctx, resId, LENGTH_LONG);
    }

    /**
     * 弹出Toast
     *
     * @param ctx   弹出Toast的上下文
     * @param resId
     * @throws NullPointerException
     */
    public static void show(Context ctx, int resId) throws NullPointerException {
        show(ctx, ctx.getResources().getString(resId), LENGTH_LONG);
    }

    /**
     * 弹出Toast
     *
     * @param ctx      弹出Toast的上下文
     * @param resId
     * @param duration 弹出Toast的持续时间
     * @throws NullPointerException
     */
    public static void show(Context ctx, int resId, int duration) throws NullPointerException {
        show(ctx, ctx.getResources().getString(resId), duration);
    }

    /**
     * 弹出Toast
     *
     * @param ctx       弹出Toast的上下文
     * @param resObject
     * @throws NullPointerException
     */
    public static void show(Context ctx, Object resObject) throws NullPointerException {
        show(ctx, resObject.toString(), LENGTH_LONG);
    }

    /**
     * 弹出Toast
     *
     * @param ctx       弹出Toast的上下文
     * @param resObject
     * @param duration  弹出Toast的持续时间
     * @throws NullPointerException
     */
    public static void show(Context ctx, Object resObject, int duration) throws NullPointerException {
        show(ctx, resObject.toString(), duration);
    }

    /**
     * 弹出Toast
     *
     * @param ctx      弹出Toast的上下文
     * @param msg      弹出Toast的内容
     * @param duration 弹出Toast的持续时间
     */
    public static void show(Context ctx, CharSequence msg, int duration) throws NullPointerException {
        if (0 > duration) {
            duration = LENGTH_SHORT;
        }
        if (ctx == null) {
            toast(MicCommonConfigHelper.getInstance().getContext(), msg, duration);
        } else {
            toast(ctx, msg, duration);
        }
    }

    public static void cancel() {
        if (null != toast)
            toast.cancel();
    }
}
