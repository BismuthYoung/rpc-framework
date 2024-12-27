package org.bohan.rpc.client.proxy

import org.bohan.component.common.hocon.ConfigLoader
import org.bohan.component.common.hocon.annotation.Config
import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.client.client.impl.IOClient
import org.bohan.rpc.client.client.impl.SimpleSocketRpcClient
import org.bohan.rpc.client.conf.ClientConfig
import org.bohan.rpc.client.conf.enums.ClientType
import org.bohan.rpc.contract.domain.req.RpcRequest
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.NullPointerException


@Slf4j
class ClientProxy(
    private val client: RpcClient
): InvocationHandler {

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any {
        val request = RpcRequest(
            methodName = method.name,
            interfaceName = method.declaringClass.name,
            params = args,
            paramsType = method.parameterTypes
        )

        log.info("发送请求：$request")

        val response = client.sendRequest(request)
            ?: throw NullPointerException("未能收到服务器对请求的响应: $request")

        log.info("响应内容为：$response")

        return response.data ?: throw NullPointerException("服务端没有返回数据")
    }

    fun <T> getProxy(clazz: Class<T>): T {
//        log.debug("[rpc][客户端] 进入获取代理服务方法")
        val obj  = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), this)
        return obj as T
    }

}