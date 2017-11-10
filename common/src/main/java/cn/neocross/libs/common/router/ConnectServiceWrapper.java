package cn.neocross.libs.common.router;

import cn.neocross.libs.common.router.service.LocalRouterConnectService;

/**
 * 局域网路由连接服务的封装
 * Created by shenhua on 2017/9/9.
 * Email shenhuanet@126.com
 */
public class ConnectServiceWrapper {

    public Class<? extends LocalRouterConnectService> targetClass = null;

    public ConnectServiceWrapper( Class<? extends LocalRouterConnectService> logicClass) {
        this.targetClass = logicClass;
    }
}
