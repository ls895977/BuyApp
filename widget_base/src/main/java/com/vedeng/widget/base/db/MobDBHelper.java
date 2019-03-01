package com.vedeng.widget.base.db;

import android.database.sqlite.SQLiteDatabase;

import com.vedeng.widget.base.MicBusinessConfigHelper;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：MobDBHelper.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月3日 上午11:19:19
 * @文件描述：移动公共模块数据库助手
 * @修改历史：2016年2月3日创建初始版本
 **********************************************************/
public final class MobDBHelper extends DBHelper {
    private static final String DATABASE_NAME = "focus_mob.db";
    private static MobDBHelper helper = null;

    private MobDBHelper() {
        super(MicBusinessConfigHelper.getInstance().getContext(), DATABASE_NAME, null, 3);
    }

    public static MobDBHelper getInstance() {
        if (helper == null) {
            helper = new MobDBHelper();
        }
        return helper;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 3) {
            updateDBToVersion3(db, oldVersion);
        }
    }

    private void updateDBToVersion3(SQLiteDatabase db, int oldVersion) {
        db.beginTransaction();
        try {
            createAllTables(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    protected ArrayList<DBTable> initColumnList() {
        ArrayList<DBTable> list = new ArrayList<>();
        list.add(new DownloadDBTable());
        list.add(new EventDBTable());
        list.add(new ActionDBTable());
        return list;
    }
}
