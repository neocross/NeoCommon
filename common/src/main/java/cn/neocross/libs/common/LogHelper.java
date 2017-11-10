package cn.neocross.libs.common;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 日志工具类
 * <p>
 * 使用方法：
 * step 1 : 静态导入对应日志方法，比如 import static cn.neocross.common.LogHelper.LOGD;
 * step 2 : 记录日志 LOG("日志标签", "日志内容")
 * <p>
 * 注意：tag 长度应小于23个字符
 * <p>
 * Created by nodlee on 2016/4/27.
 */
public class LogHelper {

    public static void LOGD(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void LOGV(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void LOGI(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void LOGW(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void LOGE(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    /**
     * 将自定义日志保存到sd卡
     *
     * @param fileName 文件名
     * @param log      log
     */
    public static void saveCustomLog(String fileName, String log) {
        String path = Environment.getExternalStorageDirectory() + File.separator + fileName + ".txt";
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(log.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
