package cn.neocross.libs.common.router.multiprocess;

import cn.neocross.libs.common.router.WideRouter;

/**
 * Created by shenhua on 2017/9/9.
 * Email shenhuanet@126.com
 */
public final class WideRouterApplicationLogic extends BaseApplicationLogic {

    @Override
    public void onCreate() {
        super.onCreate();
        initRouter();
    }

    protected void initRouter() {
        WideRouter.with(mApplication);
        mApplication.initAllProcessRouter();
    }

}
