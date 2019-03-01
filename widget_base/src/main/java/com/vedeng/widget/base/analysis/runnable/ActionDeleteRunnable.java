package com.vedeng.widget.base.analysis.runnable;

import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.analysis.AnalyticsTracker;
import com.vedeng.widget.base.module.ActionData;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2017/1/9 14:32
 * @文件描述：接口信息数据删除线程
 * @修改历史：2017/1/9 创建初始版本
 **********************************************************/

public class ActionDeleteRunnable implements Runnable {

    private static final String TAG = "ActionCollect";
    private ArrayList<ActionData> actionList;

    public ActionDeleteRunnable(ArrayList<ActionData> actionList) {
        this.actionList = actionList;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!Utils.isEmpty(actionList)) {
                AnalyticsTracker.getInstances().deleteActionList(actionList);
            }
        }
    }
}
