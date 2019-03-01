package com.vedeng.widget.base.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.R;

/**********************************************************
 * @文件名称：CommonDialog.java
 * @文件作者：聂中泽
 * @创建时间：2014年9月18日 下午7:02:43
 * @文件描述：公用的对话框
 * @修改历史：2014年9月18日创建初始版本
 **********************************************************/
public abstract class CommonDialog extends Dialog {
    private ViewGroup contentView;

    protected int dialogWidth = 258;
    protected int dialogHeight = LayoutParams.WRAP_CONTENT;
    protected DialogClickListener cancelDialogClick;
    protected DialogClickListener confirmDialogClick;
    protected boolean isCancelable = true;
    protected boolean confirmBtnCamcel = true;
    protected RelativeLayout contentLayout;
    protected String cancelBtnText;
    protected String confirmBtnText;
    protected int cancelBtnTextColor;
    protected int confirmBtnTextColor;
    protected int dialogBtnTextSize;
    protected int dialogSplitLineColor;
    protected boolean confirmBold = false;

    public CommonDialog setConfirmBold(boolean confirmBold) {
        this.confirmBold = confirmBold;
        return this;
    }

    public CommonDialog setCancelBold(boolean cancelBold) {
        this.cancelBold = cancelBold;
        return this;
    }

    protected boolean cancelBold = false;

    public interface DialogClickListener {
        public void onDialogClick();
    }

    public CommonDialog(Context context) {
        super(context, R.style.customDialog);
        cancelBtnText = context.getString(R.string.cancel);
        confirmBtnText = context.getString(R.string.confirm);
        cancelBtnTextColor = context.getResources().getColor(R.color.common_dialog_text);
        confirmBtnTextColor = context.getResources().getColor(R.color.common_dialog_text);
        dialogBtnTextSize = 14;
        dialogSplitLineColor = context.getResources().getColor(R.color.common_dialog_line);
    }

    /**
     * 设置对话框的内容View
     *
     * @param contentView
     * @return
     */
    public CommonDialog setDialogContentView(View contentView) {
        if (contentView.getParent() == null)
            contentLayout.addView(contentView);
        return this;
    }

    /**
     * 设置取消按钮文案
     *
     * @param characterResId
     * @return
     */
    public CommonDialog setCancelBtnText(int characterResId) {
        return setCancelBtnText(getContext().getString(characterResId));
    }

    /**
     * 设置确定按钮文案
     *
     * @param characterResId
     * @return
     */
    public CommonDialog setConfirmBtnText(int characterResId) {
        return setConfirmBtnText(getContext().getString(characterResId));
    }

    /**
     * 设置取消按钮文案
     *
     * @param character
     * @return
     */
    public CommonDialog setCancelBtnText(String character) {
        this.cancelBtnText = character;
        return this;
    }

    /**
     * 设置确定按钮文案
     *
     * @param character
     * @return
     */
    public CommonDialog setConfirmBtnText(String character) {
        this.confirmBtnText = character;
        return this;
    }

    /**
     * 设置取消按钮文字颜色
     *
     * @param colorResId
     * @return
     */
    public CommonDialog setCancelBtnTextColor(int colorResId) {
        this.cancelBtnTextColor = colorResId;
        return this;
    }

    /**
     * 设置确定按钮文字颜色
     *
     * @param colorResId
     * @return
     */
    public CommonDialog setConfirmTextColor(int colorResId) {
        this.confirmBtnTextColor = colorResId;
        return this;
    }

    /**
     * 设置确定按钮点击事件
     *
     * @param confirmClick
     * @return
     */
    public CommonDialog setConfirmDialogListener(DialogClickListener confirmClick) {
        this.confirmDialogClick = confirmClick;
        return this;
    }

    /**
     * 设置取消按钮点击事件
     *
     * @param cancelClick
     * @return
     */
    public CommonDialog setCancelDialogListener(DialogClickListener cancelClick) {
        this.cancelDialogClick = cancelClick;
        return this;
    }

    /**
     * 设置对话框是否可以自动关闭
     *
     * @param isCancelable
     * @return
     */
    public CommonDialog setDialogCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
        return this;
    }

    /**
     * 设置按钮文字大小
     *
     * @param size(单位:sp)
     * @return
     */
    public CommonDialog setButtonTextSize(int size) {
        this.dialogBtnTextSize = size;
        return this;
    }

    /**
     * 设置对话框分割线颜色
     *
     * @param colorResId
     * @return
     */
    public CommonDialog setDialogSplitLineColor(int colorResId) {
        this.dialogSplitLineColor = colorResId;
        return this;
    }

    /**
     * 设置对话框宽度
     *
     * @param dialogWidth
     * @return
     */
    public CommonDialog setDialogWidth(int dialogWidth) {
        this.dialogWidth = dialogWidth;
        return this;
    }

    /**
     * 设置对话框高度
     *
     * @param dialogHeight
     * @return
     */
    public CommonDialog setDialogHeight(int dialogHeight) {
        this.dialogHeight = dialogHeight;
        return this;
    }

    /**
     * 设置点击确定按钮后是否自动关闭dialog
     *
     * @param confirmBtnCamcel
     * @return
     */
    public CommonDialog setDialogConfirmBtnCancel(boolean confirmBtnCamcel) {
        this.confirmBtnCamcel = confirmBtnCamcel;
        return this;
    }

    public abstract CommonDialog build();

    /**
     * 创建一个简单的对话框
     *
     * @param simpleTextSize
     * @param simpleTextColor
     * @param simpleTextCharacter
     * @return
     */
    public CommonDialog buildSimpleDialog(int simpleTextSize, int simpleTextColor, String simpleTextCharacter) {
        LayoutInflater.from(getContext()).inflate(R.layout.common_dialog_simple_content, contentLayout);
        TextView contextMsg = (TextView) findChildViewById(R.id.common_dialog_content_msg);
        contextMsg.setText(simpleTextCharacter);
        contextMsg.setTextSize(simpleTextSize);
        contextMsg.setTextColor(getContext().getResources().getColor(simpleTextColor));
        return build();
    }

    /**
     * 创建一个简单的对话框
     *
     * @param simpleTextColor
     * @param simpleTextCharacter
     * @return
     */
    public CommonDialog buildSimpleDialog(int simpleTextColor, String simpleTextCharacter) {
        return buildSimpleDialog(14, simpleTextColor, simpleTextCharacter);
    }

    /**
     * 创建一个简单的对话框
     *
     * @param simpleTextCharacter
     * @return
     */
    public CommonDialog buildSimpleDialog(String simpleTextCharacter) {
        return buildSimpleDialog(14, R.color.common_dialog_text, simpleTextCharacter);
    }

    protected ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    protected void setParams(int width, int height) {
        WindowManager.LayoutParams dialogParams = this.getWindow().getAttributes();
        dialogParams.width = width;
        dialogParams.height = height;
        this.getWindow().setAttributes(dialogParams);
    }

    protected View findChildViewById(int id) {
        return contentView.findViewById(id);
    }

    protected void setDialogWidthAndHeight() {
        setParams(
                dialogWidth == LayoutParams.WRAP_CONTENT ? LayoutParams.WRAP_CONTENT : Utils.toDip(getContext(),
                        dialogWidth),
                dialogHeight == LayoutParams.WRAP_CONTENT ? LayoutParams.WRAP_CONTENT : Utils.toDip(getContext(),
                        dialogHeight));
    }

}
