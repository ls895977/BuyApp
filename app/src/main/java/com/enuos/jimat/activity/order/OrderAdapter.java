package com.enuos.jimat.activity.order;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.module.OrderListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<OrderListItem> orderList;
    private MyClickListener mListener;

    public OrderAdapter(Context context, ArrayList<OrderListItem> orderList) {
        this.mContext = context;
        this.orderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(orderList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void addDataList(ArrayList<OrderListItem> orderList) {
        this.orderList.addAll(orderList);
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

        TextView item_order_shop_name;
        TextView item_order_status;
        ImageView item_order_goods_pic;
        TextView item_order_goods_name;
        TextView item_order_goods_price;

        ViewHolder(View itemView) {
            super(itemView);
            item_order_shop_name = itemView.findViewById(R.id.item_order_shop_name);
            item_order_status = itemView.findViewById(R.id.item_order_status);
            item_order_goods_pic = itemView.findViewById(R.id.item_order_goods_pic);
            item_order_goods_name = itemView.findViewById(R.id.item_order_goods_name);
            item_order_goods_price = itemView.findViewById(R.id.item_order_goods_price);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
        }

        void bindData(OrderListItem orderListItem, int position) {
            item_order_shop_name.setText(orderListItem.COMPANY_NAME);
            if (orderListItem.PAY_STATE.equals("1")) {
                item_order_status.setText("Pending");
                item_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_FB9C33));
            } else if (orderListItem.PAY_STATE.equals("2")) {
                item_order_status.setText("Success");
                item_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_00B403));
            } else {
                item_order_status.setText("Failed");
                item_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_D02D2E));
            }
            String strPic = orderListItem.GOODS_IMG;
            if (strPic != null) {
                Glide.with(mContext).load(strPic.replaceAll("\'", "\"")).into(item_order_goods_pic);
            }
            item_order_goods_name.setText(orderListItem.GOODS_NAME);
            item_order_goods_price.setText("RM " + orderListItem.TOTAL_PRICE);
        }
    }

}
