package org.bohan.rpc.server.worker

import org.bohan.rpc.server.domain.req.RpcRequest
import org.bohan.rpc.server.domain.resp.RpcResponse
import org.bohan.rpc.server.provider.ServiceProvider
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.reflect.InvocationTargetException
import java.net.Socket

class WorkThread(
    private val socket: Socket,
    private val serviceProvider: ServiceProvider
): Runnable {

    override fun run() {
        try {
            val oos = ObjectOutputStream(socket.getOutputStream())
            val ois = ObjectInputStream(socket.getInputStream())

            // 读取客户端传过来的 RpcRequest
            val rpcRequest = ois.readObject() as RpcRequest

            // 反射调用服务方法获取返回值
            val rpcResponse = getResponse(rpcRequest)

            // 向客户端写入 RpcResponse
            oos.writeObject(rpcResponse)
            oos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getResponse(rpcRequest: RpcRequest): RpcResponse<Any?> {
        // 得到服务名
        val interfaceName = rpcRequest.interfaceName

        // 得到服务端相应服务实现类
        val service = serviceProvider.getService(interfaceName) ?: throw IllegalArgumentException("该服务未被注册在容器中")

        // 反射调用方法
        return try {
            val method = service::class.java.getMethod(rpcRequest.methodName, *rpcRequest.paramsType)
            val invokeResult = method.invoke(service, *rpcRequest.params)
            RpcResponse.success(invokeResult)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            println("方法执行错误")
            RpcResponse.error()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            RpcResponse.error()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            RpcResponse.error()
        }
    }

}