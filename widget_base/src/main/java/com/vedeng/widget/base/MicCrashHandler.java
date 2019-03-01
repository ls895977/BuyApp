package com.vedeng.widget.base;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;

import com.vedeng.comm.base.utils.FileUtils;
import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.ToastUtils;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.widget.base.listener.CrashListener;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**********************************************************
 * @文件名称：MicCrashHandler.java
 * @文件作者：聂中泽
 * @创建时间：2015年6月11日 下午4:24:57
 * @文件描述：捕获全局未处理到的异常，上传服务器
 * @修改历史：2015年6月11日创建初始版本
 **********************************************************/
public class MicCrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "MicCrashHandler";
    /**
     * 系统默认的异常捕获类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 自定义异常处理类
     */
    private static MicCrashHandler instance;

    private Context mContext;
    // 存储文件夹路径
    private final String SDCARD_PATH = Environment.getExternalStorageDirectory() + "/focustech/mic/log/";
    private String FILE_PATH;
    private final String FILE_NAME = "mic_crash_exception.log";
    private CrashListener crashListener;

    private MicCrashHandler() {
        this.crashListener = MicBusinessConfigHelper.getInstance().getCrashHandlerListener();
        this.mContext = MicBusinessConfigHelper.getInstance().getContext();
        FILE_PATH = mContext.getFilesDir() + "/";
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    static {
        instance = new MicCrashHandler();
    }

    /**
     * 单例模式，获取自定义异常处理类
     */
    public static MicCrashHandler newInstance() {
        return instance;
    }

    /**
     * 自定义异常处理
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.e(TAG, "uncaughtException " + ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LogUtils.e(TAG, "error : ", e);
            } finally {
                if (crashListener != null) {
                    crashListener.onCrash();
                }
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (isInMainUIThread()) {
                    ToastUtils.toast(mContext, R.string.crash_exception);
                }
                Looper.loop();
            }
        }.start();

        // 保存日志文件
        saveCrashLog(ex);
        return true;
    }

    public static boolean isInMainUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private void saveCrashLog(Throwable ex) {
        /**
         * 组装异常信息
         */
        String exceptionMsg = assembleExceptionMsg(ex);
        FileUtils.saveContent(FILE_PATH, FILE_NAME, exceptionMsg);
    }

    private void saveOldCrashLogToNewFile() {
        String content = FileUtils.readFileContent(SDCARD_PATH, FILE_NAME);
        if (!Utils.isEmpty(content)) {
            FileUtils.saveContent(FILE_PATH, FILE_NAME, content);
            FileUtils.deleteFile(new File(SDCARD_PATH + FILE_NAME));
        }
    }

    /**
     * 组装异常信息，在这里拼装想要的格式
     */
    private String assembleExceptionMsg(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        return sb.toString();
    }

    /**
     * 从文件中获取崩溃日志内容
     *
     * @return
     */
    public String getCrashLogFromFile() {
        saveOldCrashLogToNewFile();
        return FileUtils.readFileContent(FILE_PATH, FILE_NAME);
    }

    /**
     * 删除崩溃日志文件
     */
    public void deleteCrashLogFile() {
        FileUtils.deleteFile(new File(FILE_PATH + FILE_NAME));
    }

}
