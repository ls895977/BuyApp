package com.vedeng.comm.base.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**********************************************************
 * @文件名称：FileUtils.java
 * @文件作者：聂中泽
 * @创建时间：2016年2月4日 上午11:21:46
 * @文件描述：文件工具类
 * @修改历史：2016年2月4日创建初始版本
 **********************************************************/
public class FileUtils {
    private static final String TAG = "====FileUtils====";

    private FileUtils() {

    }

    /**
     * 保存内容到文件中
     *
     * @param filePath
     * @param fileName
     * @param content
     */
    public static void saveContent(String filePath, String fileName, String content) {
        RandomAccessFile fos = null;
        try {
            File dirFile = new File(filePath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            File file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new RandomAccessFile(file, "rw");
            fos.seek(file.length());
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存内容到文件中
     *
     * @param filePath
     * @param fileName
     * @param data
     */
    public static String saveByBytes(File filePath, String fileName, final byte[] data) {
        File file = new File(filePath, fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (IOException e) {
            LogUtils.w(TAG, "Cannot write to " + file + e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 读取文件中的内容
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static String readFileContent(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        if (!file.exists())
            return "";
        String content = ""; // 文件内容字符串
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String line;
        try {
            is = new FileInputStream(file);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            // 分行读取
            while ((line = br.readLine()) != null) {
                content += line + "\n";
            }
        } catch (FileNotFoundException e) {
            LogUtils.e(TAG, "ReadFile failed, The File doesn't not exist.");
        } catch (IOException e) {
            LogUtils.e(TAG, e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            LogUtils.e(TAG, "DeleteFile failed, The File doesn't not exist.");
        }
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        if (Utils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
