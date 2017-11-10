package cn.neocross.libs.common.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.neocross.libs.common.IWideRouter;
import cn.neocross.libs.common.router.action.ErrorAction;
import cn.neocross.libs.common.router.action.RouterAction;
import cn.neocross.libs.common.router.action.RouterActionResult;
import cn.neocross.libs.common.router.data.RouterRequest;
import cn.neocross.libs.common.router.data.RouterResponse;
import cn.neocross.libs.common.router.service.LocalRouterConnectService;
import cn.neocross.libs.common.router.service.WideRouterConnectService;

/**
 * 本地路由
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class LocalRouter {

    private static final String TAG = "(LocalRouter.java:32)";
    private static LocalRouter sInstance = null;
    private static ExecutorService mService = null;
    private String mProcessName = RouterApplication.UNKNOWN_PROCESS_NAME;
    private RouterApplication mRouterApplication;
    private IWideRouter iWideRouter;
    private HashMap<String, RouterProvider> mRouterProvider = null;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iWideRouter = IWideRouter.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iWideRouter = null;
        }
    };

    /**
     * 单例
     *
     * @param routerApplication RouterApplication
     * @return LocalRouter
     */
    public static synchronized LocalRouter with(RouterApplication routerApplication) {
        if (sInstance == null) {
            sInstance = new LocalRouter(routerApplication);
        }
        return sInstance;
    }

    private LocalRouter(RouterApplication routerApplication) {
        this.mRouterApplication = routerApplication;
        mProcessName = RouterApplication.getProcessName(routerApplication, RouterApplication.getMyProcessId());
        mRouterProvider = new HashMap<>();
        if (mRouterApplication.needMultiProcess() && !WideRouter.PROCESS_NAME.equals(mProcessName)) {
            connectWideRouter();
        }
    }

    /**
     * 获取线程池服务
     *
     * @return ExecutorService
     */
    private static synchronized ExecutorService getExecutorService() {
        if (mService == null) {
            mService = Executors.newCachedThreadPool();
        }
        return mService;
    }

    public RouterResponse route(Context context, RouterRequest routerRequest) throws Exception {
        RouterResponse routerResponse = new RouterResponse();
        // 本地请求
        if (mProcessName.equals(routerRequest.getDomain())) {
            HashMap<String, String> params = new HashMap<>();
            Object attachment = routerRequest.getAndClearObject();
            params.putAll(routerRequest.getData());
            RouterAction targetAction = findRequestAction(routerRequest);
            routerRequest.isIdle.set(true);
            routerResponse.mIsAsync = attachment == null ? targetAction.isAsync(context, params)
                    : targetAction.isAsync(context, params, attachment);
            // 同步结果，则立即返回
            if (!routerResponse.mIsAsync) {
                RouterActionResult result = (attachment == null) ? targetAction.invoke(context, params)
                        : targetAction.invoke(context, params, attachment);
                routerResponse.mResultString = result.toString();
                routerResponse.mObject = result.getObject();
            }
            // 异步结果，等待本地线程池去执行
            else {
                LocalTask task = new LocalTask(routerResponse, params, attachment, context, targetAction);
                routerResponse.mAsyncResponse = getExecutorService().submit(task);
            }
        } else if (!mRouterApplication.needMultiProcess()) {
            throw new Exception("需要在RouterApplication中返回true来支持多进程，这样才能反射其它进程的动作。");
        }
        // IPC请求
        else {
            String domain = routerRequest.getDomain();
            String routerRequestString = routerRequest.toString();
            routerRequest.isIdle.set(true);
            if (checkWideRouterConnection()) {
                // 可使用 routerResponse.mIsAsync = false 表示不需要异步检测
                routerResponse.mIsAsync = iWideRouter.checkResponseAsync(domain, routerRequestString);
            }
            // 未连接到广域路由.
            else {
                routerResponse.mIsAsync = true;
                ConnectWideTask task = new ConnectWideTask(routerResponse, domain, routerRequestString);
                routerResponse.mAsyncResponse = getExecutorService().submit(task);
                return routerResponse;
            }
            if (!routerResponse.mIsAsync) {
                routerResponse.mResultString = iWideRouter.route(domain, routerRequestString);
            }
            // 异步结果，等待广域线程池去执行
            else {
                WideTask task = new WideTask(domain, routerRequestString);
                routerResponse.mAsyncResponse = getExecutorService().submit(task);
            }
        }
        return routerResponse;
    }

    /**
     * 是否是异步请求
     *
     * @param routerRequest 请求数据
     * @return true为异步
     */
    public boolean answerWiderAsync(RouterRequest routerRequest) {
        return !(mProcessName.equals(routerRequest.getDomain()) && checkWideRouterConnection())
                || findRequestAction(routerRequest).isAsync(mRouterApplication, routerRequest.getData());
    }

    /**
     * 连接到广域路由器
     */
    public void connectWideRouter() {
        Intent intent = new Intent(mRouterApplication, WideRouterConnectService.class);
        intent.putExtra("domain", mProcessName);
        mRouterApplication.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 断开广域路由
     */
    public void disConnectWideRouter() {
        if (mServiceConnection == null) {
            return;
        }
        mRouterApplication.unbindService(mServiceConnection);
        iWideRouter = null;
    }

    /**
     * 是否连接到广域路由
     *
     * @return true 表示已连接
     */
    public boolean checkWideRouterConnection() {
        return iWideRouter != null;
    }

    /**
     * 注册动作提供者
     *
     * @param providerName 提供者名称
     * @param provider     提供者
     */
    public void registerProvider(String providerName, RouterProvider provider) {
        mRouterProvider.put(providerName, provider);
    }

    /**
     * 查找请求动作
     *
     * @param routerRequest 请求数据
     * @return 真正的执行动作
     */
    private RouterAction findRequestAction(RouterRequest routerRequest) {
        RouterProvider targetProvider = mRouterProvider.get(routerRequest.getProvider());
        ErrorAction defaultNotFoundAction = new ErrorAction(RouterActionResult.CODE_NOT_FOUND,
                "没有找到对应到动作", false);
        if (targetProvider == null) {
            return defaultNotFoundAction;
        } else {
            RouterAction targetAction = targetProvider.findAction(routerRequest.getAction());
            if (targetAction == null) {
                return defaultNotFoundAction;
            } else {
                return targetAction;
            }
        }
    }

    /**
     * 关闭本身
     *
     * @param clazz LocalRouterConnectService
     * @return 正常关闭则返回true
     */
    public boolean stopSelf(Class<? extends LocalRouterConnectService> clazz) {
        if (checkWideRouterConnection()) {
            try {
                return iWideRouter.stopRouter(mProcessName);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            mRouterApplication.stopService(new Intent(mRouterApplication, clazz));
            return true;
        }
    }

    /**
     * 停止广域路由
     */
    public void stopWideRouter() {
        if (checkWideRouterConnection()) {
            try {
                iWideRouter.stopRouter(WideRouter.PROCESS_NAME);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "该本地路由还未连接到广域路由。");
        }
    }

    /**
     * 本地线程任务，实现Callable接口
     */
    private class LocalTask implements Callable<String> {
        private RouterResponse mResponse;
        private HashMap<String, String> mRequestData;
        private Context mContext;
        private RouterAction mAction;
        private Object mObject;

        public LocalTask(RouterResponse routerResponse, HashMap<String, String> requestData,
                         Object object, Context context, RouterAction routerAction) {
            this.mContext = context;
            this.mResponse = routerResponse;
            this.mRequestData = requestData;
            this.mAction = routerAction;
            this.mObject = object;
        }

        @Override
        public String call() throws Exception {
            RouterActionResult result = mObject == null ? mAction.invoke(mContext, mRequestData)
                    : mAction.invoke(mContext, mRequestData, mObject);
            mResponse.mObject = result.getObject();
            return result.toString();
        }
    }

    /**
     * 广域线程任务
     */
    private class WideTask implements Callable<String> {

        private String mDomain;
        private String mRequestString;

        public WideTask(String domain, String requestString) {
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            return iWideRouter.route(mDomain, mRequestString);
        }
    }

    /**
     * 用于连接到广域线程到任务
     */
    private class ConnectWideTask implements Callable<String> {
        private RouterResponse mResponse;
        private String mDomain;
        private String mRequestString;

        public ConnectWideTask(RouterResponse routerResponse, String domain, String requestString) {
            this.mResponse = routerResponse;
            this.mDomain = domain;
            this.mRequestString = requestString;
        }

        @Override
        public String call() throws Exception {
            connectWideRouter();
            int time = 0;
            while (true) {
                if (iWideRouter == null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time++;
                } else {
                    break;
                }
                if (time >= 600) {
                    ErrorAction defaultNotFoundAction = new ErrorAction(RouterActionResult.CODE_CANNOT_BIND_WIDE,
                            "绑定远程路由超时。", true);
                    RouterActionResult result = defaultNotFoundAction.invoke(mRouterApplication,
                            new HashMap<String, String>());
                    mResponse.mResultString = result.toString();
                    return result.toString();
                }
            }
            return iWideRouter.route(mDomain, mRequestString);
        }
    }
}
