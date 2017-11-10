package cn.neocross.libs.common.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 动画工具类
 * Created by shenhua on 11/8/2016.
 * Email shenhuanet@126.com
 */
public class ViewAnimUtil {

    private static int panelHeight = 0;

    /**
     * 启动alpha动画
     *
     * @param v        view
     * @param duration 时长
     */
    public static void startAlphaAnim(View v, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0.0f, 1.0f);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * 启动放大动画,从0-1
     *
     * @param v        view
     * @param duration 时长
     */
    public static void startScaleAnim(View v, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "scaleX", 0.0f, 1.0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "scaleY", 0.0f, 1.0f);
        animator.setDuration(duration);
        animator2.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator2.start();
    }

    /**
     * 启动移动动画
     *
     * @param view            view
     * @param translationName x方向为 translationX y方向为translationY
     * @param b               在该方向上 正进行或反进行
     */
    public static void startTranslationAnim(final View view, final String translationName, final boolean b) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                panelHeight = view.getHeight();
                ObjectAnimator.ofFloat(view, translationName, b ? -panelHeight : panelHeight, 0).setDuration(2000).start();
            }
        });
    }

    /**
     * 启动alpha动画
     *
     * @param view     view
     * @param duration 时长
     * @param from     from
     * @param to       to
     */
    public static void startAlphaAnim(View view, float from, float to, int duration) {
        ObjectAnimator.ofFloat(view, "alpha", from, to).setDuration(duration).start();
    }

    /**
     * 启动放大动画,从0-1
     *
     * @param view     view
     * @param scale    缩放程度
     * @param duration 时长
     */
    public static void startScaleAnim(View view, float[] scale, int duration) {
        ObjectAnimator.ofFloat(view, "scaleX", scale[0], scale[1]).setDuration(duration).start();
        ObjectAnimator.ofFloat(view, "scaleY", scale[0], scale[1]).setDuration(duration).start();
    }

    /**
     * 将给定视图渐渐隐去（view.setVisibility(View.INVISIBLE)）
     *
     * @param view              被处理的视图
     * @param isBanClick        在执行动画的过程中是否禁止点击
     * @param durationMillis    持续时间，毫秒
     * @param animationListener 动画监听器
     */
    public static void invisibleViewByAlpha(final View view, long durationMillis, final boolean isBanClick,
                                            final AnimationListener animationListener) {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
            AlphaAnimation hiddenAlphaAnimation = getAlphaAnimation(1.0f, 0f, durationMillis, animationListener);
            hiddenAlphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(false);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isBanClick) {
                        view.setClickable(true);
                    }
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }
            });
            view.startAnimation(hiddenAlphaAnimation);
        }
    }

    /**
     * 获取一个透明度渐变动画
     *
     * @param fromAlpha         开始时的透明度
     * @param toAlpha           结束时的透明度都
     * @param durationMillis    持续时间
     * @param animationListener 动画监听器
     * @return 一个透明度渐变动画
     */
    public static AlphaAnimation getAlphaAnimation(float fromAlpha, float toAlpha, long durationMillis, AnimationListener animationListener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(durationMillis);
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener);
        }
        return alphaAnimation;
    }

}
