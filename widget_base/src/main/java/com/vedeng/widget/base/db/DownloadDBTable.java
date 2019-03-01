package com.vedeng.widget.base.db;

/**********************************************************
 * @文件名称：DownloadDBTable.java
 * @文件作者：聂中泽
 * @创建时间：2016/9/19 16:54
 * @文件描述：下载数据库表模型
 * @修改历史：2016/9/19 创建初始版本
 **********************************************************/
public class DownloadDBTable extends DBTable {
    public static final String TABLE_NAME = "download";
    public static final String URL = "download_url";
    public static final String COMPLETE_SIZE = "download_completesize";
    public static final String START_POS = "download_start_pos";
    public static final String CONTENT_LENGTH = "download_contentlength";
    public static final String LOCAL_FILE_PATH = "download_localfilepath";

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] toColumns() {
        return new String[]{URL + TEXT_TYPE, COMPLETE_SIZE + INTEGER_TYPE, START_POS + INTEGER_TYPE,
                CONTENT_LENGTH + INTEGER_TYPE, LOCAL_FILE_PATH + TEXT_TYPE};
    }
}
