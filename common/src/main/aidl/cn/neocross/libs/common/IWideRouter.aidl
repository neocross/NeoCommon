// IWideRouter.aidl
package cn.neocross.libs.common;

/**
* 多进程广域路由,通过本路由,来实现模块与模块之间的隔离.
* 参考:VIPER（View Interactor Presenter Entity Routing）
*
* Created by shenhua on 2017-09-08-0008.
* Email shenhuanet@126.com
*/
interface IWideRouter {

    /**
     * 路由途径
     * @param domain 域名
     * @param routerRequest
     */
    String route(String domain,String routerRequest);

    /**
     * 异步检测响应
     * @param domain 域名
     * @param routerRequest
     */
    boolean checkResponseAsync(String domain,String routerRequest);

    /**
     * 停止路由
     * @param domain 域名
     */
    boolean stopRouter(String domain);
}
