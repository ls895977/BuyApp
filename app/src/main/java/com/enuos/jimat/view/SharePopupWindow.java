package com.enuos.jimat.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.enuos.jimat.R;


/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/4 22:53
 * @文件描述： 自定义 popupWindow 类
 *  *
 * @修改历史： 2018/12/4 创建初始版本
 **********************************************************/

public class SharePopupWindow extends PopupWindow {

    private View mMenuView; // PopupWindow 菜单布局
    private Context context; // 上下文参数
    private View.OnClickListener takeOnClick;
    private View.OnClickListener chooseOnClick;

    public SharePopupWindow(Activity context,
                            View.OnClickListener takeOnClick, View.OnClickListener chooseOnClick) {
        super(context);
        this.context = context;
        this.takeOnClick = takeOnClick;
        this.chooseOnClick = chooseOnClick;
        Init();
    }

    private void Init() {
        // popupWindow 导入
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.share_popup_window, null);
        ImageButton btn_wexin = mMenuView
                .findViewById(R.id.btn_wexin);
        ImageButton btn_circle = mMenuView
                .findViewById(R.id.btn_circle);
        LinearLayout linear_cancel = mMenuView
                .findViewById(R.id.linear_cancel);

        btn_wexin.setOnClickListener(takeOnClick);
        btn_circle.setOnClickListener(chooseOnClick);
        linear_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 导入布局
        this.setContentView(mMenuView);

        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置可触
        this.setFocusable(true);
        this.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);

        // 单击 popupWindow 以外即关闭
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.ll_pop_share).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
