package cn.neocross.libs.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

/**
 * 作者：nodlee/1516lee@gmail.com
 * 时间：2016年12月13日
 * 说明：View工具类
 */

public class ViewUtils {

    /**
     * 批量按下view
     *
     * @param views views
     */
    public static void pressedView(View... views) {
        for (View view : views) {
            view.setPressed(true);
        }
    }

    /**
     * 批量选择view
     *
     * @param views views
     */
    public static void selectedView(View... views) {
        for (View view : views) {
            view.setSelected(true);
        }
    }

    /**
     * 批量不选择view
     *
     * @param views views
     */
    public static void disSelectedView(View... views) {
        for (View view : views) {
            view.setSelected(false);
        }
    }

    /**
     * 批量处理view的可用或不可用状态
     *
     * @param enable true 可用 false 不可用
     * @param views  views
     */
    public static void enableView(boolean enable, View... views) {
        for (View view : views) {
            view.setEnabled(enable);
        }
    }

    /**
     * 批量可见view
     *
     * @param views views
     */
    public static void visibilityView(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 批量不可见view
     *
     * @param views views
     */
    public static void goneView(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 批量不可见view
     *
     * @param views views
     */
    public static void inVisibilityView(View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 是否包含某个子view
     *
     * @param parent 父容器
     * @param child  子view
     * @return 包含则返回子view的位置, 不包含则返回-1
     */
    public static int safeIndexOfChild(ViewGroup parent, View child) {
        return (parent != null && child != null) ? parent.indexOfChild(child) : -1;
    }

    /**
     * 安全地移除一个view
     *
     * @param parent 父容器
     * @param child  子view
     */
    public static void safeRemoveChildView(ViewGroup parent, View child) {
        if (parent != null && child != null) {
            if (parent == child.getParent()) {
                parent.removeView(child);
                Log.d("ViewUtil", "did safeRemoveChildView : remove view = " + child + " parent = " + parent + " realParent = " + child.getParent());
            }
        }
    }

    /**
     * 安全地移除一个view
     *
     * @param child 子view
     */
    public static void safeRemoveChildView(View child) {
        if (child != null) {
            ViewParent parent = child.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(child);
                Log.d("ViewUtil", "did safeRemoveChildView : remove view = " + child + " parent = " + parent + " realParent = " + child.getParent());
            }
        }
    }

    /**
     * 安全地添加一个view
     *
     * @param parent 父容器
     * @param child  子view
     * @param index  添加位置
     */
    public static void safeAddChildView(ViewGroup parent, View child, int index) {
        if (parent != null && child != null) {
            if (child.getParent() == null && ViewUtils.safeIndexOfChild(parent, child) == -1) {
                parent.addView(child, index);
                Log.d("ViewUtil", "did safeAddChildView : add view = " + child + " parent = " + parent + " realParent = " + child.getParent());
            }
        }
    }

    /**
     * 将一个view创建为一个bitmap
     *
     * @param sourceView view
     * @return bitmap
     */
    public static Bitmap renderViewToBitmap(View sourceView) {
        if (sourceView != null) {
            int width = sourceView.getWidth();
            int height = sourceView.getHeight();
            if (width > 0 && height > 0) {
                Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                sourceView.draw(c);
                return b;
            }
        }
        return null;
    }

    /**
     * get the relative coordinate
     *
     * @param view         view
     * @param relativeView view
     * @return the relative location
     */
    public static Point getRelativeLocation(View view, View relativeView) {
        if (view != null && relativeView != null && view != relativeView) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int[] location2 = new int[2];
            relativeView.getLocationOnScreen(location2);
            return new Point(location[0] - location2[0], location[1] - location2[1]);
        }
        return new Point(0, 0);
    }

    /**
     * get the relative rect
     *
     * @param view         view
     * @param relativeView view
     * @return the relative view's rect
     */
    public static Rect getRelativeRect(View view, View relativeView) {
        if (relativeView != null && view != null) {
            Point point = getRelativeLocation(view, relativeView);
            return new Rect(point.x, point.y, point.x + view.getWidth(), point.y + view.getHeight());
        }
        return new Rect();
    }

    /**
     * 使系统导航栏不可见
     *
     * @param window window
     */
    public static void navigationBarTranslucent(Window window) {
        if (window == null) {
            return;
        }
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
