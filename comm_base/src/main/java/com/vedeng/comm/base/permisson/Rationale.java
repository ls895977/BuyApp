package com.vedeng.comm.base.permisson;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2016/12/7 17:00
 * @文件描述：用于显示提示的接口
 * @修改历史：2016/12/7 创建初始版本
 **********************************************************/

public interface Rationale {
    /**
     * Cancel request permission.
     */
    void cancel();

    /**
     * Go request permission.
     */
    void resume();
}
