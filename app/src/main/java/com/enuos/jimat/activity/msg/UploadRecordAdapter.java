package com.enuos.jimat.activity.msg;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.UploadRecordListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class UploadRecordAdapter extends RecyclerView.Adapter<UploadRecordAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<UploadRecordListItem> uploadRecordList;
    private MyClickListener mListener;

    public UploadRecordAdapter(Context context, ArrayList<UploadRecordListItem> uploadRecordList) {
        this.mContext = context;
        this.uploadRecordList = uploadRecordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_money_upload_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(uploadRecordList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return uploadRecordList.size();
    }

    public void addDataList(ArrayList<UploadRecordListItem> uploadRecordList) {
        this.uploadRecordList.addAll(uploadRecordList);
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

        LinearLayout item_upload_record_linear;
        ImageView item_upload_record_pic;
        TextView item_upload_record_date;
        TextView item_upload_record_status;

        ViewHolder(View itemView) {
            super(itemView);
            item_upload_record_linear = itemView.findViewById(R.id.item_upload_record_linear);
            item_upload_record_pic = itemView.findViewById(R.id.item_upload_record_pic);
            item_upload_record_date = itemView.findViewById(R.id.item_upload_record_date);
            item_upload_record_status = itemView.findViewById(R.id.item_upload_record_status);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
        }

        void bindData(UploadRecordListItem uploadRecordList, int position) {
//                Glide.with(mContext).load(mJSONObject.getString("")).asBitmap().into(item_upload_record_pic);
            item_upload_record_date.setText(uploadRecordList.CREATE_TIME);
            String status = uploadRecordList.STATE;
            switch (status) {
                case "1":
                    item_upload_record_status.setText("Processing");
                    item_upload_record_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_FB9C33));
                    break;
                case "2":
                    item_upload_record_status.setText("Failed");
                    item_upload_record_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_D02D2E));
                    break;
                case "3":
                    item_upload_record_status.setText("Successful");
                    item_upload_record_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_00B403));
                    break;
            }
        }
    }

}
