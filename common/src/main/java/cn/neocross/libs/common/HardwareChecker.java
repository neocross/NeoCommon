package cn.neocross.libs.common;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 硬件检测工具类
 * Created by shenhua on 3/27/2017.
 * Email shenhuanet@126.com
 */
public class HardwareChecker {

    private static volatile HardwareChecker instance = null;
    public static final int FEATURE_CAMERA_BACK = 0xDC200101; // 后置相机
    public static final int FEATURE_CAMERA_FRONT = 0xDC200102;// 前置相机
    public static final int FEATURE_BLUETOOTH = 0xDC200103;// 蓝牙
    public static final int FEATURE_SENSOR = 0xDC200104;// 陀螺仪
    public static final int FEATURE_FINGER = 0xDC200105;// 指纹识别

    public static HardwareChecker getInstance() {
        if (instance == null) {
            synchronized (HardwareChecker.class) {
                if (instance == null) {
                    instance = new HardwareChecker();
                }
            }
        }
        return instance;
    }

    /**
     * 是否支持相机
     *
     * @param activity activity
     * @return true 支持 false 不支持
     */
    public boolean isDeviceSupportCamera(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        return !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /**
     * 是否支持蓝牙
     *
     * @param activity activity
     * @return true 支持 false 不支持
     */
    public boolean isDeviceSupportBle(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        } else {
            return !pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        }
    }

    /**
     * 是否支持相机聚焦
     *
     * @param activity activity
     * @return true 支持 false 不支持
     */
    public boolean isDeviceSupportCameraAutoFocus(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        return !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }

    /**
     * 是否支持网络
     *
     * @param activity activity
     * @return true 支持 false 不支持
     */
    public boolean isDeviceSupportNet(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return !pm.hasSystemFeature(PackageManager.FEATURE_ETHERNET) && !pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
        } else {
            return !pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
        }
    }
}
