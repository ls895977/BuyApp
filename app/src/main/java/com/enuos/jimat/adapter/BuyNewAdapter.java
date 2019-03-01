package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.BuyListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class BuyNewAdapter extends RecyclerView.Adapter<BuyNewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<BuyListItem> buyList;
    private MyClickListener mListener;

    public BuyNewAdapter(Context context, ArrayList<BuyListItem> buyList) {
        this.mContext = context;
        this.buyList = buyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_buy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(buyList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return buyList.size();
    }

    public void addDataList(ArrayList<BuyListItem> buyList) {
        this.buyList.addAll(buyList);
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
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_history_buy_linear;
        TextView item_history_phone;
        TextView item_history_buy_time;

        ViewHolder(View itemView) {
            super(itemView);
            item_history_buy_linear = itemView.findViewById(R.id.item_history_buy_linear);
            item_history_phone = itemView.findViewById(R.id.item_history_phone);
            item_history_buy_time = itemView.findViewById(R.id.item_history_buy_time);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
        }

        void bindData(BuyListItem buyList, int position) {
            item_history_phone.setText(buyList.TRUE_NAME);
            item_history_buy_time.setText(buyList.CREATE_TIME);
        }
    }

}
