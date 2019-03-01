package com.enuos.jimat.activity.common;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowBannerActivity extends BaseActivity {

    @BindView(R.id.show_banner)
    Banner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_banner);
        ButterKnife.bind(this);

        /**
         /**
         * 设置轮播图
         */
        List<String> images = new ArrayList<>();
        String intentImageArray[] = getIntent().getStringArrayExtra("intentImageArray");
        int imageLength = intentImageArray.length;
        for (int i=0; i<imageLength; i++) {
            images.add(intentImageArray[i]);
        }
        mBanner.setImages(images);
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setBannerAnimation(Transformer.Accordion);

        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                finish();
            }
        });

        mBanner.start();
    }

    /**
     * Glide 图片加载类
     */
    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }
}
