package com.enuos.jimat.utils

import com.enuos.jimat.utils.toast.LogUtils

/**********************************************************
 * @文件作者：聂中泽
 * @创建时间：2019/1/22 19:43
 * @文件描述：
 * @修改历史：2019/1/22 创建初始版本
 **********************************************************/
object ClickUtils {
    private var lastClickTime: Long = 0
    private const val DIFF: Long = 800
    private var lastButtonId = -1


    fun isFastDoubleClick(): Boolean {
        return isFastDoubleClick(-1, DIFF)
    }


    fun isFastDoubleClick(buttonId: Int): Boolean {
        return isFastDoubleClick(buttonId, DIFF)
    }

    private fun isFastDoubleClick(buttonId: Int, diff: Long): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            LogUtils.v("isFastDoubleClick", "短时间内按钮多次触发")
            return true
        }
        lastClickTime = time
        lastButtonId = buttonId
        return false
    }
}