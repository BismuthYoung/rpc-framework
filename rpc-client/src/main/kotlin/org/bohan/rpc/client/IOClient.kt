package org.bohan.rpc.client

import org.bohan.rpc.client.domain.req.RpcRequest
import org.bohan.rpc.client.domain.resp.RpcResponse
import java.io.IOException

import java.io.ObjectInputStream

import java.io.ObjectOutputStream
import java.net.Socket


class IOClient {

    companion object {
        //这里负责底层与服务端的通信，发送request，返回response
        fun sendRequest(host: String?, port: Int, request: RpcRequest?): RpcResponse<*>? {
            return try {
                val socket = Socket(host, port)
                val oos = ObjectOutputStream(socket.getOutputStream())
                val ois = ObjectInputStream(socket.getInputStream())
                oos.writeObject(request)
                oos.flush()
                ois.readObject() as RpcResponse<*>
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                null
            }
        }
    }

}