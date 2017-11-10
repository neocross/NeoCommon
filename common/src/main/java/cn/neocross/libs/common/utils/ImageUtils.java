package cn.neocross.libs.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 作者：nodlee/1516lee@gmail.com
 * 时间：2016年12月30日
 * 说明：
 */

public class ImageUtils {

    /**
     * 将字节流图像转为指定尺寸的bitmap
     *
     * @param data      字节流
     * @param reqWidth  目标宽
     * @param reqHeight 目标高
     * @return bitmap
     */
    public static Bitmap createBitmap(byte[] data, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * 将图片文件转为特定尺寸的图像
     *
     * @param filePath  图片路径
     * @param reqWidth  目标宽
     * @param reqHeight 目标高
     * @return 目标bitmap
     */
    public static Bitmap createBitmap2(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 将图片文件转为特定尺寸的图像
     *
     * @param fileName  图片路径
     * @param reqWidth  目标宽
     * @param reqHeight 目标高
     * @return 目标bitmap
     */
    public static Bitmap createBitmap(String fileName, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(fileName))
            return null;
        File file = new File(fileName);
        if (!file.exists())
            return null;
        try {
            Bitmap sourceBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, reqWidth, reqHeight, true);
            sourceBitmap.recycle();
            return scaledBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算到目标尺寸的缩放比例大小
     *
     * @param options   BitmapFactory.Options
     * @param reqWidth  目标宽
     * @param reqHeight 目标高
     * @return 大小
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 保存图片到手机存储
     *
     * @param context              上下文
     * @param bitmap               bitmap对象
     * @param title                文件名
     * @param shouldRefreshGallery 是否刷新图库
     * @return 返回保存成功后的绝对路径
     * @throws Exception e
     */
    public static String saveBitmapToSDCard(Context context, Bitmap bitmap, String title, boolean shouldRefreshGallery) throws Exception {
        File dir = context.getExternalCacheDir();
        File file = new File(dir, title + ".jpg");
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        if (bitmap == null) throw new Exception("bitmap is null");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        if (shouldRefreshGallery) {
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存字节流图像
     *
     * @param path 目标路径全名 如 ab/123.jpg
     * @param data 字节流
     * @return 文件路径
     */
    public static String saveBytePic(String path, byte[] data) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String storage = Environment.getExternalStorageDirectory().toString();
                StatFs fs = new StatFs(storage);
                long available = fs.getAvailableBlocks() * fs.getBlockSize();
                if (available < data.length) {
                    return null;
                }
                File file = new File(path);
                if (!file.exists())
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    /**
     * 翻转图片
     * 左右翻转 传递值为（bitmap,-1,1）上下翻转传递值为 ( bitmap,1,-1)
     *
     * @param srcBitmap bitmap
     * @param sx        x
     * @param sy        y
     * @return bitmap
     */
    public static Bitmap reversalBitmap(Bitmap srcBitmap, float sx, float sy) {
        Bitmap cacheBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int w = cacheBitmap.getWidth();
        int h = cacheBitmap.getHeight();
        Canvas canvas = new Canvas(cacheBitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, matrix, true);
        canvas.drawBitmap(bitmap, new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()), new Rect(0, 0, w, h), null);
        return bitmap;
    }

    /**
     * 从文件读取drawable
     *
     * @param path 文件路径
     * @return drawable
     */
    public static Drawable loadDrawableFromLocal(String path) {
        if (path != null && new File(path).exists()) {
            return Drawable.createFromPath(path);
        } else {
            return null;
        }
    }
}
