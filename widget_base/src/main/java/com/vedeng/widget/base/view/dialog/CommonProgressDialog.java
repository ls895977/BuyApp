package com.vedeng.widget.base.view.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;

/**********************************************************
 * @文件名称：CommonProgressDialog.java
 * @文件作者：聂中泽
 * @创建时间：2014年7月22日 下午4:09:53
 * @文件描述：公共Loading对话框
 * @修改历史：2014年7月22日创建初始版本
 **********************************************************/
public class CommonProgressDialog {
    private ProgressDialog progressDialog;
    private static CommonProgressDialog utils;

    static {
        utils = new CommonProgressDialog();
    }

    private CommonProgressDialog() {
    }

    public static CommonProgressDialog getInstance() {
        return utils;
    }

    /**
     * 显示进度条对话框(不可取消)
     *
     * @param context
     * @param msg
     */
    public void showProgressDialog(Context context, String msg) {
        showProgressDialog(context, null, msg);
    }

    /**
     * 显示进度条对话框(可取消)
     *
     * @param context
     * @param msg
     */
    public void showCancelableProgressDialog(Context context, String msg) {
        showCancelableProgressDialog(context, null, msg);
    }

    /**
     * 显示进度条对话框(不可取消)
     *
     * @param context
     * @param title
     * @param msg
     */
    public void showProgressDialog(Context context, String title, String msg) {
        showProgressDialog(context, title, msg, false, null);
    }

    /**
     * 显示进度条对话框(可取消)
     *
     * @param context
     * @param title
     * @param msg
     */
    public void showCancelableProgressDialog(Context context, String title, String msg) {
        showProgressDialog(context, title, msg, true, null);
    }

    /**
     * 显示进度条对话框
     *
     * @param context
     * @param title
     * @param msg
     * @param isCancelable
     * @param dismissListener
     */
    public void showProgressDialog(Context context, String title, String msg, boolean isCancelable,
                                   OnDismissListener dismissListener) {
        if (context == null)
            return;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(isCancelable);
        if (dismissListener != null) {
            progressDialog.setOnDismissListener(dismissListener);
        }
        progressDialog.show();
    }

    public void setMessage(String msg) {
        if (progressDialog != null && !TextUtils.isEmpty(msg)) {
            progressDialog.setMessage(msg);
        }
    }

    /**
     * 关闭进度条对话框
     */
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog = null;
            }
        }
    }

    /**
     * 进度条是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return progressDialog != null ? progressDialog.isShowing() : false;
    }
}
