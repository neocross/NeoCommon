package cn.neocross.libs.common.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static cn.neocross.libs.common.LogHelper.LOGD;
import static cn.neocross.libs.common.LogHelper.LOGE;

/**
 * Created by shenhua on 1/17/2017.
 * Email shenhuanet@126.com
 */
public class FileUtils {

    public static FileUtils getInstance() {
        return new FileUtils();
    }

    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private FileOperateCallback callback;
    private volatile boolean isSuccess;
    private String errorStr;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback.onSuccess();
                }
                if (msg.what == FAILED) {
                    callback.onFailed(msg.obj.toString());
                }
            }
        }
    };

    /**
     * 读取文件
     *
     * @param file file
     * @return 文件内容
     */
    public static String readFile(File file) {
        String content = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            content = "";
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                content += line;
            }
            inputStream.close();
        } catch (Exception e) {
            Log.e("alex", e.toString());
        }
        return content;
    }

    /**
     * 复制assets文件到sd卡
     *
     * @param context context
     * @param srcPath assets下文件夹
     * @param sdPath  sd卡文件夹
     * @return this
     */
    public FileUtils copyAssetsToSD(final Context context, final String srcPath, final String sdPath) {
        return this.copyAssetsToSD(context, srcPath, sdPath, true);
    }

    /**
     * 复制assets文件到sd卡
     *
     * @param context    context
     * @param srcPath    assets下文件夹
     * @param sdPath     sd卡文件夹
     * @param forceWrite 是否覆盖sd卡上文件 默认为覆盖
     * @return this
     */
    public FileUtils copyAssetsToSD(final Context context, final String srcPath, final String sdPath, final boolean forceWrite) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToDst(context, srcPath, sdPath, forceWrite);
                if (isSuccess)
                    handler.obtainMessage(SUCCESS).sendToTarget();
                else
                    handler.obtainMessage(FAILED, errorStr).sendToTarget();
            }
        }).start();
        return this;
    }

    /**
     * 设置文件操作回调,文件操作完成后回调
     *
     * @param callback c
     */
    public void setFileOperateCallback(FileOperateCallback callback) {
        this.callback = callback;
    }

    /**
     * 获取sd卡某文件夹下某种类型的文件
     *
     * @param sdDir 文件夹名
     * @param type  文件类型 如 jpg
     * @return 文件数组
     */
    public File[] getSDDirFiles(String sdDir, String type) {
        File filesPath = new File(Environment.getExternalStorageDirectory(), sdDir);
        if (!filesPath.isDirectory()) return null;
        FileNameFilter fileNameFilter = new FileNameFilter();
        fileNameFilter.addType(type);
        File[] files = filesPath.listFiles(fileNameFilter);
        if (files.length == 0) return null;
        return files;
    }

    /**
     * 文件复制监听接口
     */
    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

    /**
     * 判断SD卡(外部存储设备)是否准备就绪
     *
     * @return true 准备好
     */
    public static boolean isSdcardReady() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断文件是否存在
     *
     * @param strFile 文件
     * @return true 存在 false 不存在
     */
    public static boolean isFileExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取SD卡路径
     *
     * @return 路径
     */
    public String getSdcardPath() {
        return Environment.getExternalStorageDirectory().toString() + File.separator;
    }

    /**
     * 获取内置缓存路径
     *
     * @param context context
     * @return 路径
     */
    public static String getInnerCachePath(Context context) {
        File cacheDir = context.getCacheDir();
        return cacheDir.getPath() + File.separator;
    }

    /**
     * 获取程序外部的缓存目录
     *
     * @param context context
     * @return dir
     */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 获取可以使用的缓存目录,较安全
     *
     * @param context context
     * @return file
     */
    public static File getDiskCacheDir(Context context) {
        final String cachePath = isSdcardReady() ? getExternalCacheDir(context).getPath() : getInnerCachePath(context);
        File cacheDirFile = new File(cachePath);
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdirs();
        }
        return cacheDirFile;
    }

    /**
     * 创建app主目录
     *
     * @param context context
     * @return boolean
     */
    public static boolean checkFileDirectory(Context context) {
        final File resDir = getDiskCacheDir(context);
        return resDir.exists() || resDir.mkdirs();
    }

    /**
     * 根据后缀名删除文件
     *
     * @param delPath    path of file
     * @param delEndName end name of file
     * @return boolean the result
     */
    public static boolean deleteEndFile(String delPath, String delEndName) {
        // param is null
        if (delPath == null || delEndName == null) {
            return false;
        }
        try {
            final File file = new File(delPath);
            if (file.isDirectory()) {
                String[] fileList = file.list();
                File delFile;
                final int size = fileList.length;// 用于进度
                for (String aFileList : fileList) {
                    delFile = new File(delPath + "/" + aFileList);
                    if (delFile.isFile()) {// 删除该文件夹下所有文件以delEndName为后缀的文件（不包含子文件夹里的文件）
                        // 删除该文件夹下所有文件以delEndName为后缀的文件（包含子文件夹里的文件）
                        deleteEndFile(delFile.toString(), delEndName);
                    }
                }
            } else if (file.isFile()) {
                // check the end name
                if (file.toString().contains(".")
                        && file.toString()
                        .substring(
                                (file.toString().lastIndexOf(".") + 1))
                        .equals(delEndName)) {
                    // file delete
                    file.delete();
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 创建一个文件
     *
     * @param file 文件
     */
    public void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取路径中的文件名
     *
     * @param pathAndName apks/app.apk
     * @return app
     */
    public static String getFileName(String pathAndName) {
        int start = pathAndName.lastIndexOf("/");
        int end = pathAndName.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathAndName.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 文件名过滤器
     */
    public class FileNameFilter implements FilenameFilter {

        List<String> types;

        // 构造一个FileNameFilter对象，其指定文件类型为空。
        protected FileNameFilter() {
            types = new ArrayList<>();
        }

        /**
         * 构造一个FileNameFilter对象，具有指定的文件类型。
         *
         * @param types ".apk"
         */
        protected FileNameFilter(List<String> types) {
            super();
            this.types = types;
        }

        @Override
        public boolean accept(File dir, String filename) {
            for (String type : types) {
                if (filename.endsWith(type)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 添加指定类型的文件。
         *
         * @param type 将添加的文件类型，如".mp3"。
         */
        public void addType(String type) {
            types.add(type);
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * 读取Assets目录下面指定文件并返回String数据
     *
     * @param context  context
     * @param fileName fileName
     * @return string
     */
    public static String getJsonDataFromAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + fileName);
        try {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer, "utf-8");
            stringBuilder = stringBuilder.append(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    // ------- private

    private void copyAssetsToDst(Context context, String srcPath, String dstPath, boolean forceWrite) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(Environment.getExternalStorageDirectory(), dstPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName, forceWrite);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName, forceWrite);
                    }
                }
            } else {
                if (forceWrite) {
                    File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                    InputStream is = context.getAssets().open(srcPath);
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            errorStr = e.getMessage();
            isSuccess = false;
        }
    }

    /**
     * 拷贝文件夹中内容，子文件夹除外
     *
     * @param sourceDir 源文件
     * @param destDir   目标文件
     */
    public static void copyDir(File sourceDir, File destDir) {
        if (sourceDir == null || !sourceDir.exists()) {
            LOGE("xxx", "拷贝失败，源文件不存在");
            return;
        }
        File[] files = sourceDir.listFiles();
        for (File file : files) {
            if (file.isFile())
                copyFile(file, destDir);
        }
    }

    /**
     * 拷贝文件到指定目录
     *
     * @param inputStream 源文件流数据
     * @param fileName    文件名称
     * @param destDir     目标文件
     */
    public static void copyFile(InputStream inputStream, String fileName, File destDir) {
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        ensureDirExist(destDir);
        File destFile = new File(destDir, fileName);
        if (isExist(destFile)) {
            LOGD("xxx", "文件" + destFile + "已存在，跳过拷贝");
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int byteRoad = 0;
            while ((byteRoad = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, byteRoad);
            }
            fos.flush();
            LOGD("xxx", "拷贝文件：" + fileName + "-->" + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拷贝文件到指定目录
     *
     * @param file    file
     * @param destDir 目标文件
     */
    public static void copyFile(File file, File destDir) {
        ensureDirExist(destDir);
        String fileName = file.getName();
        File destFile = new File(destDir.getAbsolutePath() + File.separator + fileName);
        if (isExist(destFile)) {
            LOGD("xxx", "文件" + destFile + "已存在，跳过拷贝");
            return;
        }

        FileWriter writer = null;
        FileReader reader = null;
        try {
            writer = new FileWriter(destFile, false);
            reader = new FileReader(file);
            char[] buffer = new char[1024];
            while (reader.read(buffer) > 0) {
                writer.write(buffer);
            }
            writer.flush();

            LOGD("xxx", "拷贝文件：" + file.getAbsolutePath() + "-->" + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件是否存在
     *
     * @param file 文件
     * @return true 存在
     */
    public static boolean isExist(File file) {
        return file != null && file.exists() && file.length() > 0;
    }

    /**
     * 确保目录存在
     *
     * @param dir 目录
     */
    public static void ensureDirExist(File dir) {
        if (dir != null && !dir.exists())
            dir.mkdirs();
    }
}
