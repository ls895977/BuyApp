package com.vedeng.widget.base.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.vedeng.comm.base.utils.FileUtils;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.constant.MobConstants;
import com.vedeng.widget.base.module.ActionData;
import com.vedeng.widget.base.module.CollectBehaviorType;
import com.vedeng.widget.base.module.Download;
import com.vedeng.widget.base.module.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**********************************************************
 * @文件名称：MobDBManager.java
 * @文件作者：聂中泽
 * @创建时间：2014-2-10 下午02:41:51
 * @文件描述：数据助手类
 * @修改历史：2014-2-10创建初始版本
 **********************************************************/
public final class MobDBManager extends DBDataHelper {
    private static MobDBManager dataHelper = null;
    private static MobDBHelper dbHelper = null;

    static {
        dbHelper = MobDBHelper.getInstance();
        dataHelper = new MobDBManager(dbHelper);
    }

    private MobDBManager(DBHelper dbHelper) {
        super(dbHelper);
    }

    public static MobDBManager getInstance() {
        return dataHelper;
    }

    /**
     * 将event保存到数据库中
     *
     * @param event
     */
    public void saveEvent(Event event) {
        synchronized (dbHelper) {
            ContentValues values = new ContentValues();
            values.put(EventDBTable.EVENT_NAME, event.eventName);
            values.put(EventDBTable.COLUMN_TIME, event.eventTime);
            values.put(EventDBTable.EVENT_TYPE, event.eventType.toString());
            values.put(EventDBTable.EVENT_PARAMS, event.params);
            dbHelper.insert(EventDBTable.TABLE_NAME, null, values);
        }
    }

    /***
     * 删除数据库中的event
     */
    public int deleteEvent(Event event) {
        synchronized (dbHelper) {
            return dbHelper.delete(EventDBTable.TABLE_NAME, "id=?", new String[]
                    {event.id});
        }
    }

