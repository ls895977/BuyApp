package com.enuos.jimat.model;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by nzz on 2018/6/25.
 * 全局事件监听
 */

public class EventListener {
    private Context mContext;
    private final LocalBroadcastManager LBM;
    public EventListener(Context context) {
        mContext = context;
        //创建本地广播管理者
        LBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
//        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //注册一个群信息变化的监听
//        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangedListener);
    }
    // 群信息变化的监听
    /*private final EMGroupChangeListener emGroupChangedListener = new EMGroupChangeListener() {

        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            LBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

        }
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            LBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            LBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            LBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };*/

    /*private final EMContactListener emContactListener = new EMContactListener() {
        @Override
        public void onContactAdded(String hxid) {
            //数据更新
            Model.getInstance().getDBManager().getContactTableDao().saveContact(new UserInfo(hxid), true);
            //发送联系人变化的广播
            LBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        @Override
        public void onContactDeleted(String hxid) {

            Model.getInstance().getDBManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDBManager().getInviteTableDao().removeInvitation(hxid);
            //发送广播
            LBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        @Override
        public void onContactInvited(String hxid, String reason) {
            //更新数据库
            InvationInfo invationInfo=new InvationInfo();
            invationInfo.setUser(new UserInfo(hxid));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请

            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invationInfo);
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            LBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

    };*/
}
