package cn.neocross.libs.common.router;

import java.util.HashMap;

import cn.neocross.libs.common.router.action.RouterAction;

/**
 * 路由提供者,需要在各个模块中动态注册相关动作
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public abstract class RouterProvider {

    private boolean mValid = true;// 有效的,控制Provider的开关
    private HashMap<String, RouterAction> mActions;

    public RouterProvider() {
        mActions = new HashMap<>();
        registerActions();
    }

    /**
     * 注册动作
     */
    protected abstract void registerActions();

    /**
     * 注册动作
     *
     * @param actionName 动作名称
     * @param action     动作
     */
    protected void registerAction(String actionName, RouterAction action) {
        mActions.put(actionName, action);
    }

    /**
     * 查找动作
     *
     * @param actionName 动作名称
     * @return 动作
     * {@link RouterAction}
     */
    public RouterAction findAction(String actionName) {
        return mActions.get(actionName);
    }

    /**
     * 是否有效
     *
     * @return true 则为有效
     */
    public boolean isValid() {
        return mValid;
    }

}
