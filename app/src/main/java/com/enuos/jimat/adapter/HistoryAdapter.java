package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.module.HistoryListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<HistoryListItem> historyList;
    private MyClickListener mListener;

    public HistoryAdapter(Context context, ArrayList<HistoryListItem> historyList) {
        this.mContext = context;
        this.historyList = historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mine_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(historyList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void addDataList(ArrayList<HistoryListItem> historyList) {
        this.historyList.addAll(historyList);
        notifyDataSetChanged();
    }

    public void setMyClickListener(MyClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 外部回调接口
     */
    public interface MyClickListener {
        void select(int position);
        void btnPrice(int position);
        void btnNuy(int position);
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_history_linear_all;
        ImageView item_history_pic;
        TextView item_history_name;
        TextView item_history_price;
        Button item_history_btn_price;
        TextView item_history_sales;
        Button item_history_btn_buy;

        ViewHolder(View itemView) {
            super(itemView);
            item_history_linear_all = itemView.findViewById(R.id.item_history_linear_all);
            item_history_pic = itemView.findViewById(R.id.item_history_pic);
            item_history_name = itemView.findViewById(R.id.item_history_name);
            item_history_price = itemView.findViewById(R.id.item_history_price);
            item_history_btn_price = itemView.findViewById(R.id.item_history_btn_price);
            item_history_sales = itemView.findViewById(R.id.item_history_sales);
            item_history_btn_buy = itemView.findViewById(R.id.item_history_btn_buy);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
            // 价格变动记录的点击事件
            item_history_btn_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.btnPrice(getLayoutPosition());
                }
            });
            // 购买记录的点击事件
            item_history_btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.btnNuy(getLayoutPosition());
                }
            });
        }

        void bindData(HistoryListItem historyListItem, int position) {
            item_history_name.setText(historyListItem.GOODS_NAME);
            item_history_price.setText("RM " + historyListItem.GOODS_START_PRICE);
//                item_history_sales.setText(mJSONObject.getString("GOODS_SOLD_STOCK") + "人已买");
            String strPic = historyListItem.IMAGE_URL;
            if (strPic != null) {
                Glide.with(mContext).load(strPic.replaceAll("\'", "\"")).into(item_history_pic);
            }
        }
    }

}
