package cn.neocross.libs.common.router.multiprocess;

import android.support.annotation.NonNull;

/**
 * 多进程中用于优先逻辑的比较
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class PriorityLogicWrapper implements Comparable<PriorityLogicWrapper> {

    private int priority = 0;// 初始优先级
    public Class<? extends BaseApplicationLogic> logic = null;
    public BaseApplicationLogic instance;

    public PriorityLogicWrapper(int priority, Class<? extends BaseApplicationLogic> logic) {
        this.priority = priority;
        this.logic = logic;
    }

    @Override
    public int compareTo(@NonNull PriorityLogicWrapper o) {
        return o.priority - this.priority;
    }
}
