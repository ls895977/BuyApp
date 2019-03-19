package com.enuos.jimat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.activity.goods.GoodsDetailsActivity;
import com.enuos.jimat.activity.home.newInfo.HomeNewActivity;
import com.example.myvideoplayer.JCVideoPlayer;
import com.example.myvideoplayer.JCVideoPlayerStandard;

import java.util.ArrayList;
import java.util.List;


/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2019/2/24 23:58
 * @文件描述：
 * @修改历史： 2019/2/24 创建初始版本
 **********************************************************/
public class BannerViewAdapter extends PagerAdapter {
    private Context               context;
    private List<BannerModel>     listBean;
    private setOnClick            onClick;
    private JCVideoPlayerStandard jcVideoPlayer;
    private String                goodsId;
    private String                homeTime;

    public BannerViewAdapter(Activity context, List<BannerModel> list, String id, String time) {
        this.context = context;
        jcVideoPlayer = new JCVideoPlayerStandard(context);
        goodsId = id;
        homeTime = time;
        if (list == null || list.size() == 0) {
            this.listBean = new ArrayList<>();
        } else {
            this.listBean = list;
        }
    }

    public BannerViewAdapter(Activity context, List<BannerModel> list) {
        //        this.context = context.getApplicationContext();
        this.context = context;
        jcVideoPlayer = new JCVideoPlayerStandard(context);
        if (list == null || list.size() == 0) {
            this.listBean = new ArrayList<>();
        } else {
            this.listBean = list;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (listBean.get(position).getUrlType() == 0) {//图片
            final ImageView imageView = new ImageView(context);

            Glide.with(context).load(listBean.get(position).getBannerUrl())
                    //                    .skipMemoryCache(true)
                    .into(imageView);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("aa","-----------goodsId-"+goodsId+"-------homeTime--"+homeTime);
                    Intent intentInfo = new Intent(context, GoodsDetailsActivity.class);
                    intentInfo.putExtra("goodsId", goodsId);
                    intentInfo.putExtra("goodsType", "base");
                    intentInfo.putExtra("type", "home");
                    intentInfo.putExtra("value", homeTime+"");
                    context.startActivity(intentInfo);
                    Log.e("aa","------------点击图片");
                    if (onClick != null)
                        onClick.click(v);
                }
            });
            if (context instanceof HomeNewActivity) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (goodsId == null || homeTime == null)
                            return;
                        Intent intentInfo = new Intent(context, GoodsDetailsActivity.class);
                        intentInfo.putExtra("goodsId", goodsId);
                        intentInfo.putExtra("goodsType", "base");
                        intentInfo.putExtra("type", "home");
                        intentInfo.putExtra("value", homeTime);
                        context.startActivity(intentInfo);
                    }
                });
            }
            return imageView;
        } else {//视频
            //            final VideoView videoView = new VideoView(context);
            //            videoView.setVideoURI(Uri.parse(listBean.get(position).getBannerUrl()));
            //            //开始播放
            //            videoView.start();
            //            container.addView(videoView);
            jcVideoPlayer.setUp(listBean.get(position).getBannerUrl()
                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            jcVideoPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.click(view);
                    Log.e("aa","------------点击视频");
                }
            });
            Glide.with(context)
                    .load(listBean.get(position).getVideoPic())
                    .into(jcVideoPlayer.thumbImageView);
            //            jcVideoPlayer.prepareMediaPlayer();
            container.addView(jcVideoPlayer);
            return jcVideoPlayer;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    public interface setOnClick {
        void click(View view);
    }

    public void setOnClick(setOnClick onClick) {
        this.onClick = onClick;
    }

    public boolean onBack() {
        return JCVideoPlayer.backPress();
    }


    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }
}
