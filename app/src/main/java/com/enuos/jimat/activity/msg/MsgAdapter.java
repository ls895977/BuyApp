package com.enuos.jimat.activity.msg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enuos.jimat.R;
import com.enuos.jimat.module.MsgListItem;

import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MsgListItem> msgList;
    private MyClickListener mListener;

    public MsgAdapter(Context context, ArrayList<MsgListItem> msgList) {
        this.mContext = context;
        this.msgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(msgList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void addDataList(ArrayList<MsgListItem> msgList) {
        this.msgList.addAll(msgList);
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

        ImageView item_msg_pic;
        TextView item_msg_content;
        TextView item_msg_time;

        ViewHolder(View itemView) {
            super(itemView);
            item_msg_pic = itemView.findViewById(R.id.item_msg_pic);
            item_msg_content = itemView.findViewById(R.id.item_msg_content);
            item_msg_time = itemView.findViewById(R.id.item_msg_time);

            // 整体的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.select(getLayoutPosition());
                }
            });
        }

        void bindData(MsgListItem msgListItem, int position) {
            if (msgListItem.MESSAGE_SOURCE.equals("1")) {
                Glide.with(mContext).load(R.drawable.msg_order).into(item_msg_pic);
            } else {
                Glide.with(mContext).load(R.drawable.msg_money).into(item_msg_pic);
            }
            item_msg_content.setText(msgListItem.MESSAGE_CONTENT);
            item_msg_time.setText(msgListItem.CREATE_TIME);
        }
    }

}
