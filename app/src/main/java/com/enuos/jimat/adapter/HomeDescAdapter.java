package com.enuos.jimat.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enuos.jimat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class HomeDescAdapter extends RecyclerView.Adapter<HomeDescAdapter.ViewHolder> {
    private Context mContext;
    private JSONArray mJSONArray;
    private MyClickListener mListener;

    public HomeDescAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_desc, parent, false);
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
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private JSONObject mJSONObject;

        ImageView item_home_desc_img;
        TextView item_home_desc_title;
        TextView item_home_desc_price;

        ViewHolder(View itemView) {
            super(itemView);
            item_home_desc_img = itemView.findViewById(R.id.item_home_desc_img);
            item_home_desc_title = itemView.findViewById(R.id.item_home_desc_title);
            item_home_desc_price = itemView.findViewById(R.id.item_home_desc_price);

            // 产品详情的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.details(getLayoutPosition());
                }
            });
        }

        void bindData(JSONObject jsonObject) {
            mJSONObject = jsonObject;
            try {

                item_home_desc_title.setText(mJSONObject.getString("GOODS_NAME"));
                item_home_desc_price.setText("RM " + mJSONObject.getString("GOODS_START_PRICE"));
                item_home_desc_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.tupian);
                Glide.with(mContext).load(mJSONObject.getString("IMAGE_URL")).apply(options).into(item_home_desc_img);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
