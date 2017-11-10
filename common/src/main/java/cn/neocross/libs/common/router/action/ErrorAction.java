package cn.neocross.libs.common.router.action;

import android.content.Context;

import java.util.HashMap;

/**
 * 错误动作
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class ErrorAction extends RouterAction {

    private static final String DEFAULT_MSG = "似乎发生了错误";
    private int mCode;// 返回码
    private String mMsg;// 信息文本
    private boolean mAsync;// 是否异步

    public ErrorAction() {
        mCode = RouterActionResult.CODE_ERROR;
        mMsg = DEFAULT_MSG;
        mAsync = false;
    }

    public ErrorAction(int mCode, String mMsg, boolean mAsync) {
        this.mCode = mCode;
        this.mMsg = mMsg;
        this.mAsync = mAsync;
    }

    /**
     * 反射数据请求的结果
     * @param context     上下文
     * @param requestData 请求数据
     * @return 动作结果
     */
    @Override
    public RouterActionResult invoke(Context context, HashMap<String, String> requestData) {
        return new RouterActionResult.Builder()
                .code(mCode).msg(mMsg).data(null).object(null).build();
    }

    /**
     * 是否为异步任务
     * @param context     上下文
     * @param requestData 请求数据
     * @return true则为异步
     */
    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return mAsync;
    }
}
