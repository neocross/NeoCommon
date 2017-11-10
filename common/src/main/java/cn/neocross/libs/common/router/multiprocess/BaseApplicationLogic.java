package cn.neocross.libs.common.router.multiprocess;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

import cn.neocross.libs.common.router.RouterApplication;

/**
 * Application 基础逻辑
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class BaseApplicationLogic {

    protected RouterApplication mApplication;

    public BaseApplicationLogic() {
    }

    public void setApplication(@NonNull RouterApplication mApplication) {
        this.mApplication = mApplication;
    }

    /**
     * 创建时调用
     */
    public void onCreate() {
    }

    /**
     * 结束时调用
     */
    public void onTerminate() {
    }

    /**
     * 低内存时调用
     */
    public void onLowMemory() {
    }

    /**
     * 内存回收整理时调用
     *
     * @param level 占用内存水平
     */
    public void onTrimMemory(int level) {
    }

    /**
     * 资源配置改变时调用
     *
     * @param newConfig 新配置方案
     */
    public void onConfigurationChanged(Configuration newConfig) {
    }

}
