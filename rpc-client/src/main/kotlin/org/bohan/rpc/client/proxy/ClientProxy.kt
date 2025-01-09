package org.bohan.rpc.client.proxy

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.client.proxy.breaker.SimpleCircuitBreakerProvider
import org.bohan.rpc.client.proxy.retry.RpcRequestRetryHandler
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import java.lang.IllegalStateException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.NullPointerException

@Slf4j
class ClientProxy(
    private val client: RpcClient,
    private val circuitBreakerProvider: SimpleCircuitBreakerProvider,
): InvocationHandler {

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any {
        val request = RpcRequest(
            methodName = method.name,
            interfaceName = method.declaringClass.name,
            params = args,
            paramsType = method.parameterTypes
        )
        // 获取熔断器请求，判断是否可以发送请求
        val circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.declaringClass.name)
        if (! circuitBreaker.allowRequest()) {
            throw IllegalStateException("当前请求服务已熔断")
        }

        val serviceCenter = client.getServiceCenter()
        val response: RpcResponse<*>
        if (serviceCenter == null) {
            log.info("[rpc][客户端] SimpleRpcClient 发送请求：$request")
            response = client.sendRequest(request) ?: throw NullPointerException("未能收到服务器对请求的响应: $request")
            log.info("[rpc][客户端] 响应内容为：$response")

        } else {
            log.info("发送请求：$request")
            response = if (serviceCenter.checkRetry(method.declaringClass.name))
                RpcRequestRetryHandler(client).sendRequestWithRetry(request) else
                client.sendRequest(request) ?: throw NullPointerException("未能收到服务器对请求的响应: $request")
            log.info("响应内容为：$response")
        }

        // 记录请求状态
        if (response.successful()) {
            circuitBreaker.recordSuccess()
        } else {
            circuitBreaker.recordFailure()
        }

        return response.data ?: throw NullPointerException("服务端没有返回数据")
    }

    fun <T> getProxy(clazz: Class<T>): T {
//        log.debug("[rpc][客户端] 进入获取代理服务方法")
        val obj  = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), this)
        return obj as T
    }

}