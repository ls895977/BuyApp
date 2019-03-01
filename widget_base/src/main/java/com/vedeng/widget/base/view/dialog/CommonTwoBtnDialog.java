package com.vedeng.widget.base.view.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vedeng.widget.base.R;

/**********************************************************
 * @文件名称：CommonTwoBtnDialog.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月24日 上午9:55:14
 * @文件描述：创建含有两个按钮的对话框
 * @修改历史：2016年2月24日创建初始版本
 **********************************************************/
public class CommonTwoBtnDialog extends CommonDialog {
    public CommonTwoBtnDialog(Context context) {
        super(context);

        setContentView(createDialogView(R.layout.common_two_btn_dialog));
        contentLayout = (RelativeLayout) findChildViewById(R.id.common_dialog_content_layout);
    }

    /**
     * 创建一个对话框
     *
     * @return
     */
    @Override
    public CommonDialog build() {
        setDialogWidthAndHeight();

        View line1 = findChildViewById(R.id.common_dialog_line1);
        line1.setBackgroundColor(dialogSplitLineColor);
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isCancelable);

        View line2 = findChildViewById(R.id.common_dialog_line2);
        line2.setBackgroundColor(dialogSplitLineColor);

        Button btnCancel = (Button) findChildViewById(R.id.common_dialog_btn_cancel);
        btnCancel.setText(cancelBtnText);
        btnCancel.setTextColor(cancelBtnTextColor);
        btnCancel.setTextSize(dialogBtnTextSize);
        if(cancelBold){
            btnCancel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelDialogClick != null)
                    cancelDialogClick.onDialogClick();
            }
        });

        Button btnComfirm = (Button) findChildViewById(R.id.common_dialog_btn_confirm);
        btnComfirm.setText(confirmBtnText);
        btnComfirm.setTextColor(confirmBtnTextColor);
        btnComfirm.setTextSize(dialogBtnTextSize);
        if(confirmBold){
            btnComfirm.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmBtnCamcel)
                    dismiss();
                if (confirmDialogClick != null)
                    confirmDialogClick.onDialogClick();
            }
        });
        show();
        return this;
    }
}
