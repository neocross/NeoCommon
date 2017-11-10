// ILocalRouter.aidl
package cn.neocross.libs.common;

// Declare any non-default types here with import statements
/**
* 单进程本地局域路由,实现进程内部连接,脱离外部广域网控制,降低耦合
*
* Created by shenhua on 2017-09-08-0008.
* Email shenhuanet@126.com
*/
interface ILocalRouter {

    /**
     * 路由途径
     * @param routerRequest
     */
    String route(String routerRequest);

    /**
     * 异步检测响应
     * @param routerRequest
     */
    boolean checkResponseAsync(String routerRequest);

    /**
     * 连接外部路由
     */
    void connectWideRouter();

    /**
     * 停止外部路由
     */
    boolean stopWideRouter();
}
