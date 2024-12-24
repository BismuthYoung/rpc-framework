package org.bohan.rpc.client

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse
import java.io.IOException

import java.io.ObjectInputStream

import java.io.ObjectOutputStream
import java.net.Socket

@Slf4j
class IOClient {

    companion object {
        fun sendRequest(host: String?, port: Int, request: RpcRequest?): RpcResponse<*>? {
            return runCatching {
                Socket(host, port).use { socket ->
                    ObjectOutputStream(socket.getOutputStream()).use { oos ->
                        ObjectInputStream(socket.getInputStream()).use { ois ->
                            oos.writeObject(request)
                            oos.flush()
                            ois.readObject() as RpcResponse<*>
                        }
                    }
                }
            }.getOrElse { e ->
                log.error("客户端接收请求时出现异常", e)
                null
            }
        }
    }

}