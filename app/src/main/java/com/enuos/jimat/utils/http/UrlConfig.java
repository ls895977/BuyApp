package com.enuos.jimat.utils.http;

/**
 * Created by nzz on 2017/9/18.
 * 接口地址常量类
 */
public class UrlConfig {

    // base url
    public static final String base_url = "http://47.254.192.108:8080/jimatInterface/";

    // 1 获取短信验证码
    public static final String sms_url = "login/getSmsCode.do?";

    // 2 注册
    public static final String register_url = "login/saveRegister.do?";

    // 3 登录--账号密码
    public static final String login_psw_url = "login/getAccountsLogin.do?";

    // 4 登录--短信验证码
    public static final String login_sms_url = "login/getMobileLogin.do?";

    // 5 修改账号密码--手机验证码
    public static final String modify_psw_phone_url = "login/savePasswordByMobile.do?";

    // 6 获取邮箱验证码
    public static final String email_url = "login/getEmailCode.do?";

    // 7 修改账号密码--邮箱验证码
    public static final String modify_psw_email_url = "login/savePasswordByEmail.do?";

    // 8 获取在售产品列表
    public static final String goods_list_url = "goods/getGoodsList.do?";

    // 9 获取产品详情
    public static final String goods_details_url = "goods/getGoodsInfo.do?";

    // 10 获取产品价格变动列表
    public static final String goods_price_url = "goods/getGoodsPriceList.do?";

    // 11 获取产品购买记录列表
    public static final String goods_sales_record_url = "goods/getGoodsBuyList.do?";

    // 12 获取某产品设置提醒价格
    public static final String goods_remind_url = "goods/getGoodsRemind.do?";

    // 13 新增/修改某产品设置提醒价格
    public static final String goods_remind_update_url = "goods/updateGoodsRemind.do?";

    // 14 获取收货地址列表
    public static final String address_url = "memberAddress/getMemberAddressList.do?";

    // 15 获取收货地址详情
    public static final String address_details_url = "memberAddress/getMemberAddressInfo.do?";

    // 16 新增收货地址
    public static final String address_add_url = "memberAddress/saveMemberAddress.do?";

    // 17 修改收货地址
    public static final String address_update_url = "memberAddress/updateMemberAddress.do?";

    // 18 设置默认收货地址
    public static final String address_default_url = "memberAddress/getMemberAddressIsDefault.do?";

    // 19 删除收货地址
    public static final String address_delete_url = "memberAddress/deleteMemberAddress.do?";

    // 20 新增订单
    public static final String order_add_url = "order/saveOrder.do?";

    // 21 获取订单列表
    public static final String order_list_url = "order/getMemberOrderList.do?";

    // 22 获取订单详情
    public static final String order_details_url = "order/getMemberOrderInfo.do?";

    // 23 获取消息列表
    public static final String message_url = "memberMessage/getMemberMessageList.do?";

    // 24 获取消息详情
    public static final String message_details_url = "memberMessage/getMemberMessageInfo.do?";

    // 25 获取余额
    public static final String balance_url = "member/getMemberBalance.do?";

    // 26 获取余额明细
    public static final String balance_details_url = "member/getMemberBalanceList.do?";

    // 27 获取用户个人信息
    public static final String user_info_url = "member/getMember.do?";

    // 28 修改用户个人信息
    public static final String user_info_update_url = "member/updateMember.do?";

    // 29 新增上传汇款凭证
    public static final String upload_pay_add_url = "member/saveUploadDocuments.do?";

    // 30 获取上传汇款凭证列表
    public static final String upload_pay_url = "/member/getUploadDocumentsList.do?";

    // 31 获取上传汇款凭证详情
    public static final String upload_pay_details_url = "member/getUploadDocumentsInfo.do?";

    // 32 获取三级联动省市区
    public static final String get_address_city_url = "area/getAllArea.do?";

    // 33 上传图片
    public static final String upload_png_url = "servlet/UploadServelt";

    // 34 获取历史产品列表
    public static final String goods_history_url = "goods/getGoodsHistoryList.do?";

    // 35 支付
    public static final String goods_pay_url = "order/updateOrder.do?";

    // 36 新增收货地址
    public static final String edit_headimage_url = "member/updateMemberPhoto.do?";

    // 37 新增收货地址
    public static final String edit_name_url = "member/updateMemberName.do?";

    // 38 新首页
    public static final String home_new_url = "index/init.do?";

    // 39 银行卡支付 head
    public static final String bank_pay_head_url = "https://sandbox.molpay.com/MOLPay/pay/SB_10kb/index.php?";

    // 40 银行卡支付 tail
    public static final String bank_pay_tail_url = "&cancelurl=http://47.254.192.108:8080/jimatInterface/molpay/cancelurl.do&callbackurl=http://47.254.192.108:8080/jimatInterface/molpay/notifyUrl.do";

    // 41 二次支付
    public static final String goods_pay_two_url = "order/updateOrderTwo.do?";

}
