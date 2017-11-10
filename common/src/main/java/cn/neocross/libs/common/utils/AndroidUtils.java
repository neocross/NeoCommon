package cn.neocross.libs.common.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.neocross.libs.common.LogHelper;

/**
 * Android工具类,静态方法供外部调用
 * <p>
 * Created by nodlee on 2016/4/27.
 * Modify by shenhua  Email:shenhuanet@126.com
 */
public class AndroidUtils {

    // 数组分隔符
    private static final String SEPARATOR = ",";

    /**
     * 吐司
     *
     * @param ctx     context
     * @param message message
     */
    public static void showToast(Context ctx, String message) {
        if (ctx != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 吐司
     *
     * @param ctx      context
     * @param msgResId stringId
     */
    public static void showToast(Context ctx, int msgResId) {
        if (ctx != null && msgResId != 0) {
            Toast.makeText(ctx, msgResId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否同一天
     *
     * @param date1 Data1
     * @param date2 Data2
     * @return true 是同一天 false 不同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * 判断是否同一天
     *
     * @param cal1 Calendar1
     * @param cal2 Calendar2
     * @return true 是同一天 false 不同一天
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 判断是否同一分钟
     *
     * @param date1 Data1
     * @param date2 Data2
     * @return true 是同一分钟 false 不同一分钟
     */
    public static boolean isSameMinute(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMinute(cal1, cal2);
    }

    /**
     * 判断是否同一分钟
     *
     * @param cal1 Calendar1
     * @param cal2 Calendar2
     * @return true 是同一分钟 false 同一分钟
     */
    private static boolean isSameMinute(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return isSameDay(cal1, cal2) &&
                cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) &&
                cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

    /**
     * 获取格式化后的时间
     *
     * @param formatter 自定义格式
     * @return string
     */
    private static String getDate(String formatter) {
        SimpleDateFormat format = new SimpleDateFormat(formatter, Locale.CHINA);
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context context
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 用于从相册选择图片，过滤数据拿到图片本地地址
     *
     * @param context context
     * @param data    intent
     * @return path
     */
    public static String filterPhotoPath(Context context, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String selectedImagePath = null;
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedImagePath = cursor.getString(columnIndex);
            cursor.close();
            LogHelper.LOGD("xxx", "selected image:" + selectedImagePath);
        }
        return selectedImagePath;
    }

    /**
     * 获取字符串数组中相应字符的位置
     *
     * @param arr       字符串数组
     * @param destValue 目标字符串
     * @return 位置,-1 则未找到
     */
    public static int getIndexByValue(String[] arr, String destValue) {
        int position = -1;
        for (int i = 0; i < arr.length; i++) {
            if (destValue.equals(arr[i])) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 判断字符是否非空
     *
     * @param value 字符
     * @return true 非空 false 空
     */
    public static boolean isNotNull(String value) {
        return !TextUtils.isEmpty(value);
    }

    /**
     * 判断字符是否空
     *
     * @param value 字符
     * @return true 空 false 非空
     */
    public static boolean isNull(String value) {
        return TextUtils.isEmpty(value);
    }

    /**
     * 检测对象是否为空
     *
     * @param reference 对象
     * @param <T>       未检测
     * @return 对象本身 抛出空指针
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * 获取app版本名
     *
     * @param context 上下文
     * @return 版本名
     */
    public static String getAppVersion(Context context) {
        String versionInfo = "<未知版本>";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (info != null) {
                versionInfo = info.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionInfo;
    }

    /**
     * 将字符数组转为字符串,中间以","分隔
     *
     * @param array 字符数组
     * @return 字符串
     */
    public static String array2String(String[] array) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) {
                result += array[i] + SEPARATOR;
            } else {
                result += array[i];
            }
        }
        return result;
    }

    /**
     * 将浮点数组转为字符串,中间以","分隔
     *
     * @param array 浮点数组
     * @return 浮点字符串
     */
    public static String arrayToString(float[] array) {
        if (array == null || array.length == 0)
            return null;
        String result = "";
        for (float data : array) {
            result += data + SEPARATOR;
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * 将以","分隔的浮点字符串转为浮点数组
     *
     * @param string 字符串
     * @return 浮点数组
     */
    public static float[] stringToFloatArray(String string) {
        if (TextUtils.isEmpty(string))
            return null;

        String[] source = string.split(SEPARATOR);
        float[] result = new float[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = Float.parseFloat(source[i]);
        }
        Log.d("xxx", "stringToArray:" + Arrays.toString(result));
        return result;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    /**
     * 获取textView中emoji文本
     *
     * @param context 上下文
     * @param tv      textView
     * @param source  SpannableStringBuilder
     * @return SpannableStringBuilder
     * FIXME: 2017-07-26-0026 imgRes is always null
     */
    public static SpannableStringBuilder getEmotionContent(final Context context, final TextView tv,
                                                           SpannableStringBuilder source) {
        SpannableStringBuilder spannableString = source;
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = null;//
            if (imgRes != null) {
                // 压缩表情图片
                int size = (int) tv.getTextSize();
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                if (bitmap == null) continue;
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 获取顶层activity名称
     *
     * @param context 上下文
     * @return 名称
     */
    public static String getTopActivityName(Context context) {
        String topActivityName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = f.getClassName();
            String temp[] = topActivityClassName.split("\\.");
            topActivityName = temp[temp.length - 1];
            System.out.println("topActivityName=" + topActivityName);
        }
        return topActivityName;
    }
}
