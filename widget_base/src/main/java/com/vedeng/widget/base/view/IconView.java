package com.vedeng.widget.base.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2018/11/21 20:42
 * @文件描述：
 * @修改历史：2018/11/21 创建初始版本
 **********************************************************/
public class IconView extends AppCompatTextView {
    public IconView(Context context) {
        super(context);
        init(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 设置字体图标
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts.ttf"));
    }
}
