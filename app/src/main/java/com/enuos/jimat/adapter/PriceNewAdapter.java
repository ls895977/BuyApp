package com.enuos.jimat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.PriceListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class PriceNewAdapter extends RecyclerView.Adapter<PriceNewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PriceListItem> priceList;
    private MyClickListener mListener;

    public PriceNewAdapter(Context context, ArrayList<PriceListItem> priceList) {
        this.mContext = context;
        this.priceList = priceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_price, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(priceList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

    public void addDataList(ArrayList<PriceListItem> priceList) {
        this.priceList.addAll(priceList);
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

        LinearLayout item_history_price_linear;
        TextView item_price_time;
        TextView item_price_price;

        ViewHolder(View itemView) {
            super(itemView);
            item_history_price_linear = itemView.findViewById(R.id.item_history_price_linear);
            item_price_time = itemView.findViewById(R.id.item_price_time);
            item_price_price = itemView.findViewById(R.id.item_price_price);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
        }

        void bindData(PriceListItem priceList, int position) {
            item_price_time.setText(priceList.GOODS_TIME);
            item_price_price.setText("RM " + priceList.GOODS_PRICE);
        }
    }

}
