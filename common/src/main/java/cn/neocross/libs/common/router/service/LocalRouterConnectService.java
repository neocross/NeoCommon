package cn.neocross.libs.common.router.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import cn.neocross.libs.common.ILocalRouter;
import cn.neocross.libs.common.router.LocalRouter;
import cn.neocross.libs.common.router.RouterApplication;
import cn.neocross.libs.common.router.action.RouterActionResult;
import cn.neocross.libs.common.router.data.RouterRequest;
import cn.neocross.libs.common.router.data.RouterResponse;

/**
 * 本地路由连接服务，用来实现局域网内多进程间通讯
 * Created by shenhua on 2017/9/9.
 * Email shenhuanet@126.com
 */
public class LocalRouterConnectService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * aidl通讯实现
     */
    ILocalRouter.Stub stub = new ILocalRouter.Stub() {

        @Override
        public String route(String routerRequest) throws RemoteException {
            try {
                LocalRouter localRouter = LocalRouter.with(RouterApplication.getRouterApplication());
                RouterRequest request = new RouterRequest.Builder(getApplicationContext())
                        .json(routerRequest).build();
                RouterResponse routerResponse = localRouter.route(LocalRouterConnectService.this, request);
                return routerResponse.get();
            } catch (Exception e) {
                e.printStackTrace();
                return new RouterActionResult.Builder().msg(e.getMessage()).build().toString();
            }

        }

        @Override
        public boolean checkResponseAsync(String routerRequest) throws RemoteException {
            return LocalRouter.with(RouterApplication.getRouterApplication()).
                    answerWiderAsync(new RouterRequest.Builder(getApplicationContext())
                            .json(routerRequest).build());
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            LocalRouter.with(RouterApplication.getRouterApplication()).connectWideRouter();
        }

        @Override
        public boolean stopWideRouter() throws RemoteException {
            LocalRouter.with(RouterApplication.getRouterApplication()).disConnectWideRouter();
            return true;
        }
    };

}
