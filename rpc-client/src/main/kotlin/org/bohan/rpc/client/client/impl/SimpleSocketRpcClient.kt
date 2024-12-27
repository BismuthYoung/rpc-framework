package org.bohan.rpc.client.client.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.client.client.RpcClient
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

@Slf4j
class SimpleSocketRpcClient(
    private val host: String,
    private val port: Int
): RpcClient {
    override fun sendRequest(request: RpcRequest?): RpcResponse<*>? {
        log.info("[rpc][客户端] 进入请求发送服务")
        if (request == null) {
            throw IllegalArgumentException("待发送的请求不得为空")
        }
        try {
            val socket = Socket(host, port)
            val oos = ObjectOutputStream(socket.getOutputStream())
            val ois = ObjectInputStream(socket.getInputStream())

            oos.writeObject(request)
            oos.flush()

            return ois.readObject() as RpcResponse<*>
        } catch (e: IOException) {
            log.error("连接异常", e)
            return null
        } catch (e: ClassNotFoundException) {
            log.error("无法找到类", e)
            return null
        }
    }
}