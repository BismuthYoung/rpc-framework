package org.bohan.rpc.client.proxy

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.IOClient
import org.bohan.rpc.contract.domain.req.RpcRequest
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.NullPointerException


@Slf4j
class ClientProxy(
    private val host: String,
    private val port: Int
): InvocationHandler {

    init {
        require(host.isNotBlank()) { "服务端主机地址不得为空" }
        require(port in 1..65535) { "端口号必须在 1 至 65535 之间" }
    }

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any {
        val request = RpcRequest(
            methodName = method.name,
            interfaceName = method.declaringClass.name,
            params = args,
            paramsType = method.parameterTypes
        )

        log.info("发送请求：$request")

        val response = IOClient.sendRequest(host, port, request)
            ?: throw NullPointerException("Failed to receive a response from the server for request: $request")

        log.info("响应内容为：$response")

        return response.data ?: throw NullPointerException("服务端没有返回数据")
    }

    fun <T> getProxy(clazz: Class<T>): T {
        val obj  = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), this)
        return obj as T
    }

}