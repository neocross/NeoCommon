package cn.neocross.libs.common.router.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import cn.neocross.libs.common.IWideRouter;
import cn.neocross.libs.common.router.RouterApplication;
import cn.neocross.libs.common.router.WideRouter;
import cn.neocross.libs.common.router.action.RouterActionResult;

/**
 * 广域网内路由连接服务，提供初始多进程间连接服务，需要接收一个 域名 domain
 * Created by shenhua on 2017/9/9.
 * Email shenhuanet@126.com
 */
public final class WideRouterConnectService extends Service {

    private static final String TAG = "(WideRouterConnectService.java:23)";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!(getApplication() instanceof RouterApplication)) {
            throw new RuntimeException("application非RouterApplication。");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String domain = intent.getStringExtra("domain");
        if (WideRouter.with(RouterApplication.getRouterApplication()).mIsStopping) {
            Log.e(TAG, "绑定错误，该路由已停止。");
            return null;
        }
        if (TextUtils.isEmpty(domain)) {
            Log.e(TAG, "绑定错误，没有提供intent域名。");
            return null;
        }
        boolean hasRegister = WideRouter.with(RouterApplication.getRouterApplication())
                .checkLocalRouterHasRegistered(domain);
        if (!hasRegister)
            return null;
        WideRouter.with(RouterApplication.getRouterApplication()).connectLocalRouter(domain);
        return stub;
    }

    /**
     * 广域网路由aidl通讯服务实现
     */
    IWideRouter.Stub stub = new IWideRouter.Stub() {
        @Override
        public String route(String domain, String routerRequest) throws RemoteException {
            try {
                return WideRouter.with(RouterApplication.getRouterApplication())
                        .route(domain, routerRequest).mResultString;
            } catch (Exception e) {
                e.printStackTrace();
                return new RouterActionResult.Builder().code(RouterActionResult.CODE_ERROR)
                        .msg(e.getMessage()).build().toString();
            }
        }

        @Override
        public boolean checkResponseAsync(String domain, String routerRequest) throws RemoteException {
            return WideRouter.with(RouterApplication.getRouterApplication())
                            .answerLocalAsync(domain, routerRequest);
        }

        @Override
        public boolean stopRouter(String domain) throws RemoteException {
            return WideRouter.with(RouterApplication.getRouterApplication())
                    .disconnectLocalRouter(domain);
        }
    };
}
