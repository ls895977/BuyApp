package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
public class HistoryBuyAdapter extends RecyclerView.Adapter<HistoryBuyAdapter.ViewHolder> {
    private Context mContext;
    private JSONArray mJSONArray;

    public HistoryBuyAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_buy, parent, false);
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

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private JSONObject mJSONObject;

        LinearLayout item_history_buy_linear;
        ImageView item_history_buy_pic;
        TextView item_history_phone;
        TextView item_history_buy_time;

        ViewHolder(View itemView) {
            super(itemView);
            item_history_buy_linear = itemView.findViewById(R.id.item_history_buy_linear);
            item_history_buy_pic = itemView.findViewById(R.id.item_history_buy_pic);
            item_history_phone = itemView.findViewById(R.id.item_history_phone);
            item_history_buy_time = itemView.findViewById(R.id.item_history_buy_time);
        }

        void bindData(JSONObject jsonObject) {
            mJSONObject = jsonObject;
            try {
                item_history_phone.setText(mJSONObject.getString("TRUE_NAME"));
                item_history_buy_time.setText(mJSONObject.getString("CREATE_TIME"));
                Glide.with(mContext).load(mJSONObject.getString("MEMBER_AVATAR")).into(item_history_buy_pic);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
