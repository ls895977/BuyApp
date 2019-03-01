package com.enuos.jimat.activity.address;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.AddressListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class ChooseAddressAdapter extends RecyclerView.Adapter<ChooseAddressAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AddressListItem> addressList;
    private MyClickListener mListener;

    public ChooseAddressAdapter(Context context, ArrayList<AddressListItem> addressList) {
        this.mContext = context;
        this.addressList = addressList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(addressList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
    public void addDataList(ArrayList<AddressListItem> addressList) {
        this.addressList.addAll(addressList);
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
        void edit(int position);
    }

    /**
     * 内部 ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_address_name;
        TextView item_address_phone;
        TextView item_address_area;
        TextView item_address_default_pic;
        TextView item_address_edit;
        TextView item_address_flag;

        ViewHolder(View itemView) {
            super(itemView);
            item_address_name = itemView.findViewById(R.id.item_address_name);
            item_address_phone = itemView.findViewById(R.id.item_address_phone);
            item_address_area = itemView.findViewById(R.id.item_address_area);
            item_address_default_pic = itemView.findViewById(R.id.item_address_default_pic);
            item_address_edit = itemView.findViewById(R.id.item_address_edit);
            item_address_flag = itemView.findViewById(R.id.item_address_flag);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());

                }
            });
            // 编辑的点击事件
            item_address_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.edit(getLayoutPosition());
                }
            });
        }

        void bindData(AddressListItem addressList, int position) {
            item_address_name.setText(addressList.TAKE_NAME);
            item_address_phone.setText(addressList.TAKE_MOBILE);
//            String area = addressList.TAKE_PROVINCE + addressList.TAKE_CITY
//                    + addressList.TAKE_AREA + addressList.TAKE_ADDRESS;
            String area = addressList.TAKE_PROVINCE + " " + addressList.TAKE_ADDRESS;
            item_address_area.setText(area);
            if (addressList.IS_DEFAULT.equals("1")) {
                item_address_default_pic.setVisibility(View.VISIBLE);
            } else {
                item_address_default_pic.setVisibility(View.GONE);
            }
            item_address_flag.setText("    " + addressList.TAKE_NAME.substring(0, 1) + "    ");
        }
    }

}
