package com.enuos.jimat.inter;

/**********************************************************
 * @文件作者： 聂中泽
 * @创建时间： 2018/12/2 14:11
 * @文件描述：
 * @修改历史： 2018/12/2 创建初始版本
 **********************************************************/

import android.content.Intent;

/**
 * 解决子Activity无法接收Activity回调的问题
 *
 */
public interface OnTabActivityResultListener {

    void onTabActivityResult(int requestCode, int resultCode, Intent data);

}
