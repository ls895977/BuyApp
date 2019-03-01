package com.vedeng.widget.base.view.dialog;

import android.content.Context;
import android.widget.RelativeLayout;

import com.vedeng.widget.base.R;

/**********************************************************
 * @文件名称：CommonNoneBtnDialog.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月24日 上午10:01:11
 * @文件描述：创建没有按钮的对话框
 * @修改历史：2016年2月24日创建初始版本
 **********************************************************/
public class CommonNoneBtnDialog extends CommonDialog {
    public CommonNoneBtnDialog(Context context) {
        super(context);

        setContentView(createDialogView(R.layout.common_none_btn_dialog));
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

        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isCancelable);
        show();
        return this;
    }
}
