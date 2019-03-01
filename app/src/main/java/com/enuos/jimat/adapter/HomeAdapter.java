package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.enuos.jimat.utils.MyUtils.secondsToTime;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context mContext;
    private JSONArray mJSONArray;
    private MyClickListener mListener;

    public HomeAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = mJSONArray.getJSONObject(position);
            holder.bindData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mJSONArray.length();
    }

    public void setMyClickListener(MyClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 外部回调接口
     */
    public interface MyClickListener {
        void details(int position);
        void clock(int position);
        void buy(int position);
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private JSONObject mJSONObject;

        TextView item_home_goods_name;
        TextView item_home_goods_details;
        Banner item_home_goods_banner;
        TextView item_home_goods_old_price;
        TextView item_home_goods_new_price;
        TextView item_home_goods_time;
        ProgressBar item_home_progess;
        ImageView item_home_clock_img;
        Button item_home_btn_buy;

        ViewHolder(View itemView) {
            super(itemView);
            item_home_goods_name = itemView.findViewById(R.id.item_home_goods_name);
            item_home_goods_details = itemView.findViewById(R.id.item_home_goods_details);
            item_home_goods_banner = itemView.findViewById(R.id.item_home_goods_banner);
            item_home_goods_old_price = itemView.findViewById(R.id.item_home_goods_old_price);
            item_home_goods_new_price = itemView.findViewById(R.id.item_home_goods_new_price);
            item_home_goods_time = itemView.findViewById(R.id.item_home_goods_time);
            item_home_progess = itemView.findViewById(R.id.item_home_progess);
            item_home_clock_img = itemView.findViewById(R.id.item_home_clock_img);
            item_home_btn_buy = itemView.findViewById(R.id.item_home_btn_buy);

            // 产品详情的点击事件
            item_home_goods_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.details(getLayoutPosition());
                }
            });
            // 设置提醒的点击事件
            item_home_clock_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.clock(getLayoutPosition());
                }
            });
            // 点击购买的点击事件
            item_home_btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.buy(getLayoutPosition());
                }
            });
        }

        void bindData(JSONObject jsonObject) {
            mJSONObject = jsonObject;
            try {
                itemView.setVisibility(View.VISIBLE);

                item_home_goods_name.setText(mJSONObject.getString("GOODS_NAME"));

                /**
                 * 设置轮播图 goodsImgList
                 */
                String jsonArrayString = mJSONObject.getString("goodsImgList");
                JSONArray imgJsonArray = new JSONArray(jsonArrayString);
                int maxImage = imgJsonArray.length();

                List<String> images = new ArrayList<>();
                String postStr[] = new String[maxImage];
                for (int i=0; i<maxImage; i++) {
                    postStr[i] = "IMG_URL";
                }

                int imageSize = 0;

                for(int i=0; i<maxImage; i++) {
                    if (!imgJsonArray.getJSONObject(i).getString(postStr[i]).equals("null")) {
                        imageSize++;
                    }
                }
                final String intentImageArray[] = new String[imageSize];

                for (int i = 0; i < maxImage; i++) {
                    if (!imgJsonArray.getJSONObject(i).getString(postStr[i]).equals("null")) {
                        images.add(imgJsonArray.getJSONObject(i).getString(postStr[i]));
                        intentImageArray[i] = imgJsonArray.getJSONObject(i).getString(postStr[i]);
                    }
                }
                item_home_goods_banner.setImages(images);
                item_home_goods_banner.setImageLoader(new GlideImageLoader());
                item_home_goods_banner.setBannerAnimation(Transformer.Accordion);
                item_home_goods_banner.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void OnBannerClick(int position) {

                    }
                });

                item_home_goods_banner.start();

                String startPrice = mJSONObject.getString("GOODS_START_PRICE");
                String downType = mJSONObject.getString("GOODS_DOWN_TYPE");
                String downValue = mJSONObject.getString("GOODS_DOWN_VALUE");
                if (downType.equals("1")) { // 按照金额降价
                    double newPrice = Double.valueOf(startPrice) - Double.valueOf(downValue);
                    item_home_goods_new_price.setText(String.valueOf(newPrice));
                } else {
                    double newPrice = Double.valueOf(startPrice) - (Double.valueOf(startPrice) * Double.valueOf(downValue));
                    item_home_goods_new_price.setText(String.valueOf(newPrice));
                }

                item_home_goods_old_price.setText(startPrice);

                long second = Long.parseLong(mJSONObject.getString("GOODS_DOWN_TIME"));
                item_home_goods_time.setText(secondsToTime(second));
                int totalNumber = Integer.parseInt(mJSONObject.getString("GOODS_TOTAL_STOCK"));
                int saleNumber = Integer.parseInt(mJSONObject.getString("GOODS_SOLD_STOCK"));
                int precent = saleNumber / totalNumber;
                item_home_progess.setProgress(precent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
