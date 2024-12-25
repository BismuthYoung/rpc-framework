package org.bohan.rpc.client.client.impl

import org.bohan.component.common.log.Slf4j
import org.bohan.component.common.log.Slf4j.Companion.log
import org.bohan.rpc.contract.domain.req.RpcRequest
import org.bohan.rpc.contract.domain.resp.RpcResponse

import java.io.ObjectInputStream

import java.io.ObjectOutputStream
import java.net.Socket

@Slf4j
@Deprecated("该类已弃用，请使用 SimpleSocketRpcClient 替代其功能")
class IOClient {

    companion object {
        fun sendRequest(host: String?, port: Int, request: RpcRequest?): RpcResponse<*>? {
            log.info("[rpc][客户端] 进入请求发送服务")
            return runCatching {
                Socket(host, port).use { socket ->
                    log.debug("[rpc][客户端] socket 已打开")
                    ObjectOutputStream(socket.getOutputStream()).use { oos ->
                        log.debug("[rpc][客户端] 输出流 oos 已打开")
                        ObjectInputStream(socket.getInputStream()).use { ois ->
                            log.debug("[rpc][客户端] 输入流 ois 已打开，客户端发送的请求内容为{}", request)
                            oos.writeObject(request)
                            log.debug("[rpc][客户端] 请求已写入对象流，准备调用 flush...")
                            oos.flush()
                            log.debug("[rpc][客户端] 请求发送完成，等待响应...")
                            ois.readObject() as RpcResponse<*>
                        }
                    }
                }
            }.getOrElse { e ->
                log.error("[rpc][客户端] 客户端请求失败：host={}, port={}, request={}", host, port, request, e)
                null
            }
        }
    }

}