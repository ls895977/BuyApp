package com.enuos.jimat.activity.goods;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.fragment.BaseFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/9 2:08
 * @文件描述： 全部订单
 * @修改历史： 2018/12/9 创建初始版本
 **********************************************************/
public class DetailsDetailsFragment extends BaseFragment {

    @BindView(R.id.details_details_text)
    TextView tv;
    private Unbinder unbinder;
    @BindView(R.id.details_details_scroll)
    ScrollView mScroll;
    private Spanned text;
    private Spanned text2 = new SpannableString("");

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.textview, null);
    }

    @Override
    public void initData() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        TextView stringTv = getActivity().findViewById(R.id.goods_details_details_default);
        String stringDetails = stringTv.getText().toString();
        URLImageParser imageGetter = new URLImageParser(tv);
        text = Html.fromHtml(stringDetails, imageGetter, null);
        tv.setText(text);
    }

    public class URLImageParser implements Html.ImageGetter {
        TextView mTextView;

        public URLImageParser(TextView textView) {
            this.mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            ImageLoader.getInstance().loadImage(source,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            urlDrawable.bitmap = loadedImage;
                            urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
                            mTextView.invalidate();
                            mTextView.setText(mTextView.getText());
                        }
                    });
            return urlDrawable;
        }
    }

    public class URLDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        ((GoodsDetailsActivity) getActivity()).mViewPager.setObjectForPosition(rootView, 0);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void moveFirst() {
        tv.setText(text2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        }, 100);
    }
}