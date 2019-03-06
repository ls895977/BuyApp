package com.hyphenate.helpdesk.easeui.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;

/**
 */
public class UserUtil {

    public static void setAgentNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView) {
        AgentInfo agentInfo = MessageHelper.getAgentInfo(message);
        if (usernickView != null) {
            usernickView.setText(message.from());
            if (agentInfo != null) {
                if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                    usernickView.setText(agentInfo.getNickname());
                }
            }
        }
        if (userAvatarView != null) {
            //设置图片圆角角度
            RoundedCorners roundedCorners = new RoundedCorners(30);
            //通过RequestOptions扩展功能
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300)
                    //圆形
                    .circleCrop().placeholder(R.drawable.hd_default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(R.drawable.hd_default_avatar).apply(options).into(userAvatarView);
            if (agentInfo != null) {
                if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                    String strUrl = agentInfo.getAvatar();
                    // 设置客服头像
                    if (!TextUtils.isEmpty(strUrl)) {
                        if (!strUrl.startsWith("http")) {
                            strUrl = "http:" + strUrl;
                        }

                        //正常的string路径
                        //                        Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.hd_default_avatar).into(userAvatarView);
                        Glide.with(context).load(strUrl).apply(options).into(userAvatarView);
                    }
                }
            }

        }
    }

    public static void setCurrentUserNickAndAvatar(Context context, ImageView userAvatarView, TextView userNickView) {
        if (userAvatarView != null) {
            userAvatarView.setImageResource(R.drawable.hd_default_avatar);
        }
    }

}
