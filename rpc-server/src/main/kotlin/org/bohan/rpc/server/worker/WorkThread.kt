package org.bohan.rpc.server.worker

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import org.bohan.rpc.server.provider.ServiceProvider
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.reflect.InvocationTargetException
import java.net.Socket

@Slf4j
class WorkThread(
    private val socket: Socket,
    private val serviceProvider: ServiceProvider
): Runnable {

    override fun run() {
        try {
            // 模拟数据库查询和网络时间
            Thread.sleep(30)
            val oos = ObjectOutputStream(socket.getOutputStream())
            val ois = ObjectInputStream(socket.getInputStream())

            // 读取客户端传过来的 RpcRequest
            val rpcRequest = ois.readObject() as RpcRequest
            log.info("服务端已读取客户端请求，请求内容为 $rpcRequest")

            // 反射调用服务方法获取返回值
            val rpcResponse = ThreadUtil.getResponse(serviceProvider, rpcRequest)

            // 向客户端写入 RpcResponse
            oos.writeObject(rpcResponse)
            oos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

}