package cn.neocross.libs.common.router;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.neocross.libs.common.router.multiprocess.BaseApplicationLogic;
import cn.neocross.libs.common.router.multiprocess.PriorityLogicWrapper;
import cn.neocross.libs.common.router.multiprocess.WideRouterApplicationLogic;
import cn.neocross.libs.common.router.service.WideRouterConnectService;

/**
 * 路由应用,用于初始化应用逻辑和进程配置,所有application继承本RouterApplication.
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public abstract class RouterApplication extends Application {

    private static final String TAG = "RouterApplication";
    private static RouterApplication sInstance;
    private ArrayList<PriorityLogicWrapper> mLogicList;// 逻辑优先级列表
    private HashMap<String, ArrayList<PriorityLogicWrapper>> mLogicClassMap;// 存储逻辑实现类

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        long start = System.currentTimeMillis();
        init();
        startWideRouter();
        initLogic();
        dispatchLogic();
        instantiateLogic();

        // 遍历逻辑
        if (mLogicList != null && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (priorityLogicWrapper != null && priorityLogicWrapper.instance != null) {
                    priorityLogicWrapper.instance.onCreate();
                }
            }
        }
        Log.i(TAG, "RouterApplication onCreate finished in: " +
                (double) (System.currentTimeMillis() - start) / 1000);
    }

    /**
     * 初始化
     */
    private void init() {
        LocalRouter.with(this);
        mLogicClassMap = new HashMap<>();
    }

    /**
     * 开启广域路由
     */
    protected void startWideRouter() {
        if (needMultiProcess()) {
            registerApplicationLogic(WideRouter.PROCESS_NAME, 1000, WideRouterApplicationLogic.class);
            Intent intent = new Intent(this, WideRouterConnectService.class);
            startService(intent);
        }

    }

    /**
     * 是否需要多进程
     *
     * @return true为需要
     */
    public abstract boolean needMultiProcess();

    /**
     * 初始化逻辑
     */
    protected abstract void initLogic();

    /**
     * 初始化所有进程路由
     */
    public abstract void initAllProcessRouter();

    /**
     * 分发逻辑
     */
    private void dispatchLogic() {
        if (mLogicClassMap != null) {
            mLogicList = mLogicClassMap.get(getProcessName(this, getMyProcessId()));
        }
    }

    /**
     * 实例化逻辑
     */
    private void instantiateLogic() {
        if (mLogicList != null && mLogicList.size() > 0) {
            Collections.sort(mLogicList);
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (priorityLogicWrapper != null) {
                    try {
                        priorityLogicWrapper.instance = priorityLogicWrapper.logic.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (priorityLogicWrapper.instance != null) {
                        priorityLogicWrapper.instance.setApplication(this);
                    }
                }
            }
        }
    }

    /**
     * 注册application逻辑
     *
     * @param processName 进程名
     * @param priority    优先级
     * @param logic       逻辑
     * @return false
     */
    protected boolean registerApplicationLogic(String processName, int priority,
                                               @NonNull Class<? extends BaseApplicationLogic> logic) {
        if (mLogicClassMap != null) {
            ArrayList<PriorityLogicWrapper> temp = mLogicClassMap.get(processName);
            if (temp == null) {
                temp = new ArrayList<>();
                mLogicClassMap.put(processName, temp);
            }
            if (temp.size() > 0) {
                for (PriorityLogicWrapper priorityLogicWrapper : temp) {
                    if (logic.getName().equals(priorityLogicWrapper.logic.getName())) {
                        throw new RuntimeException(logic.getName() + "进程逻辑已注册。");
                    }
                }
            }
            PriorityLogicWrapper logicWrapper = new PriorityLogicWrapper(priority, logic);
            temp.add(logicWrapper);
        }
        return false;
    }

    /**
     * 结束时调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mLogicList != null && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (priorityLogicWrapper != null && priorityLogicWrapper.instance != null) {
                    priorityLogicWrapper.instance.onTerminate();
                }
            }
        }
    }

    /**
     * 低内存时调用
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mLogicList && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (null != priorityLogicWrapper && null != priorityLogicWrapper.instance) {
                    priorityLogicWrapper.instance.onLowMemory();
                }
            }
        }
    }

    /**
     * 内存回收时调用
     *
     * @param level 水平
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (mLogicList != null && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (priorityLogicWrapper != null && priorityLogicWrapper.instance != null) {
                    priorityLogicWrapper.instance.onTrimMemory(level);
                }
            }
        }
    }

    /**
     * 配置改变时调用
     *
     * @param newConfig 新的配置
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mLogicList != null && mLogicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : mLogicList) {
                if (priorityLogicWrapper != null && priorityLogicWrapper.instance != null) {
                    priorityLogicWrapper.instance.onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public static RouterApplication getRouterApplication() {
        return sInstance;
    }

    public static final String UNKNOWN_PROCESS_NAME = "unknown_process_name";

    public static int getMyProcessId() {
        return android.os.Process.myPid();
    }

    public static String getProcessName(int pid) {
        String processName = UNKNOWN_PROCESS_NAME;
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        }
        return UNKNOWN_PROCESS_NAME;
    }

    public static String getProcessName(Context context, int pid) {
        String processName = getProcessName(pid);
        if (UNKNOWN_PROCESS_NAME.equals(processName)) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return UNKNOWN_PROCESS_NAME;
            }
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        } else {
            return processName;
        }
        return UNKNOWN_PROCESS_NAME;
    }
}
