package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class HistoryPriceAdapter extends RecyclerView.Adapter<HistoryPriceAdapter.ViewHolder> {
    private Context mContext;
    private JSONArray mJSONArray;

    public HistoryPriceAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_price, parent, false);
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

        LinearLayout item_history_price_linear;
        TextView item_price_time;
        TextView item_price_price;

        ViewHolder(View itemView) {
            super(itemView);
            item_history_price_linear = itemView.findViewById(R.id.item_history_price_linear);
            item_price_time = itemView.findViewById(R.id.item_price_time);
            item_price_price = itemView.findViewById(R.id.item_price_price);
        }

        void bindData(JSONObject jsonObject) {
            mJSONObject = jsonObject;
            try {
                item_price_time.setText(mJSONObject.getString("GOODS_START_TIME"));
                item_price_price.setText("￥" + mJSONObject.getString("GOODS_OLD_PRICE"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
