package cn.neocross.libs.common.router.action;

import android.content.Context;

import java.util.HashMap;

/**
 * 路由请求动作
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public abstract class RouterAction {

    /**
     * 反射路由动作结果
     *
     * @param context     上下文
     * @param requestData 请求数据
     * @return 路由动作结果
     */
    public abstract RouterActionResult invoke(Context context, HashMap<String, String> requestData);

    /**
     * 是否异步
     *
     * @param context     上下文
     * @param requestData 请求数据
     * @return 是否异步
     */
    public abstract boolean isAsync(Context context, HashMap<String, String> requestData);

    /**
     * 是否异步
     *
     * @param context     上下文
     * @param requestData 请求数据
     * @param object      对象
     * @return 是否异步
     */
    public boolean isAsync(Context context, HashMap<String, String> requestData, Object object) {
        return false;
    }

    /**
     * 反射路由动作结果
     *
     * @param context     上下文
     * @param requestData 请求数据
     * @param object      对象
     * @return 路由动作结果
     */
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData, Object object) {
        return new RouterActionResult.Builder()
                .code(RouterActionResult.CODE_NOT_IMPLEMENT)
                .msg("尚未实现。")
                .build();
    }
}
