package com.enuos.jimat.activity.address;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enuos.jimat.R;
import com.enuos.jimat.module.AddressListItem;
import com.enuos.jimat.utils.MyUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/17 9:46
 * @文件描述：
 * @修改历史： 2018/12/17 创建初始版本
 **********************************************************/
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AddressListItem> addressList;
    private MyClickListener mListener;

    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };

    public AddressAdapter(Context context, ArrayList<AddressListItem> addressList) {
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
            if (!MyUtils.isSpecialChar(addressList.TAKE_NAME)) { // 不是特殊符号
                String oneStr = addressList.TAKE_NAME.substring(0, 1);
                if (!noContainsEmoji(oneStr)) { // 不含表情
                    if (MyUtils.isChinese(oneStr)) { // 中文
                        item_address_flag.setText("    " + getSpells(oneStr) + "    ");
                    } else {
                        item_address_flag.setText("    " + oneStr + "    ");
                    }
                }
            }
        }
    }

    private boolean noContainsEmoji(String str) { //真为不含有表情
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {

            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {

        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }

}
