package com.vedeng.widget.base.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**********************************************************
 * @文件名称：CommonDBHelper.java
 * @文件作者：聂中泽
 * @创建时间：2016/9/19 16:51
 * @文件描述：公共数据库助手类
 * @修改历史：2016/9/19 创建初始版本
 **********************************************************/
public abstract class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
    }

    /**
     * 关闭数据库
     */
    public void closeDatabase(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    protected abstract ArrayList<DBTable> initColumnList();

    protected void createAllTables(SQLiteDatabase db) {
        ArrayList<DBTable> columnList = initColumnList();
        DBTable module = null;
        for (int i = 0; i < columnList.size(); i++) {
            module = columnList.get(i);
            if (module != null) {
                createTable(db, module.tableName(), module.toColumns());
            }
        }
    }

    /**
     * @param sqliteDatabase
     * @param table          要创建的数据表名
     * @param columns        列名
     */
    protected void createTable(SQLiteDatabase sqliteDatabase, String table, String[] columns) {
        String createTable = "create table if not exists ";
        String primaryKey = " Integer primary key autoincrement";
        String text = " text";
        char leftBracket = '(';
        char rightBracket = ')';
        char comma = ',';
        int stringBufferSize = 170;
        StringBuffer sql = new StringBuffer(stringBufferSize);
        sql.append(createTable).append(table).append(leftBracket).append(DBTable.COLUMN_ID).append(primaryKey).append(comma);
        for (String column : columns) {
            sql.append(column);
            sql.append(comma);
        }
        sql.append(DBTable.COLUMN_TIME).append(text).append(rightBracket);
        sqliteDatabase.execSQL(sql.toString());
    }

    /**
     * 修改表
     *
     * @param db
     * @param table
     * @param columnName defaultType:TEXT
     */
    protected synchronized void alterTable(final SQLiteDatabase db, final String table, final String columnName) {
        SQLiteDatabase database = null;
        try {
            database = db == null ? getWritableDatabase() : db;
            database.execSQL("alter table " + table + " add " + columnName + " " + DBTable.TEXT_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * drop表
     *
     * @param table 需要drop的表名
     */
    protected synchronized void dropTable(final SQLiteDatabase db, final String table) {
        SQLiteDatabase database = null;
        try {
            database = db == null ? getWritableDatabase() : db;
            database.execSQL("drop table if exists " + table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入数据
     *
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    public synchronized long insert(final String table, final String nullColumnHack, final ContentValues values) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            return database.insert(table, nullColumnHack, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * 删除数据
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            return database.delete(table, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * 更新数据
     *
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int update(final String table, final ContentValues values, final String whereClause, final String[] whereArgs) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            return database.update(table, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeDatabase(database);
        }
    }
}
