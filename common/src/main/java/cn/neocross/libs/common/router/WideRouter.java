package cn.neocross.libs.common.router;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.neocross.libs.common.ILocalRouter;
import cn.neocross.libs.common.router.action.RouterActionResult;
import cn.neocross.libs.common.router.data.RouterResponse;
import cn.neocross.libs.common.router.service.LocalRouterConnectService;
import cn.neocross.libs.common.router.service.WideRouterConnectService;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 广域网路由
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class WideRouter {

    private static final String TAG = "(WideRouter.java:9)";
    public static final String PROCESS_NAME = "cn.neocross.common.router.widerouter";
    private static HashMap<String, ConnectServiceWrapper> sLocalRouterClasses;// 用于存储本地路由优先级
    private static WideRouter sInstance = null;
    private RouterApplication mApplication;
    private HashMap<String, ServiceConnection> mLocalRouterConnectionMap;
    private HashMap<String, ILocalRouter> mLocalRouterMap;
    public boolean mIsStopping = false;// 是否已经停止

    public static WideRouter with(@NonNull RouterApplication mApplication) {
        if (sInstance == null) {
            sInstance = new WideRouter(mApplication);
        }
        return sInstance;
    }

    private WideRouter(RouterApplication context) {
        mApplication = context;
        String checkProcessName = RouterApplication.getProcessName(context, RouterApplication.getMyProcessId());
        if (!PROCESS_NAME.equals(checkProcessName)) {
            throw new RuntimeException("不应该初始化该进程：" + checkProcessName);
        }
        sLocalRouterClasses = new HashMap<>();
        mLocalRouterConnectionMap = new HashMap<>();
        mLocalRouterMap = new HashMap<>();
    }

    /**
     * 注册本地路由
     *
     * @param processName 进程名
     * @param targetClass 目标本地路由
     */
    public static void registerLocalRouter(String processName,
                                           Class<? extends LocalRouterConnectService> targetClass) {
        if (sLocalRouterClasses == null) {
            sLocalRouterClasses = new HashMap<>();
        }
        ConnectServiceWrapper connectServiceWrapper = new ConnectServiceWrapper(targetClass);
        sLocalRouterClasses.put(processName, connectServiceWrapper);
    }

    /**
     * 检测某个本地路由是否已经注册
     *
     * @param domain 域名
     * @return true表示已经注册
     */
    public boolean checkLocalRouterHasRegistered(final String domain) {
        ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
        if (connectServiceWrapper == null) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        return clazz != null;
    }

    /**
     * 连接到本地路由
     *
     * @param domain 域名
     * @return true表示连接成功
     */
    public boolean connectLocalRouter(final String domain) {
        ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
        if (connectServiceWrapper == null) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        if (clazz == null) {
            return false;
        }
        Intent binderIntent = new Intent(mApplication, clazz);
        Bundle bundle = new Bundle();
        binderIntent.putExtras(bundle);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocalRouter mLocalRouterAIDL = ILocalRouter.Stub.asInterface(service);
                ILocalRouter temp = mLocalRouterMap.get(domain);
                if (temp == null) {
                    mLocalRouterMap.put(domain, mLocalRouterAIDL);
                    mLocalRouterConnectionMap.put(domain, this);
                    try {
                        mLocalRouterAIDL.connectWideRouter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocalRouterMap.remove(domain);
                mLocalRouterConnectionMap.remove(domain);
            }
        };
        mApplication.bindService(binderIntent, serviceConnection, BIND_AUTO_CREATE);
        return true;
    }

    /**
     * 断开本地路由连接
     *
     * @param domain 域名
     * @return true表示断开成功
     */
    public boolean disconnectLocalRouter(String domain) {
        if (TextUtils.isEmpty(domain)) {
            return false;
        } else if (PROCESS_NAME.equals(domain)) {
            stopSelf();
            return true;
        } else if (mLocalRouterConnectionMap.get(domain) == null) {
            return false;
        } else {
            ILocalRouter aidl = mLocalRouterMap.get(domain);
            if (null != aidl) {
                try {
                    aidl.stopWideRouter();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
            mLocalRouterMap.remove(domain);
            mLocalRouterConnectionMap.remove(domain);
            return true;
        }
    }

    /**
     * 结束服务
     */
    private void stopSelf() {
        mIsStopping = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> locals = new ArrayList<>();
                locals.addAll(mLocalRouterMap.keySet());
                for (String domain : locals) {
                    ILocalRouter aidl = mLocalRouterMap.get(domain);
                    if (null != aidl) {
                        try {
                            aidl.stopWideRouter();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        mApplication.unbindService(mLocalRouterConnectionMap.get(domain));
                        mLocalRouterMap.remove(domain);
                        mLocalRouterConnectionMap.remove(domain);
                    }
                }
                try {
                    Thread.sleep(1000);
                    mApplication.stopService(new Intent(mApplication, WideRouterConnectService.class));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
    }

    /**
     * 本地路由是否异步
     *
     * @param domain        端口
     * @param routerRequest 请求数据
     * @return true为异步
     */
    public boolean answerLocalAsync(String domain, String routerRequest) {
        ILocalRouter target = mLocalRouterMap.get(domain);
        if (target == null) {
            ConnectServiceWrapper connectServiceWrapper = sLocalRouterClasses.get(domain);
            if (null == connectServiceWrapper) {
                return false;
            }
            Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
            return null != clazz;
        } else {
            try {
                return target.checkResponseAsync(routerRequest);
            } catch (RemoteException e) {
                e.printStackTrace();
                return true;
            }
        }
    }

    /**
     * 路由
     *
     * @param domain        端口
     * @param routerRequest 请求数据
     * @return 路由响应数据
     */
    public RouterResponse route(String domain, String routerRequest) {
        RouterResponse routerResponse = new RouterResponse();
        if (mIsStopping) {
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_WIDE_STOPPING)
                    .msg("广域网路由停止中。").build();
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        if (PROCESS_NAME.equals(domain)) {
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_TARGET_IS_WIDE)
                    .msg("域名不能为 " + PROCESS_NAME + "。")
                    .build();
            routerResponse.mIsAsync = true;
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        ILocalRouter target = mLocalRouterMap.get(domain);
        if (target == null) {
            if (!connectLocalRouter(domain)) {
                RouterActionResult result = new RouterActionResult.Builder()
                        .code(RouterActionResult.CODE_ROUTER_NOT_REGISTER)
                        .msg("The " + domain + " has not registered.")
                        .build();
                routerResponse.mIsAsync = false;
                routerResponse.mResultString = result.toString();
                return routerResponse;
            } else {
                // 30秒超时
                int time = 0;
                while (true) {
                    target = mLocalRouterMap.get(domain);
                    if (target == null) {
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
                        RouterActionResult result = new RouterActionResult.Builder()
                                .code(RouterActionResult.CODE_CANNOT_BIND_LOCAL)
                                .msg("不能绑定域名 " + domain + "，绑定超时。")
                                .build();
                        routerResponse.mResultString = result.toString();
                        return routerResponse;
                    }
                }
            }
        }
        try {
            routerResponse.mResultString = target.route(routerRequest);
        } catch (RemoteException e) {
            e.printStackTrace();
            RouterActionResult result = new RouterActionResult.Builder()
                    .code(RouterActionResult.CODE_REMOTE_EXCEPTION)
                    .msg(e.getMessage()).build();
            routerResponse.mResultString = result.toString();
            return routerResponse;
        }
        return routerResponse;
    }
}
