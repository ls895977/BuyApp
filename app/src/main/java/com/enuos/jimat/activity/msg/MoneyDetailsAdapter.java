package com.enuos.jimat.activity.msg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.MoneyDetailsListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class MoneyDetailsAdapter extends RecyclerView.Adapter<MoneyDetailsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MoneyDetailsListItem> moneyDetailsList;

    public MoneyDetailsAdapter(Context context, ArrayList<MoneyDetailsListItem> moneyDetailsList) {
        this.mContext = context;
        this.moneyDetailsList = moneyDetailsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mine_money_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(moneyDetailsList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return moneyDetailsList.size();
    }

    public void addDataList(ArrayList<MoneyDetailsListItem> moneyDetailsList) {
        this.moneyDetailsList.addAll(moneyDetailsList);
        notifyDataSetChanged();
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_money_details_type;
        TextView item_money_details_price;
        TextView item_money_details_time;
        TextView item_money_details_flag;

        ViewHolder(View itemView) {
            super(itemView);
            item_money_details_type = itemView.findViewById(R.id.item_money_details_type);
            item_money_details_price = itemView.findViewById(R.id.item_money_details_price);
            item_money_details_time = itemView.findViewById(R.id.item_money_details_time);
            item_money_details_flag = itemView.findViewById(R.id.item_money_details_flag);
        }

        void bindData(MoneyDetailsListItem moneyDetailsList, int position) {
//            double oldPrice = Double.valueOf(moneyDetailsList.OLD_MONEY);
//            double newPrice = Double.valueOf(moneyDetailsList.NEW_MONEY);
            /*String stringFlag, stringValue;
            if (oldPrice < newPrice) {
                stringFlag = "+";
                stringValue = String.valueOf(newPrice - oldPrice);
            } else {
                stringFlag = "-";
                stringValue = String.valueOf(oldPrice - newPrice);
            }*/
            String stringFlag = moneyDetailsList.BALANCE_MONEY.substring(0, 1);
            String stringEnd = moneyDetailsList.BALANCE_MONEY.substring(1, moneyDetailsList.BALANCE_MONEY.length());

            item_money_details_type.setText(moneyDetailsList.BALANCE_TYPE);
            item_money_details_time.setText(moneyDetailsList.CREATE_TIME);
            item_money_details_price.setText("RM " + stringEnd);
            item_money_details_flag.setText(stringFlag);
        }
    }

}