    /***
     * 删除数据库中的event
     */
    public void deleteEvent(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        if (events.size() == 1) {
            deleteEvent(events.get(0));
            return;
        }
        String[] whereArgs = new String[events.size()];
        String[] placeHolders = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            placeHolders[i] = "?";
            whereArgs[i] = events.get(i).id;
        }
        delete(EventDBTable.TABLE_NAME, EventDBTable.COLUMN_ID + " IN ( " + TextUtils.join(",", placeHolders) + " )", whereArgs);
    }

    /***
     * 获得event 数据
     *
     * @return
     */
    public ArrayList<Event> selectEvents() {
        synchronized (dbHelper) {
            ArrayList<Event> events = new ArrayList<Event>();
            Cursor cursor = null;
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getReadableDatabase();
                cursor = db.query(EventDBTable.TABLE_NAME, new String[]
                                {EventDBTable.COLUMN_ID, EventDBTable.EVENT_NAME, EventDBTable.EVENT_TYPE, EventDBTable.EVENT_PARAMS,
                                        EventDBTable.COLUMN_TIME}, null, null, null, null, DBTable.COLUMN_ID + " desc",
                        String.valueOf(MicBusinessConfigHelper.getInstance().getAnalyticsUploadCountLimit() > 0 ?
                                MicBusinessConfigHelper.getInstance().getAnalyticsUploadCountLimit() : MobConstants.ANALYTICS_UPLOAD_COUNT_LIMIT));
                while (cursor.moveToNext()) {
                    Event event = new Event();
                    event.id = cursor.getString(cursor.getColumnIndex(EventDBTable.COLUMN_ID));
                    event.eventType = CollectBehaviorType.getValueByTag(cursor.getString(cursor
                            .getColumnIndex(EventDBTable.EVENT_TYPE)));
                    event.eventName = cursor.getString(cursor.getColumnIndex(EventDBTable.EVENT_NAME));
                    event.eventTime = cursor.getString(cursor.getColumnIndex(EventDBTable.COLUMN_TIME));
                    event.params = cursor.getString(cursor.getColumnIndex(EventDBTable.EVENT_PARAMS));
                    events.add(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                dbHelper.closeDatabase(db);
            }
            return events;
        }
    }

    public void saveDownload(String url, String localFilePath, int contentLength) {
        if (!isDownloaded(url)) {
            if (FileUtils.isFileExists(localFilePath)) {
                FileUtils.deleteFile(new File(localFilePath));
            }
            synchronized (dbHelper) {
                ContentValues values = new ContentValues();
                values.put(DownloadDBTable.URL, url);
                values.put(DownloadDBTable.COMPLETE_SIZE, 0);
                values.put(DownloadDBTable.START_POS, 0);
                values.put(DownloadDBTable.CONTENT_LENGTH, contentLength);
                values.put(DownloadDBTable.LOCAL_FILE_PATH, localFilePath);
                values.put(DownloadDBTable.COLUMN_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
                dbHelper.insert(DownloadDBTable.TABLE_NAME, null, values);
            }
        } else {
            // 如果已存在下载记录，但是文件已丢失，则重置下载记录
            if (!FileUtils.isFileExists(localFilePath)) {
                updateDownloadProgress(url, 0, 0);
            }
        }
    }

    public void updateDownloadProgress(String url, int startPos, int completeSize) {
        synchronized (dbHelper) {
            ContentValues values = new ContentValues();
            values.put(DownloadDBTable.COMPLETE_SIZE, completeSize);
            values.put(DownloadDBTable.START_POS, startPos);
            dbHelper.update(DownloadDBTable.TABLE_NAME, values, DownloadDBTable.URL + "=?", new String[]
                    {url});
        }
    }

    public Download selectDownload(String url) {
        synchronized (dbHelper) {
            Download download = null;
            Cursor cursor = null;
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getReadableDatabase();
                cursor = db.query(DownloadDBTable.TABLE_NAME, null, DownloadDBTable.URL + "=?", new String[]
                        {url}, null, null, null);
                while (cursor.moveToNext()) {
                    download = new Download();
                    download.id = cursor.getString(cursor.getColumnIndex(DownloadDBTable.COLUMN_ID));
                    download.url = cursor.getString(cursor.getColumnIndex(DownloadDBTable.URL));
                    download.completeSize = cursor.getInt(cursor.getColumnIndex(DownloadDBTable.COMPLETE_SIZE));
                    download.startPos = cursor.getInt(cursor.getColumnIndex(DownloadDBTable.START_POS));
                    download.contentLength = cursor.getInt(cursor.getColumnIndex(DownloadDBTable.CONTENT_LENGTH));
                    download.localFilePath = cursor.getString(cursor
                            .getColumnIndex(DownloadDBTable.LOCAL_FILE_PATH));
                    download.time = cursor.getString(cursor.getColumnIndex(DownloadDBTable.COLUMN_TIME));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                dbHelper.closeDatabase(db);
            }
            return download;
        }
    }

    public int deleteDownload(String url) {
        synchronized (dbHelper) {
            return dbHelper.delete(DownloadDBTable.TABLE_NAME, DownloadDBTable.URL + "=?", new String[]
                    {url});
        }
    }

    public boolean isDownloaded(String url) {
        return selectDownload(url) != null;
    }

    public void saveAction(ActionData action) {
        if (action == null) {
            return;
        }
        insert(ActionDBTable.TABLE_NAME, action);
    }

    /***
     * 删除数据库中的event
     */

    public void deleteActionList(ArrayList<ActionData> dateList) {
        if (dateList == null || dateList.isEmpty()) {
            return;
        }
        if (dateList.size() == 1) {
            delete(ActionDBTable.TABLE_NAME, dateList.get(0));
            return;
        }
        String[] whereArgs = new String[dateList.size()];
        String[] placeHolders = new String[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            placeHolders[i] = "?";
            whereArgs[i] = dateList.get(i).id;
        }
        delete(ActionDBTable.TABLE_NAME, ActionDBTable.COLUMN_ID + " IN ( " + TextUtils.join(",", placeHolders) + " )", whereArgs);
    }

    public ArrayList<DBData> getActionList() {
        return select(ActionDBTable.TABLE_NAME, null, null, null, DBTable.COLUMN_ID + " desc",
                String.valueOf(MicBusinessConfigHelper.getInstance().getAnalyticsUploadCountLimit() > 0 ?
                        MicBusinessConfigHelper.getInstance().getAnalyticsUploadCountLimit() : MobConstants.ANALYTICS_UPLOAD_COUNT_LIMIT),
                ActionData.class);
    }
}
