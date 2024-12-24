package org.bohan.rpc.client.proxy

import org.bohan.rpc.client.IOClient
import org.bohan.rpc.client.domain.req.RpcRequest
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


class ClientProxy(
    private val host: String,
    private val port: Int
): InvocationHandler {

    //jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    @Throws(Throwable::class)
    override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any? {
        //构建request
        val request = RpcRequest(
            methodName = method.declaringClass.name,
            interfaceName = method.name,
            params = args,
            paramsType = method.parameterTypes
        )
        //IOClient.sendRequest 和服务端进行数据传输
        val response = IOClient.sendRequest(host, port, request)
        return response?.data
    }

    fun <T> getProxy(clazz: Class<T>): T {
        val o: Any = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), this)
        return o as T
    }

}